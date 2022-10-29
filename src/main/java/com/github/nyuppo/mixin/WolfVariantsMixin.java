package com.github.nyuppo.mixin;

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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public class WolfVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<Integer> VARIANT_ID =
            DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String NBT_KEY = "Variant";
    // 0 = default
    // 1 = jupiter
    // 2 = husky
    // 3 = german shepherd
    // 4 = golden retriever
    // 5 = french bulldog

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((WolfEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, 0);
    }

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt(NBT_KEY, ((WolfEntity)(Object)this).getDataTracker().get(VARIANT_ID));
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((WolfEntity)(Object)this).getDataTracker().set(VARIANT_ID, nbt.getInt(NBT_KEY));
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        int i = this.getRandomVariant(world.getRandom());
        ((WolfEntity)(Object)this).getDataTracker().set(VARIANT_ID, i);
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/WolfEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<WolfEntity> ci) {
        WolfEntity child = (WolfEntity) EntityType.WOLF.create(world);

        int i = 0;
        if (entity.getRandom().nextInt(10) != 0) {
            // Make child inherit parent's variants
            NbtCompound thisNbt = new NbtCompound();
            ((WolfEntity)(Object)this).writeNbt(thisNbt);
            NbtCompound parentNbt = new NbtCompound();
            entity.writeNbt(parentNbt);

            if (thisNbt.contains("Variant") && parentNbt.contains("Variant")) {
                int thisVariant = thisNbt.getInt("Variant");
                int parentVariant = parentNbt.getInt("Variant");

                if (thisVariant == parentVariant) {
                    // If both parents are the same variant, just pick that one
                    i = thisVariant;
                } else {
                    // Handle breeding
                    boolean hasBred = false;

                    if ((thisVariant == 2 && parentVariant == 1) || (thisVariant == 1 && parentVariant == 2)) { // German shepherd
                        if (entity.getRandom().nextInt(3) == 0) {
                            hasBred = true;
                            i = 3;
                        }
                    } else if ((thisVariant == 1 && parentVariant == 0) || (thisVariant == 0 && parentVariant == 1)) { // Golden retriever
                        if (entity.getRandom().nextInt(3) == 0) {
                            hasBred = true;
                            i = 4;
                        }
                    } else if ((thisVariant == 2 && parentVariant == 4) || (thisVariant == 4 && parentVariant == 2)) { // French bulldog
                        if (entity.getRandom().nextInt(3) == 0) {
                            hasBred = true;
                            i = 5;
                        }
                    }

                    // Otherwise, pick a random parent's variant
                    if (!hasBred) {
                        i = entity.getRandom().nextBoolean() ? thisVariant : parentVariant;
                    }
                }
            }
        } else {
            // Give child random variant
            i = this.getRandomVariant(entity.getRandom());
        }

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putInt("Variant", i);
        child.readCustomDataFromNbt(childNbt);

        ci.setReturnValue(child);
    }

    private int getRandomVariant(Random random) {
        int i = random.nextInt(3);

        if (i == 0) {
            // Jupiter
            return 1;
        } else if (i == 1) {
            // Husky
            return 2;
        }

        // Default
        return 0;
    }
}
