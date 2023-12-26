package org.erg.deforestry.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.erg.deforestry.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.erg.deforestry.common.registries.DeforestrySounds;
import org.erg.deforestry.common.util.InventiveChoppersUtil;

import java.util.ArrayList;

public class RemoteChopperItem extends Item {

    public RemoteChopperItem(Item.Properties props) {
        super(props.durability(100).setNoRepair());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!level.isClientSide) {
            HitResult hit = player.pick(Config.remoteChopperRange, 0.0f, false);

            if(hit.getType() == HitResult.Type.BLOCK) {

                BlockPos pos = ((BlockHitResult) hit).getBlockPos();

                BlockState target = level.getBlockState(pos);

                if (target.is(BlockTags.LOGS)) {
                    stack.hurtAndBreak(1, player, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));

                    Block logType = target.getBlock();

                    ArrayList<BlockPos> logs = InventiveChoppersUtil.getLogsInTree(logType, pos, level);

                    int numLogs = logs.size();
                    int logsToChop = Math.min(Math.min(stack.getMaxDamage() - stack.getDamageValue(), numLogs), Config.maxRemoteChop);
                    for (int i = 0; i < logsToChop; i++) {
                        level.destroyBlock(logs.get(i), true);
                    }

                    player.getCooldowns().addCooldown(this, 20);
                    level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), DeforestrySounds.REMOTE_CHOPPER_SOUND.get(), SoundSource.PLAYERS);
                    return InteractionResultHolder.success(stack);
                }
                if(player.canReach(pos, 0d)) {
                    return InteractionResultHolder.success(stack);
                } else {
                    return InteractionResultHolder.pass(stack);
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }
}
