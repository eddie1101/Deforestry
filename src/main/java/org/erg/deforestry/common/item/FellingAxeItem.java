package org.erg.deforestry.common.item;

import org.erg.deforestry.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.erg.deforestry.common.util.DeforestryUtil;

import java.util.*;

public class FellingAxeItem extends AxeItem {

    public FellingAxeItem(Item.Properties props) {
        super(Tiers.IRON, 12.0f /* Base Attack damage */, -3.5f /* Base Attack Speed*/, props.durability(850));
    }

    @Override
    public boolean mineBlock(ItemStack heldItem, Level level, BlockState blockBroken, BlockPos pos, LivingEntity breaker) {
        if(!level.isClientSide && blockBroken.getDestroySpeed(level, pos) != 0.0f) {
            heldItem.hurtAndBreak(1, breaker, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            if(blockBroken.is(BlockTags.LOGS)) {

                Block logType = blockBroken.getBlock();

                List<BlockPos> logs = DeforestryUtil.getLogsInTree(logType, pos, level);

                int numLogs = logs.size();
                int logsToChop = Math.min(Math.min(heldItem.getMaxDamage() - heldItem.getDamageValue(), numLogs), Config.maxFellingAxeChop);

                for(int i = 0; i < logsToChop; i++) {
                    level.destroyBlock(logs.get(i), true, breaker);
                    for(BlockPos leaf: DeforestryUtil.getConnectedLeavesAroundLog(logs.get(i), level, logs)) {
                        level.destroyBlock(leaf, true, breaker);
                    }
                    heldItem.hurtAndBreak(1, breaker, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
        }

        return true;
    }



}
