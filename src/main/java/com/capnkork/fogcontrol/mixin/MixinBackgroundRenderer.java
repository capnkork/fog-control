package com.capnkork.fogcontrol.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import org.objectweb.asm.Opcodes;

import net.minecraft.client.render.BackgroundRenderer;

import com.capnkork.fogcontrol.config.FogControlConfig;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
    private static final float SMALL = 0.5F;
    private static final float LARGE = 100;

    @ModifyConstant(
        slice = @Slice(
            from = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 7)
        ),
        constant = @Constant(floatValue = 192.0F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyNetherFogMaxDistance(float d) {
        return FogControlConfig.getInstance().getNetherFogMaxDistance();
    }

    @ModifyConstant(
        slice = @Slice(
            from = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 7)
        ),
        constant = @Constant(floatValue = 0.05F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyNetherFogStartMultiplier(float m) {
        FogControlConfig config = FogControlConfig.getInstance();
        return config.isNetherFogEnabled() ? config.getNetherFogStartMultiplier() : LARGE;
    }

    @ModifyConstant(
        slice = @Slice(
            from = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 7)
        ),
        constant = @Constant(floatValue = 0.5F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyNetherFogEndMultiplier(float m) {
        FogControlConfig config = FogControlConfig.getInstance();
        return config.isNetherFogEnabled() ? config.getNetherFogEndMultiplier() : LARGE;
    }

    @ModifyConstant(
        slice = @Slice(
            from = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 9)
        ),
        constant = @Constant(floatValue = 0.75F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyOverworldFogStartMultiplier(float m) {
        FogControlConfig config = FogControlConfig.getInstance();
        return config.isOverworldFogEnabled() ? config.getOverworldFogStartMultiplier() : LARGE;
    }

    @ModifyVariable(
        at = @At(value = "STORE", ordinal = 9),
        index = 7,
        method = "applyFog"
    )
    private static float applyOverworldFogEndMultiplier(float ab) {
        FogControlConfig config = FogControlConfig.getInstance();
        return ab * (config.isOverworldFogEnabled() ? config.getOverworldFogEndMultiplier() : LARGE);
    }

    @ModifyArg(
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V", ordinal = 1),
        index = 0,
        remap = false,
        method = "applyFog"
    )
    private static float fogEndFix(float f) {
        // Sodium's renderer has some issues when fog end is less than or equal to fog start, so this prevents that
        return Math.max(RenderSystem.getShaderFogStart() + SMALL, f);
    }
}
