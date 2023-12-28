package org.erg.deforestry.common.util;

import net.minecraft.resources.ResourceLocation;
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

        while(!toSearch.isEmpty()) {

            if(logs.size() >= Config.maxGlobalChop) {
                break;
            }

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
            List<BlockPos> candidates = getSurroundingBlocks(aabb, center);

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

            Deforestry.LOGGER.debug(sb.toString());
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

}
