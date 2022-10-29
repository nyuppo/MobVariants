package com.github.nyuppo.mixin;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(OcelotEntityModel.class)
public class CatFluffMixin {
    @Inject(method = "getModelData", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onGetModelData(Dilation dilation, CallbackInfoReturnable<ModelData> ci, ModelData modelData, ModelPartData modelPartData) {
        modelPartData.addChild(EntityModelPartNames.HEAD,
                ModelPartBuilder.create()
                        .cuboid("main", -2.5f, -2.0f, -3.0f, 5.0f, 4.0f, 5.0f, dilation)
                        .cuboid(EntityModelPartNames.NOSE, -1.5f, 0.0f, -4.0f, 3, 2, 2, dilation, 0, 24)
                        .cuboid("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, dilation, 0, 10)
                        .cuboid("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, dilation, 6, 10)
                        .cuboid("scruff1", -4.5f, -2.0f, 1.0f, 9, 4, 0, dilation, 40, 28)
                        .cuboid("scruff2", -4.5f, -2.0f, -1.0f, 9, 4, 0, dilation, 40, 28),
                ModelTransform.pivot(0.0f, 15.0f, -9.0f));

        ModelPartBuilder modelpartBuilder3 = ModelPartBuilder.create().uv(20, 0).cuboid(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, dilation)
                .uv(32, 12).cuboid(-4.0f, 6.0f, -9.0f, 8.0f, 0.0f, 8.0f, dilation)
                .uv(32, 20).cuboid(-4.0f, 8.0f, -9.0f, 8.0f, 0.0f, 8.0f, dilation)
                .uv(32, 12).cuboid(-4.0f, 10.0f, -9.0f, 8.0f, 0.0f, 8.0f, dilation)
                .uv(32, 20).cuboid(-4.0f, 12.0f, -9.0f, 8.0f, 0.0f, 8.0f, dilation)
                .uv(32, 12).cuboid(-4.0f, 14.0f, -9.0f, 8.0f, 0.0f, 8.0f, dilation)
                .uv(32, 20).cuboid(-4.0f, 16.0f, -9.0f, 8.0f, 0.0f, 8.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.BODY, modelpartBuilder3, ModelTransform.of(0.0f, 12.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
    }
}
