package net.mitask.riptidefix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class TridentFixMixin {
    @Shadow protected int autoSpinAttackTicks;

    @WrapOperation(method = "travelInWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double riptideFix$ignoreDepthStriderDuringRiptide(LivingEntity instance, Holder<Attribute> attribute, Operation<Double> original) {
        return autoSpinAttackTicks > 0 && attribute == Attributes.WATER_MOVEMENT_EFFICIENCY ? 0.0 : original.call(instance, attribute);
    }

    @Inject(method = "travelInWater", at = @At("TAIL"))
    private void riptideFix$syncRiptideVelocityToClient(Vec3 input, double baseGravity, boolean isFalling, double oldY, CallbackInfo ci) {
        if (autoSpinAttackTicks == 20 && (LivingEntity)(Object)this instanceof ServerPlayer player && player.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY) > 0.0) {
            player.hurtMarked = true;
        }
    }
}
