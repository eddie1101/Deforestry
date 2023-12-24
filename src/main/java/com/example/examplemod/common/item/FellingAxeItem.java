package com.example.examplemod.common.item;

import com.example.examplemod.Config;
import com.example.examplemod.InventiveChoppers;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class FellingAxeItem extends AxeItem {

    public FellingAxeItem(Item.Properties props) {
        super(Tiers.IRON, 11 /* Attack damage */, 1 /* Attack Speed*/, props);
    }

    @Override
    public boolean mineBlock(ItemStack heldItem, Level level, BlockState blockBroken, BlockPos pos, LivingEntity breaker) {
        if(!level.isClientSide && blockBroken.getDestroySpeed(level, pos) != 0.0f) {
            heldItem.hurtAndBreak(1, breaker, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            if(blockBroken.is(BlockTags.LOGS)) {

                Block logType = blockBroken.getBlock();

                ArrayList<BlockPos> logs = collectSimilarAdjacentLogs(logType, pos, level);

                int numLogs = logs.size();
                int logsToChop = Math.min(Math.min(heldItem.getMaxDamage() - heldItem.getDamageValue(), numLogs), Config.maxFellingAxeChop);

                for(int i = 0; i < logsToChop; i++) {
                    level.destroyBlock(logs.get(i), true);
                    heldItem.hurtAndBreak(1, breaker, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
        }

        return true;
    }

    @NotNull
    private static ArrayList<BlockPos> collectSimilarAdjacentLogs(Block logType, BlockPos origin, Level level) {
        Set<BlockPos> logs = new HashSet<>();
        Stack<BlockPos> toSearch = new Stack<>();
        toSearch.add(origin);

        while(!toSearch.isEmpty()) {

            BlockPos center = toSearch.pop();

            StringBuilder sb = new StringBuilder();
            sb.append("FIND LOGS!\n");
            sb.append("Logs: ");
            for(BlockPos pos: logs) {
                sb.append(pos).append(", ");
            }
            sb.append("\n").append("toSearch: ");
            for(BlockPos pos: toSearch) {
                sb.append(pos).append(", ");
            }
            sb.append("\n").append("center: ").append(center).append("\n");

            AABB aabb = AABB.encapsulatingFullBlocks(center.below().west().south(), center.above().east().north());
            ArrayList<BlockPos> candidates = getSurroundingBlocks(aabb, center);

            sb.append("Candidates: ");
            for(BlockPos pos: candidates) {
                sb.append(pos).append(", ");
            }
            sb.append("\n");


            for(BlockPos pos: candidates) {
                if(level.getBlockState(pos).is(logType)) {
                    if(!toSearch.contains(pos) && !logs.contains(pos)) toSearch.add(pos);
                }
            }

            logs.add(center);

            InventiveChoppers.LOGGER.info(sb.toString());
        }

        return new ArrayList<>(logs);
    }

    @NotNull
    private static ArrayList<BlockPos> getSurroundingBlocks(AABB aabb, BlockPos center) {
        ArrayList<BlockPos> candidates = new ArrayList<>();

        for(int x = (int) aabb.maxX - 1; x >= aabb.minX; x--) {
            for(int y = (int) aabb.maxY - 1; y >= aabb.minY; y--) {
                for(int z = (int) aabb.maxZ - 1; z >= aabb.minZ; z--) {
                    BlockPos newPos = new BlockPos(x, y, z);
                    if(!newPos.equals(center)) {
                        candidates.add(newPos);
                    }
                }
            }
        }
        return candidates;
    }

}
