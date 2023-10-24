package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(WolfEntity.class)
public class WolfVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((WolfEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, MoreMobVariants.id("default").toString());
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, ((WolfEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((WolfEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getString(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        MobVariant variant = Variants.getRandomVariant(Variants.Mob.WOLF, world.getRandom(), world.getBiome(((WolfEntity)(Object)this).getBlockPos()), null);
        ((WolfEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant.getIdentifier().toString());
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle mod version upgrades
        if (((WolfEntity)(Object)this).getDataTracker().get(VARIANT_ID).isEmpty()) { // 1.2.0 -> 1.2.1 (empty variant id)
            MobVariant variant = Variants.getRandomVariant(Variants.Mob.WOLF, ((WolfEntity)(Object)this).getWorld().getRandom(), ((WolfEntity)(Object)this).getWorld().getBiome(((WolfEntity)(Object)this).getBlockPos()), null);
            ((WolfEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant.getIdentifier().toString());
        } else if (!((WolfEntity)(Object)this).getDataTracker().get(VARIANT_ID).contains(":")) { //  1.2.1 -> 1.3.0 (un-namespaced id)
            ((WolfEntity)(Object)this).getDataTracker().set(VARIANT_ID, MoreMobVariants.id(((WolfEntity)(Object)this).getDataTracker().get(VARIANT_ID)).toString());
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/WolfEntity;",
            at = @At("RETURN")
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<WolfEntity> ci) {
        WolfEntity child = ci.getReturnValue();

        MobVariant variant = Variants.getChildVariant(Variants.Mob.WOLF, world, ((WolfEntity)(Object)this), entity);

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString("Variant", variant.getIdentifier().toString());
        child.readCustomDataFromNbt(childNbt);
    }
}
