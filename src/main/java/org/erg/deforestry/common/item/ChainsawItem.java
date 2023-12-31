package org.erg.deforestry.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.erg.deforestry.Config;
import org.erg.deforestry.common.registries.DeforestrySounds;
import org.erg.deforestry.common.util.DeforestryUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

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
        return super.useOn(ctx);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), DeforestrySounds.CHAINSAW_SOUND.get(), SoundSource.PLAYERS, 0.7f, 0.8f);
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int ticksRemaining) {

        int duration = getUseDuration(stack) - ticksRemaining;
        int interval = Config.chainsawInterval;
        boolean skip = duration % (interval + 1) == 0;

        if(user instanceof Player player && duration > 20 && !skip) {

            HitResult hitResult = user.pick(player.getBlockReach(), 1.0f, false);
            if(hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
                return;
            }

            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos origin = blockHitResult.getBlockPos();
            BlockState state = level.getBlockState(origin);

            if(!level.isClientSide() && state.is(BlockTags.LOGS)) {

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

                LootParams.Builder lootBuilder = new LootParams.Builder((ServerLevel) level)
                        .withParameter(LootContextParams.ORIGIN, origin.getCenter())
                        .withParameter(LootContextParams.TOOL, new ItemStack(Items.IRON_AXE));

                for(int i = 0; i < logsToChop; i++) {
                    level.destroyBlock(logs.get(i), false, player);
                    ItemStack choppedLog = new ItemStack(logType);
                    if(!player.addItem(choppedLog)) {
                        level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), choppedLog));
                    }

                    for(BlockPos leaf: DeforestryUtil.getConnectedLeavesAroundLog(logs.get(i), level)) {
                        BlockState blockState = level.getBlockState(leaf);
                        for(ItemStack item: blockState.getDrops(lootBuilder)) {
                            if(!player.addItem(item)) {
                                level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), item));
                            }
                        }
                        level.destroyBlock(leaf, false, player);
                    }

                    stack.hurtAndBreak(1, player, (e) -> {
                        if (e != null) {
                            e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                        }
                    });
                }
            }
        }
    }

}
