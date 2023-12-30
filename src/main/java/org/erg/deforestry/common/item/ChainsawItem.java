package org.erg.deforestry.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.erg.deforestry.Config;
import org.erg.deforestry.Deforestry;
import org.erg.deforestry.common.registries.DeforestrySounds;
import org.erg.deforestry.common.util.DeforestryUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static org.erg.deforestry.common.util.DeforestryUtil.getLogsInTree;

public class ChainsawItem extends Item {

    public ChainsawItem(Item.Properties props) {
        super(props.durability(4096));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        Level level = ctx.getLevel();
        BlockPos origin = ctx.getClickedPos();
        BlockState state = level.getBlockState(origin);

        if(!level.isClientSide() && state.is(BlockTags.LOGS)) {

            ItemStack stack = ctx.getItemInHand();
            Player player = ctx.getPlayer();

            Block logType = level.getBlockState(origin).getBlock();

            List<BlockPos> logs = DeforestryUtil.getLogsInTree(logType, origin, level);

            Collections.sort(logs);
            Collections.reverse(logs);

            int numLogs = logs.size();
            int logsToChop = Math.min(
                    Math.min(
                            stack.getMaxDamage() - stack.getDamageValue(),
                            numLogs
                    ),
                    Config.chainsawSpeed
            );

            level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), DeforestrySounds.CHAINSAW_SOUND.get(), SoundSource.PLAYERS, 0.7f, 0.8f);

            for(int i = 0; i < logsToChop; i++) {
                level.destroyBlock(logs.get(i), false);

                for(BlockPos leaf: DeforestryUtil.getConnectedLeavesAroundLog(logs.get(i), level)) {
                    level.destroyBlock(leaf, true, player);
                }

                ItemStack choppedLog = new ItemStack(logType);
                if(!player.addItem(choppedLog)) {
                    level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), choppedLog));
                }

                stack.hurtAndBreak(1, player, (e) -> {
                    if (e!= null) {
                        e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                    }
                });
            }

            player.getCooldowns().addCooldown(this, Config.chainsawCooldown);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

}
