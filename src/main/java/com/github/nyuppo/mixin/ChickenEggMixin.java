package com.github.nyuppo.mixin;

import net.minecraft.entity.EntityType;
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

        if (chickenEntity.world.getBiome(chickenEntity.getBlockPos()).isIn(BiomeTags.IS_NETHER) && chickenEntity.getRandom().nextInt(6) == 0) {
            i = 7;
        }

        NbtCompound newNbt = new NbtCompound();
        chickenEntity.writeNbt(newNbt);
        newNbt.putInt("Variant", i);
        chickenEntity.readCustomDataFromNbt(newNbt);

        return chickenEntity;
    }

    private int getRandomVariant(Random random) {
        int i = random.nextInt(14);
        if (i == 0) {
            // Ayam Cemani
            return 6;
        } else if (i > 0 && i <= 2) {
            // Golden
            return 1;
        } else if (i > 2 && i <= 4) {
            // Gold Crested
            return 2;
        } else if (i > 4 && i <= 6) {
            // Welsummer
            return 3;
        } else if (i > 6 && i <= 8) {
            // Cochin
            return 4;
        } else if (i > 8 && i <= 10) {
            // Bantam
            return 5;
        }
        // Default
        return 0;
    }
}
