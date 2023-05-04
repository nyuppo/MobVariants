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
        int i = this.getRandomVariant(chickenEntity.getRandom());

        if (!VariantBlacklist.isBlacklisted("chicken", "bone")) {
            if (chickenEntity.world.getBiome(chickenEntity.getBlockPos()).isIn(BiomeTags.IS_NETHER) && chickenEntity.getRandom().nextInt(6) == 0) {
                i = 7;
            }
        }

        NbtCompound newNbt = new NbtCompound();
        chickenEntity.writeNbt(newNbt);
        newNbt.putInt("Variant", i);
        chickenEntity.readCustomDataFromNbt(newNbt);

        return chickenEntity;
    }

    public int getVariantID(String variantName) {
        return switch(variantName) {
            case "amber" -> 1;
            case "gold_crested" -> 2;
            case "bronzed" -> 3;
            case "skewbald" -> 4;
            case "stormy" -> 5;
            case "midnight" -> 6;
            default -> 0;
        };
    }

    public int getRandomVariant(Random random) {
        return getVariantID(VariantWeights.getRandomVariant("chicken", random));
    }
}
