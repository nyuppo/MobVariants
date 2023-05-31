package com.github.nyuppo.mixin;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PigEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PigEntityModel.class)
public class PigFlowerMixin {
    @Inject(method = "getTexturedModelData", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onGetTexturedModelData(Dilation dilation, CallbackInfoReturnable<TexturedModelData> ci, ModelData modelData, ModelPartData modelPartData) {
        modelPartData.addChild(EntityModelPartNames.HEAD,
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, dilation)
                        .uv(16, 16).cuboid(-2.0f, 0.0f, -9.0f, 4.0f, 3.0f, 1.0f, dilation)
                        .uv(28, 3).cuboid(-1.0f, -5.0f, -7.0f, 4.0f, 1.0f, 4.0f, dilation)
                        .uv(44, 2).cuboid(0.0f, -11.0f, -5.0f,4, 6, 0, dilation),
                ModelTransform.pivot(0.0f, 12.0f, -6.0f));
    }
}
