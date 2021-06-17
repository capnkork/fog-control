package com.capnkork.fogcontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

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
        constant = @Constant(floatValue = 0.05F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyNetherFogStartMultiplier(float m) {
        return FogControlConfig.getInstance().getNetherFogStartMultiplier();
    }

    @ModifyConstant(
        slice = @Slice(
            from = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 7)
        ),
        constant = @Constant(floatValue = 0.5F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyNetherFogEndMultiplier(float m) {
        return FogControlConfig.getInstance().getNetherFogEndMultiplier();
    }

    @ModifyConstant(
        slice = @Slice(
            from = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 9)
        ),
        constant = @Constant(floatValue = 0.75F, ordinal = 0),
        method = "applyFog"
    )
    private static float applyOverworldFogStartMultiplier(float m) {
        return FogControlConfig.getInstance().getOverworldFogStartMultiplier();
    }

    @ModifyVariable(
        slice = @Slice(
            from = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 9)
        ),
        at = @At("STORE"),
        ordinal = 2,
        method = "applyFog"
    )
    private static float applyOverworldFogEndMultiplier(float ab) {
        return ab * FogControlConfig.getInstance().getOverworldFogEndMultiplier();
    }
}
