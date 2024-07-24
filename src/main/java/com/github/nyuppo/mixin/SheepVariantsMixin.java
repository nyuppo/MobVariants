package com.github.nyuppo.mixin;

import com.github.nyuppo.MoreMobVariants;
import com.github.nyuppo.config.SheepHornSettings;
import com.github.nyuppo.config.Variants;
import com.github.nyuppo.networking.MMVNetworkingConstants;
import com.github.nyuppo.variant.MobVariant;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
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

@Mixin(SheepEntity.class)
public abstract class SheepVariantsMixin extends MobEntityVariantsMixin {
    private MobVariant variant = Variants.getDefaultVariant(EntityType.SHEEP);
    private String hornColour = "";

    @Override
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
        nbt.putString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY, hornColour);
    }

    @Override
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.getString(MoreMobVariants.NBT_KEY).isEmpty()) {
            if (nbt.getString(MoreMobVariants.NBT_KEY).contains(":")) {
                variant = Variants.getVariant(EntityType.SHEEP, new Identifier(nbt.getString(MoreMobVariants.NBT_KEY)));
            } else {
                variant = Variants.getVariant(EntityType.SHEEP, MoreMobVariants.id(nbt.getString(MoreMobVariants.NBT_KEY)));
            }
        } else {
            variant = Variants.getRandomVariant(EntityType.SHEEP, ((SheepEntity)(Object)this).getRandom().nextLong(), ((SheepEntity)(Object)this).getWorld().getBiome(((SheepEntity)(Object)this).getBlockPos()), null, ((SheepEntity)(Object)this).getWorld().getMoonSize());
        }
        hornColour = nbt.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY);

        // Update all players in the event that this is from modifying entity data with a command
        // This should be fine since the packet is so small anyways
        MinecraftServer server = ((Entity)(Object)this).getServer();
        if (server != null) {
            server.getPlayerManager().getPlayerList().forEach((player) -> {
                PacketByteBuf updateBuf = PacketByteBufs.create();
                updateBuf.writeInt(((Entity)(Object)this).getId());
                updateBuf.writeString(variant.getIdentifier().toString());
                //all three values in the "regular" packet post update
                updateBuf.writeBoolean(false);
                updateBuf.writeVarInt(0);
                updateBuf.writeString(hornColour);

                ServerPlayNetworking.send(player, MMVNetworkingConstants.SERVER_RESPOND_VARIANT_ID, updateBuf);
            });
        }
    }

    @Override
    protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
        variant = Variants.getRandomVariant(EntityType.SHEEP, ((SheepEntity)(Object)this).getRandom().nextLong(), world.getBiome(((SheepEntity)(Object)this).getBlockPos()), null, world.getMoonSize());

        SheepHornSettings.SheepHornColour colour = SheepHornSettings.getRandomSheepHornColour(((SheepEntity)(Object)this).getRandom(), world.getBiome(((SheepEntity)(Object)this).getBlockPos()));
        if (colour != null) {
            hornColour = colour.getId();
        }
    }

    @Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/SheepEntity;",
            at = @At("RETURN")
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<SheepEntity> ci) {
        SheepEntity child = ci.getReturnValue();

        MobVariant variant = Variants.getChildVariant(EntityType.SHEEP, world, ((SheepEntity)(Object)this), entity);

        // Determine horn colour
        NbtCompound nbtParent1 = new NbtCompound();
        ((SheepEntity)(Object)this).writeCustomDataToNbt(nbtParent1);
        NbtCompound nbtParent2 = new NbtCompound();
        entity.writeCustomDataToNbt(nbtParent2);

        String colour = "";
        if (nbtParent1.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)
                && !nbtParent1.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()
                && nbtParent2.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)
                && !nbtParent2.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()
                && entity.getRandom().nextDouble() <= SheepHornSettings.getInheritChance()) {
            colour = entity.getRandom().nextBoolean() ? nbtParent1.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY) : nbtParent2.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY);
        } else {
            SheepHornSettings.SheepHornColour col = SheepHornSettings.getRandomSheepHornColour(entity.getRandom(), world.getBiome(((SheepEntity)(Object)this).getBlockPos()));
            if (col != null) {
                colour = col.getId();
            }
        }

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
        childNbt.putString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY, colour);
        child.readCustomDataFromNbt(childNbt);
    }
}