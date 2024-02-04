package com.github.nyuppo.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(QuadrupedEntityModel.class)
public interface QuadrupedEntityModelPartAccessor {
    @Accessor("head")
    ModelPart getHead();
}
