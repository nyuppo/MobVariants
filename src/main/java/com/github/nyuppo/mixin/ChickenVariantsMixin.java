package com.github.nyuppo.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BiomeTags;
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
    private static final TrackedData<Integer> VARIANT_ID =
            DataTracker.registerData(ChickenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String NBT_KEY = "Variant";
    // 0 = default
    // 1 = amber
    // 2 = gold_crested
    // 3 = bronzed
    // 4 = skewbald
    // 5 = stormy
    // 6 = midnight
    // 7 = bone

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((ChickenEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, 0);
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt(NBT_KEY, ((ChickenEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((ChickenEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getInt(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        int i = this.getRandomVariant(world.getRandom());

        // If in nether, random chance of bone chicken
        if (world.getBiome(((ChickenEntity)(Object)this).getBlockPos()).isIn(BiomeTags.IS_NETHER) && world.getRandom().nextInt(6) == 0) {
            i = 7;
        }

        ((ChickenEntity)(Object)this).getDataTracker().set(VARIANT_ID, i);
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/ChickenEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<ChickenEntity> ci) {
        ChickenEntity child = EntityType.CHICKEN.create(world);

        int i = 0;
        if (entity.getRandom().nextInt(4) != 0) {
            // Make child inherit parent's variants
            NbtCompound thisNbt = new NbtCompound();
            ((ChickenEntity)(Object)this).writeNbt(thisNbt);
            NbtCompound parentNbt = new NbtCompound();
            entity.writeNbt(parentNbt);

            if (thisNbt.contains("Variant") && parentNbt.contains("Variant")) {
                int thisVariant = thisNbt.getInt("Variant");
                int parentVariant = parentNbt.getInt("Variant");

                if (thisVariant == parentVariant) {
                    // If both parents are the same variant, just pick that one
                    i = thisVariant;
                } else {
                    // Otherwise, pick a random parent's variant
                    i = ((ChickenEntity)(Object)this).getRandom().nextBoolean() ? thisVariant : parentVariant;
                }
            }
        } else {
            // Give child random variant
            i = this.getRandomVariant(entity.getRandom());
        }

        // If in nether, random chance of bone chicken
        if (world.getBiome(entity.getBlockPos()).isIn(BiomeTags.IS_NETHER) && ((ChickenEntity)(Object)this).getRandom().nextInt(6) == 0) {
            i = 7;
        }

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putInt("Variant", i);
        child.readCustomDataFromNbt(childNbt);

        ci.setReturnValue(child);
    }

    private int getRandomVariant(Random random) {
        int i = random.nextInt(14);
        if (i == 0) {
            // Midnight
            return 6;
        } else if (i > 0 && i <= 2) {
            // Amber
            return 1;
        } else if (i > 2 && i <= 4) {
            // Gold Crested
            return 2;
        } else if (i > 4 && i <= 6) {
            // Bronzed
            return 3;
        } else if (i > 6 && i <= 8) {
            // Skewbald
            return 4;
        } else if (i > 8 && i <= 10) {
            // Stormy
            return 5;
        }
        // Default
        return 0;
    }
}
