package net.mitask.riptidefix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class TridentFixMixin {
    @Shadow protected int riptideTicks;
    @Shadow public abstract boolean shouldSwimInFluids();
    @Shadow protected abstract float getBaseWaterMovementSpeedMultiplier();

    @WrapOperation(method = "travelInWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d riptideFix_travelInWater(Vec3d instance, double x, double y, double z, Operation<Vec3d> original) {
        LivingEntity entity = LivingEntity.class.cast(this);
        FluidState fluidState = entity.getEntityWorld().getFluidState(entity.getBlockPos());
        if (!entity.isTouchingWater() || !shouldSwimInFluids() || entity.canWalkOnFluid(fluidState)) return instance.multiply(x, y, z);

        float f = entity.isSprinting() ? 0.9F : getBaseWaterMovementSpeedMultiplier();
        float h = (float)entity.getAttributeValue(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
        if (!entity.isOnGround()) {
            h *= 0.5F;
        }

        if (h > 0.0f) {
            if (riptideTicks == 0) {
                f += (0.54600006F - f) * h;
            }
        }

        if (entity.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
            f = 0.96F;
        }

        return instance.multiply(f, y, f);
    }
}
