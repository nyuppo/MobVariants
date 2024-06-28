package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.VariantSettings;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.networking.MMVNetworkingConstants;
import com.github.nyuppo.variant.MobVariant;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.MinecraftServer;
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

@Mixin(PigEntity.class)
public abstract class PigVariantsMixin extends MobEntityVariantsMixin {
    private MobVariant variant = Variants.getDefaultVariant(EntityType.COW);
    private boolean isMuddy = false;
    private int muddyTimeLeft = -1;

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
        nbt.putBoolean(MoreMobVariants.MUDDY_NBT_KEY, isMuddy);
        nbt.putInt(MoreMobVariants.MUDDY_TIMEOUT_NBT_KEY, muddyTimeLeft);
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.getString(MoreMobVariants.NBT_KEY).isEmpty()) {
            if (nbt.getString(MoreMobVariants.NBT_KEY).contains(":")) {
                variant = Variants.getVariant(EntityType.PIG, new Identifier(nbt.getString(MoreMobVariants.NBT_KEY)));
            } else {
                variant = Variants.getVariant(EntityType.PIG, MoreMobVariants.id(nbt.getString(MoreMobVariants.NBT_KEY)));
            }
        } else {
            variant = Variants.getRandomVariant(EntityType.PIG, ((PigEntity)(Object)this).getRandom().nextLong(), ((PigEntity)(Object)this).getWorld().getBiome(((PigEntity)(Object)this).getBlockPos()), null, ((PigEntity)(Object)this).getWorld().getMoonSize());
        }
        isMuddy = nbt.getBoolean(MoreMobVariants.MUDDY_NBT_KEY);
        muddyTimeLeft = nbt.getInt(MoreMobVariants.MUDDY_TIMEOUT_NBT_KEY);

        // Update all players in the event that this is from modifying entity data with a command
        // This should be fine since the packet is so small anyways
        MinecraftServer server = ((Entity)(Object)this).getServer();
        if (server != null) {
            server.getPlayerManager().getPlayerList().forEach((player) -> {
                PacketByteBuf updateBuf = PacketByteBufs.create();
                updateBuf.writeInt(((Entity)(Object)this).getId());
                updateBuf.writeString(variant.getIdentifier().toString());
                //all three values in the "regular" packet post update
                updateBuf.writeBoolean(isMuddy);
                updateBuf.writeVarInt(muddyTimeLeft);
                updateBuf.writeString("");

                ServerPlayNetworking.send(player, MMVNetworkingConstants.SERVER_RESPOND_VARIANT_ID, updateBuf);
            });
        }
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        variant = Variants.getRandomVariant(EntityType.PIG, ((PigEntity)(Object)this).getRandom().nextLong(), world.getBiome(((PigEntity)(Object)this).getBlockPos()), null, world.getMoonSize());

        // 2% chance of pig starting muddy if in swamp
        if (world.getBiome(((PigEntity)(Object)this).getBlockPos()).isIn(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE) && ((PigEntity)(Object)this).getRandom().nextDouble() < 0.02) {
            isMuddy = true;
        }
    }

    @Override
    protected void onTick(CallbackInfo ci) {
        // Handle muddy pigs
        if (VariantSettings.getEnableMuddyPigs()) {
            int muddyPigTimeout = VariantSettings.getMuddyPigTimeout();

            if (muddyTimeLeft == -1) {
                if (((PigEntity)(Object)this).getWorld().getBlockState(((PigEntity)(Object)this).getBlockPos()).isIn(MoreMobVariants.PIG_MUD_BLOCKS) || ((PigEntity)(Object)this).getWorld().getBlockState(((PigEntity)(Object)this).getBlockPos().down()).isIn(MoreMobVariants.PIG_MUD_BLOCKS)) {
                    isMuddy = true;
                    if (muddyPigTimeout > 0 ) {
                        muddyTimeLeft = 20 * muddyPigTimeout;
                    }
                } else if (((PigEntity)(Object)this).isTouchingWaterOrRain()) {
                    isMuddy = false;
                    muddyTimeLeft = -1;
                }
            }

            if (muddyPigTimeout > 0 && muddyTimeLeft > 0) {
                muddyTimeLeft--;
                if (muddyTimeLeft == 0) {
                    isMuddy = false;
                    muddyTimeLeft = -1;
                }
            }
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/PigEntity;",
            at = @At("RETURN")
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<PigEntity> ci) {
        PigEntity child = ci.getReturnValue();

        MobVariant variant = Variants.getChildVariant(EntityType.PIG, world, ((PigEntity)(Object)this), entity);

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
        child.readCustomDataFromNbt(childNbt);
    }
}