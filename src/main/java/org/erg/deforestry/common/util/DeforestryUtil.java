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
    public static List<BlockPos> getConnectedLeavesAroundLog(BlockPos log, Level level, final List<BlockPos> originTree) {
        Set<BlockPos> leaves = new HashSet<>();
        Stack<BlockPos> toSearch = new Stack<>();
        List<List<BlockPos>> surroundingTrees = new ArrayList<>();
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
                } else if(level.getBlockState(pos).is(BlockTags.LOGS) && !originTree.contains(pos) && !logInTrees(pos, surroundingTrees)) {
                    surroundingTrees.add(getLogsInTree(level.getBlockState(pos).getBlock(), pos, level));
                }
            }

            if(level.getBlockState(center).is(BlockTags.LEAVES))
                leaves.add(center);
        }

        List<BlockPos> boundingAxes = new ArrayList<>();
        for(List<BlockPos> tree: surroundingTrees) {
            int highX = Integer.MIN_VALUE, highZ = Integer.MIN_VALUE, lowX = Integer.MAX_VALUE, lowZ = Integer.MAX_VALUE;
            for(BlockPos pos: tree) {
                if(pos.getX() > highX)
                    highX = pos.getX();
                if(pos.getX() < lowX)
                    lowX = pos.getX();
                if(pos.getZ() > highZ)
                    highZ = pos.getZ();
                if(pos.getZ() < lowZ)
                    lowZ = pos.getZ();
            }

            int midX = lowX + ((highX - lowX) / 2);
            int midZ = lowZ + ((highZ - lowZ) / 2);

            boundingAxes.add(new BlockPos(midX, log.getY(), midZ));
        }

        AABB validLeaves = new AABB(log).inflate(4.0d);

        for(BlockPos axis: boundingAxes) {
            BlockPos diff = axis.subtract(log);
            BlockPos midpoint = log.offset(diff.getX() / 2, diff.getY() / 2, diff.getZ() / 2);
            BlockPos midpointDiff = midpoint.subtract(log);

            int xSigned = midpointDiff.getX() < 0 ? -1 : 1;
            int zSigned = midpointDiff.getZ() < 0 ? -1 : 1;

            int contractAmountX = Math.abs(midpointDiff.getX()) < 4 && Math.abs(midpointDiff.getX()) > 0 ? (4 * xSigned) - midpointDiff.getX() : 0;
            int contractAmountZ = Math.abs(midpointDiff.getZ()) < 4 && Math.abs(midpointDiff.getZ()) > 0 ? (4 * zSigned) - midpointDiff.getZ() : 0;

            validLeaves = validLeaves.contract(contractAmountX, 0, contractAmountZ);

            Deforestry.LOGGER.debug("bounding midpoint: " + midpoint.getX() + " " + midpoint.getZ());
            Deforestry.LOGGER.debug("bounding midpoint delta: " + midpointDiff.getX() + " " + midpointDiff.getZ());
            Deforestry.LOGGER.debug("Contract Amounts: " + contractAmountX + " " + contractAmountZ);
        }

        AABB finalValidLeaves = validLeaves;
        leaves.removeIf(e -> !finalValidLeaves.contains(e.getX(), e.getY(), e.getZ()));

        for(BlockPos axis: boundingAxes) {
            Deforestry.LOGGER.debug("Bounding Axis: " + axis.getX() + " " + axis.getZ());
        }

        return new ArrayList<>(leaves);
    }

    @NotNull
    public static boolean logInTrees(BlockPos pos, List<List<BlockPos>> trees) {
        boolean found = false;
        for(List<BlockPos> tree: trees) {
            found = tree.stream().anyMatch(e -> e.equals(pos));
        }
        return found;
    }

}
