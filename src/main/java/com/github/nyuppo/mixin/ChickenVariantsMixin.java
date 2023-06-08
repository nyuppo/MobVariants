package com.github.nyuppo.mixin;

import com.github.nyuppo.config.VariantBlacklist;
import com.github.nyuppo.config.VariantWeights;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BiomeTags;
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

@Mixin(ChickenEntity.class)
public abstract class ChickenVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(ChickenEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((ChickenEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, "default");
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, ((ChickenEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((ChickenEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getString(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        String variant = this.getRandomVariant(world.getRandom());

        // If in nether, random chance of bone chicken
        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (world.getBiome(((ChickenEntity)(Object)this).getBlockPos()).isIn(BiomeTags.IS_NETHER) && world.getRandom().nextInt(6) == 0) {
                variant = "bone";
            }
        }

        ((ChickenEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle the NBT storage change from 1.2.0 -> 1.2.1 that could result in empty variant id
        if (((ChickenEntity)(Object)this).getDataTracker().get(VARIANT_ID).isEmpty()) {
            String variant = this.getRandomVariant(((ChickenEntity)(Object)this).getWorld().getRandom());
            ((ChickenEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/ChickenEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<ChickenEntity> ci) {
        ChickenEntity child = EntityType.CHICKEN.create(world);

        String variant = "default";
        if (entity.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            NbtCompound thisNbt = new NbtCompound();
            ((ChickenEntity)(Object)this).writeNbt(thisNbt);
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
                    variant = ((ChickenEntity)(Object)this).getRandom().nextBoolean() ? thisVariant : parentVariant;
                }
            }
        } else {
            // Give child random variant
            variant = this.getRandomVariant(entity.getRandom());
        }

        // If in nether, random chance of bone chicken
        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (world.getBiome(entity.getBlockPos()).isIn(BiomeTags.IS_NETHER) && ((ChickenEntity)(Object)this).getRandom().nextInt(6) == 0) {
                variant = "bone";
            }
        }

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString("Variant", variant);
        child.readCustomDataFromNbt(childNbt);

        ci.setReturnValue(child);
    }

    public String getRandomVariant(Random random) {
        return VariantWeights.getRandomVariant("chicken", random);
    }
}
