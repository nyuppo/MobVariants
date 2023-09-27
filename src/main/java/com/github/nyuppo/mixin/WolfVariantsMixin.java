package com.github.nyuppo.mixin;

import com.github.nyuppo.config.VariantBlacklist;
import com.github.nyuppo.config.VariantSettings;
import com.github.nyuppo.config.VariantWeights;
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

import java.util.UUID;

@Mixin(WolfEntity.class)
public class WolfVariantsMixin extends MobEntityVariantsMixin {
    private static final TrackedData<String> VARIANT_ID =
            DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onInitDataTracker(CallbackInfo ci) {
        ((WolfEntity)(Object)this).getDataTracker().startTracking(VARIANT_ID, "default");
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
        String variant = this.getRandomVariant(world.getRandom());
        ((WolfEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle the NBT storage change from 1.2.0 -> 1.2.1 that could result in empty variant id
        if (((WolfEntity)(Object)this).getDataTracker().get(VARIANT_ID).isEmpty()) {
            String variant = this.getRandomVariant(((WolfEntity)(Object)this).getWorld().getRandom());
            ((WolfEntity)(Object)this).getDataTracker().set(VARIANT_ID, variant);
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/WolfEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<WolfEntity> ci) {
        WolfEntity child = (WolfEntity) EntityType.WOLF.create(world);
        if (child != null) {
            UUID uUID = ((WolfEntity)(Object)this).getOwnerUuid();
            if (uUID != null) {
                child.setOwnerUuid(uUID);
                child.setTamed(true);
            }
        }

        String variant = "default";
        if (entity.getRandom().nextInt(10) != 0) {
            // Make child inherit parent's variants
            NbtCompound thisNbt = new NbtCompound();
            ((WolfEntity)(Object)this).writeNbt(thisNbt);
            NbtCompound parentNbt = new NbtCompound();
            entity.writeNbt(parentNbt);

            if (thisNbt.contains("Variant") && parentNbt.contains("Variant")) {
                String thisVariant = thisNbt.getString("Variant");
                String parentVariant = parentNbt.getString("Variant");

                if (thisVariant.equals(parentVariant)) {
                    // If both parents are the same variant, just pick that one
                    variant = thisVariant;
                } else {
                    // Handle breeding
                    boolean hasBred = false;

                    if ((thisVariant.equals("husky") && parentVariant.equals("jupiter")) || (thisVariant.equals("jupiter") && parentVariant.equals("husky"))) { // German shepherd
                        if (entity.getRandom().nextFloat() < VariantSettings.getWolfBreedingChance() && !VariantBlacklist.isBlacklisted("wolf", "german_shepherd")) {
                            hasBred = true;
                            variant = "german_shepherd";
                        }
                    } else if ((thisVariant.equals("jupiter") && parentVariant.equals("default")) || (thisVariant.equals("default") && parentVariant.equals("jupiter"))) { // Golden retriever
                        if (entity.getRandom().nextFloat() < VariantSettings.getWolfBreedingChance() && !VariantBlacklist.isBlacklisted("wolf", "golden_retriever")) {
                            hasBred = true;
                            variant = "golden_retriever";
                        }
                    } else if ((thisVariant.equals("husky") && parentVariant.equals("golden_retriever")) || (thisVariant.equals("golden_retriever") && parentVariant.equals("husky"))) { // French bulldog
                        if (entity.getRandom().nextFloat() < VariantSettings.getWolfBreedingChance() && !VariantBlacklist.isBlacklisted("wolf", "french_bulldog")) {
                            hasBred = true;
                            variant = "french_bulldog";
                        }
                    }

                    // Otherwise, pick a random parent's variant
                    if (!hasBred) {
                        variant = entity.getRandom().nextBoolean() ? thisVariant : parentVariant;
                    }
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
        return VariantWeights.getRandomVariant("wolf", random);
    }
}
