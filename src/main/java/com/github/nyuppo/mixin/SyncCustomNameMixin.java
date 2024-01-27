package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.networking.MMVNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class SyncCustomNameMixin {
    @Inject(method = "setCustomName", at = @At("RETURN"))
    public void syncVariantAfterCustomName(@Nullable Text name, CallbackInfo ci) {
        MinecraftServer server = ((Entity)(Object)this).getServer();
        if (server != null) {
            server.getPlayerManager().getPlayerList().forEach((player) -> {
                NbtCompound nbt = new NbtCompound();
                ((Entity)(Object)this).writeNbt(nbt);

                if (nbt.contains(MoreMobVariants.NBT_KEY)) {
                    PacketByteBuf responseBuf = PacketByteBufs.create();
                    responseBuf.writeInt(((Entity)(Object)this).getId());
                    responseBuf.writeString(nbt.getString(MoreMobVariants.NBT_KEY));

                    MoreMobVariants.LOGGER.info("sending " + nbt.getString(MoreMobVariants.NBT_KEY) + " to " + player.getName().getString());
                    ServerPlayNetworking.send(player, MMVNetworkingConstants.SERVER_RESPOND_VARIANT_ID, responseBuf);
                }
            });
        }
    }
}
