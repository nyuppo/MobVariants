package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.variant.MobVariant;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
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
    private MobVariant variant = Variants.getDefaultVariant(EntityType.COW);
    private static final String NBT_KEY = "Variant";

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(NBT_KEY, variant.getIdentifier().toString());
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.getString(NBT_KEY).isEmpty()) {
            if (nbt.getString(NBT_KEY).contains(":")) {
                variant = Variants.getVariant(EntityType.COW, new Identifier(nbt.getString(NBT_KEY)));
            } else {
                variant = Variants.getVariant(EntityType.COW, MoreMobVariants.id(nbt.getString(NBT_KEY)));
            }
        } else {
            variant = Variants.getDefaultVariant(EntityType.COW);
        }
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        variant = Variants.getRandomVariant(EntityType.COW, world.getRandom(), world.getBiome(((CowEntity)(Object)this).getBlockPos()), null);
        NbtCompound nbt = new NbtCompound();
        ((CowEntity)(Object)this).writeNbt(nbt);
        nbt.putString(NBT_KEY, variant.getIdentifier().toString());
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
