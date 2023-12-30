package org.erg.deforestry.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.erg.deforestry.Config;
import org.erg.deforestry.Deforestry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.erg.deforestry.Deforestry.MODID;

public class DeforestryUtil {

    public static double clamp(double min, double val, double max) {
        return Math.max(Math.min(val, max), min);
    }

    public static ResourceLocation getDeforestryRLFrom(String location) {
        return new ResourceLocation(MODID + ":" + location);
    }
    @NotNull
    public static List<BlockPos> getLogsInTree(Block logType, BlockPos origin, Level level) {
        Set<BlockPos> logs = new HashSet<>();
        Stack<BlockPos> toSearch = new Stack<>();
        toSearch.add(origin);

        while(!toSearch.isEmpty() && logs.size() <= Config.maxGlobalChop) {

            BlockPos center = toSearch.pop();

            List<BlockPos> candidates = getSurroundingBlocks(center);

            for(BlockPos pos: candidates) {
                if(level.getBlockState(pos).is(logType)) {
                    if(!toSearch.contains(pos) && !logs.contains(pos)) toSearch.add(pos);
                }
            }

            logs.add(center);

        }

        return new ArrayList<>(logs);
    }

    @NotNull
    public static List<BlockPos> getSurroundingBlocks(AABB aabb, BlockPos center) {
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

    public static List<BlockPos> getSurroundingBlocks(BlockPos center) {
        AABB aabb = AABB.encapsulatingFullBlocks(center.below().west().south(), center.above().east().north());
        return getSurroundingBlocks(aabb, center);
    }

    public static List<BlockPos> getAdjacentBlocks(BlockPos center) {
        List<BlockPos> blocks = new ArrayList<>();
        blocks.add(center);
        blocks.add(center.above());
        blocks.add(center.below());
        blocks.add(center.east());
        blocks.add(center.west());
        blocks.add(center.north());
        blocks.add(center.south());
        return blocks;
    }

    @NotNull
    public static List<BlockPos> getConnectedLeavesAroundLog(BlockPos log, Level level) {
        Set<BlockPos> leaves = new HashSet<>();
        Stack<BlockPos> toSearch = new Stack<>();
        toSearch.add(log);

        AABB range = AABB.encapsulatingFullBlocks(log.below(4).west(4).south(4), log.above(4).east(4).north(4));

        while(!toSearch.isEmpty()) {

            BlockPos center = toSearch.pop();

            for(BlockPos pos: getAdjacentBlocks(center)) {
                if(level.getBlockState(pos).is(BlockTags.LEAVES)) {
                    if(!toSearch.contains(pos) &&
                            !leaves.contains(pos) &&
                            range.contains(pos.getX(), pos.getY(), pos.getZ())) {
                        toSearch.add(pos);
                    }
                }
            }

            if(level.getBlockState(center).is(BlockTags.LEAVES))
                leaves.add(center);
        }
        return new ArrayList<>(leaves);

    }

}
