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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepEntity.class)
public abstract class SheepVariantsMixin extends MobEntityVariantsMixin {
    @Shadow
    public abstract DyeColor getChildColor(AnimalEntity firstParent, AnimalEntity secondParent);

    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(SheepEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((SheepEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, MoreMobVariants.id("default").toString());
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, ((SheepEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((SheepEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getString(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        MobVariant variant = Variants.getRandomVariant(EntityType.SHEEP, world.getRandom(), world.getBiome(((SheepEntity)(Object)this).getBlockPos()), null);
        ((SheepEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant.getIdentifier().toString());
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle mod version upgrades
        if (((SheepEntity)(Object)this).getDataTracker().get(VARIANT_ID).isEmpty()) { // 1.2.0 -> 1.2.1 (empty variant id)
            MobVariant variant = Variants.getRandomVariant(EntityType.SHEEP, ((SheepEntity)(Object)this).getWorld().getRandom(), ((SheepEntity)(Object)this).getWorld().getBiome(((SheepEntity)(Object)this).getBlockPos()), null);
            ((SheepEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant.getIdentifier().toString());
        } else if (!((SheepEntity)(Object)this).getDataTracker().get(VARIANT_ID).contains(":")) { //  1.2.1 -> 1.3.0 (un-namespaced id)
            ((SheepEntity)(Object)this).getDataTracker().set(VARIANT_ID, MoreMobVariants.id(((SheepEntity)(Object)this).getDataTracker().get(VARIANT_ID)).toString());
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/SheepEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<SheepEntity> ci) {
        SheepEntity child = (SheepEntity)EntityType.SHEEP.create(world);
        if (child != null) {
            child.setColor(this.getChildColor(((SheepEntity)(Object)this), (SheepEntity)entity));
        }

        MobVariant variant = Variants.getChildVariant(EntityType.SHEEP, world, ((SheepEntity)(Object)this), entity);

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString("Variant", variant.getIdentifier().toString());
        child.readCustomDataFromNbt(childNbt);

        ci.setReturnValue(child);
    }
}
