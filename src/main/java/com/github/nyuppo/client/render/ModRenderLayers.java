package com.github.nyuppo.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ModRenderLayers extends RenderLayer {
    private static final Function<Identifier, RenderLayer> CUSTOM_EYES = Util.memoize(texture -> {
        MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder().program(ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM).texture(new RenderPhase.Texture((Identifier)texture, false, false)).transparency(ADDITIVE_TRANSPARENCY).cull(DISABLE_CULLING).writeMaskState(COLOR_MASK).overlay(ENABLE_OVERLAY_COLOR).build(false);
        return RenderLayer.of("custom_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters);
    });

    public static RenderLayer getEyes(Identifier texture) {
        return CUSTOM_EYES.apply(texture);
    }

    public ModRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}
