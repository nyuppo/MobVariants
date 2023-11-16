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
import net.minecraft.entity.passive.*;
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

@Mixin(CowEntity.class)
public abstract class CowVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((CowEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, MoreMobVariants.id("default").toString());
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, ((CowEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getString(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        MobVariant variant = Variants.getRandomVariant(EntityType.COW, world.getRandom(), world.getBiome(((CowEntity)(Object)this).getBlockPos()), null);
        ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant.getIdentifier().toString());
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle mod version upgrades
        if (((CowEntity)(Object)this).getDataTracker().get(VARIANT_ID).isEmpty()) { // 1.2.0 -> 1.2.1 (empty variant id)
            MobVariant variant = Variants.getRandomVariant(EntityType.COW, ((CowEntity)(Object)this).getWorld().getRandom(), ((CowEntity)(Object)this).getWorld().getBiome(((CowEntity)(Object)this).getBlockPos()), null);
            ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant.getIdentifier().toString());
        } else if (!((CowEntity)(Object)this).getDataTracker().get(VARIANT_ID).contains(":")) { //  1.2.1 -> 1.3.0 (un-namespaced id)
            ((CowEntity)(Object)this).getDataTracker().set(VARIANT_ID, MoreMobVariants.id(((CowEntity)(Object)this).getDataTracker().get(VARIANT_ID)).toString());
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/CowEntity;",
            at = @At("RETURN")
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<CowEntity> ci) {
        CowEntity child = ci.getReturnValue();

        MobVariant variant = Variants.getChildVariant(EntityType.COW, world, ((CowEntity)(Object)this), entity);

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString("Variant", variant.getIdentifier().toString());
        child.readCustomDataFromNbt(childNbt);
    }
}
