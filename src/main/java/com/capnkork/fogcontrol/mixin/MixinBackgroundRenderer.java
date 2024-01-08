package com.capnkork.fogcontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.objectweb.asm.Opcodes;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;

import com.capnkork.fogcontrol.config.FogControlConfig;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
    private static final float LARGE = 100;
    private static float viewDistance;

    @Inject(
        at = @At(
            value = "HEAD"
        ),
        method = "applyFog"
    )
    private static void captureViewDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance,
                                            boolean thickFog, float tickDelta, CallbackInfo ci)
    {
        MixinBackgroundRenderer.viewDistance = viewDistance;
    }

    @Redirect(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/BackgroundRenderer$FogData;fogStart:F",
            opcode = Opcodes.PUTFIELD,
            ordinal = 6
        ),
        method = "applyFog"
    )
    private static void applyNetherFogStartMultiplier(BackgroundRenderer.FogData fogData, float fogStart) {
        FogControlConfig config = FogControlConfig.getInstance();
        if (config.isNetherFogEnabled()) {
            fogData.fogStart = Math.min(viewDistance, config.getNetherFogMaxDistance()) * config.getNetherFogStartMultiplier();
        } else {
            fogData.fogStart = LARGE * viewDistance;
        }
    }

    @Redirect(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/BackgroundRenderer$FogData;fogEnd:F",
            opcode = Opcodes.PUTFIELD,
            ordinal = 9
        ),
        method = "applyFog"
    )
    private static void applyNetherFogEndMultiplier(BackgroundRenderer.FogData fogData, float fogEnd) {
        FogControlConfig config = FogControlConfig.getInstance();
        if (config.isNetherFogEnabled()) {
            fogData.fogEnd = Math.min(viewDistance, config.getNetherFogMaxDistance()) * config.getNetherFogEndMultiplier();
        } else {
            fogData.fogEnd = LARGE * viewDistance;
        }
    }

    @Redirect(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/BackgroundRenderer$FogData;fogStart:F",
            opcode = Opcodes.PUTFIELD,
            ordinal = 8
        ),
        method = "applyFog"
    )
    private static void applyOverworldFogStartMultiplier(BackgroundRenderer.FogData fogData, float fogStart) {
        FogControlConfig config = FogControlConfig.getInstance();
        fogData.fogStart = (config.isOverworldFogEnabled() ? config.getOverworldFogStartMultiplier() : LARGE) * viewDistance;
    }

    @Redirect(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/render/BackgroundRenderer$FogData;fogEnd:F",
            opcode = Opcodes.PUTFIELD,
            ordinal = 11
        ),
        method = "applyFog"
    )
    private static void applyOverworldFogEndMultiplier(BackgroundRenderer.FogData fogData, float fogEnd) {
        FogControlConfig config = FogControlConfig.getInstance();
        fogData.fogEnd = (config.isOverworldFogEnabled() ? config.getOverworldFogEndMultiplier() : LARGE) * viewDistance;
    }
}
