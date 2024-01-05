package org.erg.deforestry.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.erg.deforestry.Config;
import org.erg.deforestry.common.util.DeforestryUtil;

import java.util.List;

public class BoomerangChopperEntity extends BoomerangEntity {

    public BoomerangChopperEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang, int slot, double x, double y, double z, float power) {
        super(type, level, owner, boomerang, slot, x, y, z, power);
        this.minDamage += 4;
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

    @Override
    public void onHitBlock(BlockHitResult hitResult) {
        Level level = level();
        BlockPos origin = hitResult.getBlockPos();
        BlockState state = level.getBlockState(origin);
        boolean shouldBounce = true;

        if (getOwner() instanceof Player player) {
            if (state.is(BlockTags.LOGS)) {

                if(!level.isClientSide()) {
                    boomerangItemStack.hurtAndBreak(1, player, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));

                    Block logType = state.getBlock();

                    List<BlockPos> logs = DeforestryUtil.getLogsInTree(logType, origin, level);

                    int numLogs = logs.size();
                    int logsToChop = Math.min(numLogs, Config.maxBoomerangChop);

                    for (int i = 0; i < logsToChop; i++) {
                        level.destroyBlock(logs.get(i), true, player);
                        for (BlockPos leaf : DeforestryUtil.getConnectedLeavesAroundLog(logs.get(i), level, logs)) {
                            level.destroyBlock(leaf, true, player);
                        }
                    }
                }
            } else if (state.is(BlockTags.LEAVES) && !level.isClientSide()) {
                level.destroyBlock(origin, true, player);
                shouldBounce = false;
            }
        }

        if(shouldBounce) {
            super.onHitBlock(hitResult);
        }

    }

}
