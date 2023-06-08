package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.VariantSettings;
import com.github.nyuppo.config.VariantWeights;
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
import net.minecraft.util.math.random.Random;

@Mixin(PigEntity.class)
public abstract class PigVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    private static final TrackedData<Boolean> MUDDY_ID = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final String MUDDY_NBT_KEY = "IsMuddy";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((PigEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, "default");
        ((PigEntity)(Object)this).getDataTracker().startTracking(MUDDY_ID, false);
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, ((PigEntity)(Object)this).getDataTracker().get(VARIANT_ID));
        nbt.putBoolean(MUDDY_NBT_KEY, ((PigEntity)(Object)this).getDataTracker().get(MUDDY_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((PigEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getString(NBT_KEY));
        ((PigEntity)(Object)this).getDataTracker().set(MUDDY_ID, nbt.getBoolean(MUDDY_NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        String variant = this.getRandomVariant(world.getRandom());
        ((PigEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle muddy pigs
        if (VariantSettings.getEnableMuddyPigs()) {
            if (((PigEntity)(Object)this).getWorld().getBlockState(((PigEntity)(Object)this).getBlockPos()).isIn(MoreMobVariants.PIG_MUD_BLOCKS) || ((PigEntity)(Object)this).getWorld().getBlockState(((PigEntity)(Object)this).getBlockPos().down()).isIn(MoreMobVariants.PIG_MUD_BLOCKS)) {
                ((PigEntity)(Object)this).getDataTracker().set(MUDDY_ID, true);
            } else if (((PigEntity)(Object)this).isTouchingWaterOrRain()) {
                ((PigEntity)(Object)this).getDataTracker().set(MUDDY_ID, false);
            }
        }

        // Handle the NBT storage change from 1.2.0 -> 1.2.1 that could result in empty variant id
        if (((PigEntity)(Object)this).getDataTracker().get(VARIANT_ID).isEmpty()) {
            String variant = this.getRandomVariant(((PigEntity)(Object)this).getWorld().getRandom());
            ((PigEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/PigEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<PigEntity> ci) {
        PigEntity child = (PigEntity)EntityType.PIG.create(world);

        String variant = "default";
        if (entity.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            NbtCompound thisNbt = new NbtCompound();
            ((PigEntity)(Object)this).writeNbt(thisNbt);
            NbtCompound parentNbt = new NbtCompound();
            entity.writeNbt(parentNbt);

            if (thisNbt.contains("Variant") && parentNbt.contains("Variant")) {
                String thisVariant = thisNbt.getString("Variant");
                String parentVariant = parentNbt.getString("Variant");

                if (thisVariant.equals("parentVariant")) {
                    // If both parents are the same variant, just pick that one
                    variant = thisVariant;
                } else {
                    // Otherwise, pick a random parent's variant
                    variant = entity.getRandom().nextBoolean() ? thisVariant : parentVariant;
                }
            }
        } else {
            // Give child random variant
            variant = this.getRandomVariant(entity.getRandom());
        }

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString("Variant", variant);
        child.readCustomDataFromNbt(childNbt);

        ci.setReturnValue(child);
    }

    private String getRandomVariant(Random random) {
        return VariantWeights.getRandomVariant("pig", random);
    }
}
