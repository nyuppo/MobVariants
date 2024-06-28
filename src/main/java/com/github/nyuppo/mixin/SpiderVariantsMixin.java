package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.networking.MMVNetworkingConstants;
import com.github.nyuppo.variant.MobVariant;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpiderEntity.class)
public class SpiderVariantsMixin extends MobEntityVariantsMixin {
    private MobVariant variant = Variants.getDefaultVariant(EntityType.SPIDER);

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.getString(MoreMobVariants.NBT_KEY).isEmpty()) {
            if (nbt.getString(MoreMobVariants.NBT_KEY).contains(":")) {
                variant = Variants.getVariant(EntityType.SPIDER, new Identifier(nbt.getString(MoreMobVariants.NBT_KEY)));
            } else {
                variant = Variants.getVariant(EntityType.SPIDER, MoreMobVariants.id(nbt.getString(MoreMobVariants.NBT_KEY)));
            }
        } else {
            variant = Variants.getRandomVariant(EntityType.SPIDER, ((SpiderEntity)(Object)this).getRandom().nextLong(), ((SpiderEntity)(Object)this).getWorld().getBiome(((SpiderEntity)(Object)this).getBlockPos()), null, ((SpiderEntity)(Object)this).getWorld().getMoonSize());
        }

        // Update all players in the event that this is from modifying entity data with a command
        // This should be fine since the packet is so small anyways
        MinecraftServer server = ((Entity)(Object)this).getServer();
        if (server != null) {
            server.getPlayerManager().getPlayerList().forEach((player) -> {
                PacketByteBuf updateBuf = PacketByteBufs.create();
                updateBuf.writeInt(((Entity)(Object)this).getId());
                updateBuf.writeString(variant.getIdentifier().toString());

                ServerPlayNetworking.send(player, MMVNetworkingConstants.SERVER_RESPOND_BASIC_VARIANT_ID, updateBuf);
            });
        }
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        variant = Variants.getRandomVariant(EntityType.SPIDER, ((SpiderEntity)(Object)this).getRandom().nextLong(), world.getBiome(((SpiderEntity)(Object)this).getBlockPos()), null, world.getMoonSize());
    }
}