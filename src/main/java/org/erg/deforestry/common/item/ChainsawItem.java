package org.erg.deforestry.common.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
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
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.erg.deforestry.Config;
import org.erg.deforestry.common.registries.DeforestrySounds;
import org.erg.deforestry.common.util.DeforestryUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ChainsawItem extends Item {

    public ChainsawItem(Item.Properties props) {
        super(props.durability(4096));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                poseStack.translate(0.1d, -0.15d, -0.25d);
                poseStack.scale(0.75f, 0.75f, 0.75f);
                if(player.isUsingItem()) {
                    int duration = itemInHand.getUseDuration() - player.getUseItemRemainingTicks();
                    if (duration <= 24) {
                        poseStack.mulPose(Axis.XN.rotation((float) Math.PI / 5.0f));
                        double yOff = -.12d + ((duration % 12) / 100.0d);
                        poseStack.translate(0.0d, yOff - 0.1d, 0.0d);
                    } else {
                        int sway = duration % 20;
                        sway = sway < 10 ? sway : 20 - sway;
                        poseStack.translate(0.0d, 0.0d, (sway / 40.0d) - 0.1d);
                    }
                }

                return true;
            }
        });
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if(!player.isUsingItem()) {
            player.startUsingItem(hand);
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        level.playSound(null, player, DeforestrySounds.CHAINSAW_STARTING.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int ticksRemaining) {

        int duration = getUseDuration(stack) - ticksRemaining;
        int interval = Config.chainsawInterval;
        boolean skip = duration % (interval + 1) == 0;

        //Will I go to hell for using ticks to measure time?
        if(duration >= 30 && duration % 30 == 0)
            level.playSound(null, user, DeforestrySounds.CHAINSAW_SOUND.get(), SoundSource.PLAYERS, 0.7f, 0.8f);

        if(user instanceof Player player && duration > 30 && !skip) {

            //Thank you enderman
            for(Entity e: player.level().getEntities(player, player.getBoundingBox().inflate(player.getBlockReach()))) {
                Vec3 playerViewAngle = player.getViewVector(1.0F).normalize();
                Vec3 directionToEntity = new Vec3(e.getX() - player.getX(), e.getEyeY() - player.getEyeY(), e.getZ() - player.getZ());
                double distanceToEntity = directionToEntity.length();
                directionToEntity = directionToEntity.normalize();
                double angleSimilarity = playerViewAngle.dot(directionToEntity);
                if(angleSimilarity > 1.0 - 0.025 / distanceToEntity && player.hasLineOfSight(e)) {
                    e.hurt(player.level().damageSources().playerAttack(player), Config.chainsawDamage);
                    return;
                }
            }

            HitResult hitResult = player.pick(player.getBlockReach(), 1.0f, false);
            if(hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
                return;
            }

            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos origin = blockHitResult.getBlockPos();
            BlockState state = level.getBlockState(origin);

            if(!level.isClientSide() && state.is(BlockTags.LOGS)) {

                LootParams.Builder lootBuilder = new LootParams.Builder((ServerLevel) level)
                        .withParameter(LootContextParams.ORIGIN, origin.getCenter())
                        .withParameter(LootContextParams.TOOL, new ItemStack(Items.IRON_AXE));

                Block logType = level.getBlockState(origin).getBlock();

                ((ServerLevel) level).sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        hitResult.getLocation().x(),
                        hitResult.getLocation().y(),
                        hitResult.getLocation().z(),
                        level.getRandom().nextIntBetweenInclusive(3, 10),
                        0.0d,
                        0.0d,
                        0.0d,
                        1.0d
                );

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

                for(int i = 0; i < logsToChop; i++) {
                    level.destroyBlock(logs.get(i), false, player);
                    ItemStack choppedLog = new ItemStack(logType);
                    if(!player.addItem(choppedLog)) {
                        level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), choppedLog));
                    }

                    for(BlockPos leaf: DeforestryUtil.getConnectedLeavesAroundLog(logs.get(i), level, logs)) {
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
            } else if(!level.isClientSide() &&
                            (state.is(BlockTags.LEAVES) ||
                            state.is(BlockTags.PLANKS) ||
                            state.is(BlockTags.FENCE_GATES) ||
                            state.is(BlockTags.WOODEN_BUTTONS) ||
                            state.is(BlockTags.WOODEN_DOORS) ||
                            state.is(BlockTags.WOODEN_FENCES) ||
                            state.is(BlockTags.WOODEN_SLABS) ||
                            state.is(BlockTags.WOODEN_STAIRS) ||
                            state.is(BlockTags.WOODEN_PRESSURE_PLATES) ||
                            state.is(BlockTags.WOODEN_TRAPDOORS))) {

                LootParams.Builder lootBuilder = new LootParams.Builder((ServerLevel) level)
                        .withParameter(LootContextParams.ORIGIN, origin.getCenter())
                        .withParameter(LootContextParams.TOOL, new ItemStack(Items.IRON_AXE));

                level.destroyBlock(origin, false);
                for(ItemStack item: state.getDrops(lootBuilder)) {
                    if(!player.addItem(item)) {
                        level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), item));
                    }
                }
            }

        }
    }

}
