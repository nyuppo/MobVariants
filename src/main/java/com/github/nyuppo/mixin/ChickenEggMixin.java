package com.github.nyuppo.mixin;

import com.github.nyuppo.config.VariantBlacklist;
import com.github.nyuppo.config.VariantWeights;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BiomeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.util.math.random.Random;

@Mixin(EggEntity.class)
public class ChickenEggMixin {
    @ModifyVariable(
            method = "onCollision(Lnet/minecraft/util/hit/HitResult;)V",
            at = @At("STORE")
    )
    private ChickenEntity mixin(ChickenEntity chickenEntity) {
        String variant = this.getRandomVariant(chickenEntity.getRandom());

        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (chickenEntity.getWorld().getBiome(chickenEntity.getBlockPos()).isIn(BiomeTags.IS_NETHER) && chickenEntity.getRandom().nextInt(6) == 0) {
                variant = "bone";
            }
        }

        NbtCompound newNbt = new NbtCompound();
        chickenEntity.writeNbt(newNbt);
        newNbt.putString("Variant", variant);
        chickenEntity.readCustomDataFromNbt(newNbt);

        return chickenEntity;
    }

    public String getRandomVariant(Random random) {
        return VariantWeights.getRandomVariant("chicken", random);
    }
}
