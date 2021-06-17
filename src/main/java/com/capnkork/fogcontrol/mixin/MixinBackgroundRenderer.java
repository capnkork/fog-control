package com.capnkork.fogcontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

import org.objectweb.asm.Opcodes;

import net.minecraft.client.render.BackgroundRenderer;

import com.capnkork.fogcontrol.config.FogControlConfig;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
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
        constant = @Constant(floatValue = 0.5F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyNetherFogMultiplier(float m) {
        return FogControlConfig.getInstance().getNetherFogMultiplier();
    }

    @ModifyConstant(
        slice = @Slice(
            from = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 7)
        ),
        constant = @Constant(floatValue = 0.75F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyOverworldFogMultiplier(float ab) {
        return FogControlConfig.getInstance().getOverworldFogMultiplier();
    }
}
