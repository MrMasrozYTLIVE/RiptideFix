package net.mitask.riptidefix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class TridentFixMixin {
    @Shadow protected int autoSpinAttackTicks;
    @Shadow public abstract boolean isAffectedByFluids();
    @Shadow protected abstract float getWaterSlowDown();

    @WrapOperation(method = "travelInWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 riptideFix_travelInWater(Vec3 instance, double x, double y, double z, Operation<Vec3> original) {
        LivingEntity entity = LivingEntity.class.cast(this);
        FluidState fluidState = entity.level().getFluidState(entity.blockPosition());
        if (!entity.isInWater() || !isAffectedByFluids() || entity.canStandOnFluid(fluidState)) return instance.multiply(x, y, z);

        float f = entity.isSprinting() ? 0.9F : getWaterSlowDown();
        float h = (float)entity.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
        if (!entity.onGround()) {
            h *= 0.5F;
        }

        if (h > 0.0f) {
            if (autoSpinAttackTicks == 0) {
                f += (0.54600006F - f) * h;
            }
        }

        if (entity.hasEffect(MobEffects.DOLPHINS_GRACE)) {
            f = 0.96F;
        }

        return instance.multiply(f, y, f);
    }
}
