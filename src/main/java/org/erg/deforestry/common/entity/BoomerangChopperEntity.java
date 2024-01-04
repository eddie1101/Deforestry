package org.erg.deforestry.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BoomerangChopperEntity extends BoomerangEntity {

    public BoomerangChopperEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang, int slot, double x, double y, double z, float power) {
        super(type, level, owner, boomerang, slot, x, y, z, power);
    }

    public BoomerangChopperEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang, int slot, float power) {
        this(type, level, owner, boomerang, slot, owner.getX(), owner.getEyeY() - 0.5f, owner.getZ(), power);
    }

    public BoomerangChopperEntity(EntityType<? extends BoomerangEntity> type, Level level, double x, double y, double z) {
        this(type, level, null, null, -1, x, y, z, 1.0f);
    }

    public BoomerangChopperEntity(EntityType<? extends BoomerangEntity> entityType, Level level) {
        this(entityType, level, 0.0d, 0.0d, 0.0d);
    }
}
