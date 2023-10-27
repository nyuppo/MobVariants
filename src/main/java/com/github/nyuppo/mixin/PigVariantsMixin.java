package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.VariantSettings;
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

@Mixin(PigEntity.class)
public abstract class PigVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    private static final TrackedData<Boolean> MUDDY_ID = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final String MUDDY_NBT_KEY = "IsMuddy";

    private static final TrackedData<Integer> MUDDY_TIMEOUT_ID = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String MUDDY_TIMEOUT_NBT_KEY = "MuddyTimeLeft";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((PigEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, MoreMobVariants.id("default").toString());
        ((PigEntity)(Object)this).getDataTracker().startTracking(MUDDY_ID, false);
        ((PigEntity)(Object)this).getDataTracker().startTracking(MUDDY_TIMEOUT_ID, -1);
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, ((PigEntity)(Object)this).getDataTracker().get(VARIANT_ID));
        nbt.putBoolean(MUDDY_NBT_KEY, ((PigEntity)(Object)this).getDataTracker().get(MUDDY_ID));
        nbt.putInt(MUDDY_TIMEOUT_NBT_KEY, ((PigEntity)(Object)this).getDataTracker().get(MUDDY_TIMEOUT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((PigEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getString(NBT_KEY));
        ((PigEntity)(Object)this).getDataTracker().set(MUDDY_ID, nbt.getBoolean(MUDDY_NBT_KEY));
        ((PigEntity)(Object)this).getDataTracker().set(MUDDY_TIMEOUT_ID, nbt.getInt(MUDDY_TIMEOUT_NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        MobVariant variant = Variants.getRandomVariant(EntityType.PIG, world.getRandom(), world.getBiome(((PigEntity)(Object)this).getBlockPos()), null);
        ((PigEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant.getIdentifier().toString());
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle muddy pigs
        if (VariantSettings.getEnableMuddyPigs()) {
            int muddyPigTimeout = VariantSettings.getMuddyPigTimeout();
            int currentTimeLeft = ((PigEntity)(Object)this).getDataTracker().get(MUDDY_TIMEOUT_ID);

            if (currentTimeLeft == -1) {
                if (((PigEntity)(Object)this).getWorld().getBlockState(((PigEntity)(Object)this).getBlockPos()).isIn(MoreMobVariants.PIG_MUD_BLOCKS) || ((PigEntity)(Object)this).getWorld().getBlockState(((PigEntity)(Object)this).getBlockPos().down()).isIn(MoreMobVariants.PIG_MUD_BLOCKS)) {
                    ((PigEntity)(Object)this).getDataTracker().set(MUDDY_ID, true);
                    if (muddyPigTimeout > 0 ) {
                        ((PigEntity)(Object)this).getDataTracker().set(MUDDY_TIMEOUT_ID, 20 * muddyPigTimeout);
                    }
                } else if (((PigEntity)(Object)this).isTouchingWaterOrRain()) {
                    ((PigEntity)(Object)this).getDataTracker().set(MUDDY_ID, false);
                    ((PigEntity)(Object)this).getDataTracker().set(MUDDY_TIMEOUT_ID, -1);
                }
            }

            if (muddyPigTimeout > 0 && currentTimeLeft > 0) {
                currentTimeLeft--;
                ((PigEntity)(Object)this).getDataTracker().set(MUDDY_TIMEOUT_ID, currentTimeLeft);
                if (currentTimeLeft == 0) {
                    ((PigEntity)(Object)this).getDataTracker().set(MUDDY_ID, false);
                    ((PigEntity)(Object)this).getDataTracker().set(MUDDY_TIMEOUT_ID, -1);
                }
            }
        }

        // Handle mod version upgrades
        if (((PigEntity)(Object)this).getDataTracker().get(VARIANT_ID).isEmpty()) { // 1.2.0 -> 1.2.1 (empty variant id)
            MobVariant variant = Variants.getRandomVariant(EntityType.PIG, ((PigEntity)(Object)this).getWorld().getRandom(), ((PigEntity)(Object)this).getWorld().getBiome(((PigEntity)(Object)this).getBlockPos()), null);
            ((PigEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant.getIdentifier().toString());
        } else if (!((PigEntity)(Object)this).getDataTracker().get(VARIANT_ID).contains(":")) { //  1.2.1 -> 1.3.0 (un-namespaced id)
            ((PigEntity)(Object)this).getDataTracker().set(VARIANT_ID, MoreMobVariants.id(((PigEntity)(Object)this).getDataTracker().get(VARIANT_ID)).toString());
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/PigEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<PigEntity> ci) {
        PigEntity child = (PigEntity)EntityType.PIG.create(world);

        MobVariant variant = Variants.getChildVariant(EntityType.PIG, world, ((PigEntity)(Object)this), entity);

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString("Variant", variant.getIdentifier().toString());
        child.readCustomDataFromNbt(childNbt);

        ci.setReturnValue(child);
    }
}
