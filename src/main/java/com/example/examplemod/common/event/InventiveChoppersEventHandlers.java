package com.example.examplemod.common.event;

import com.example.examplemod.InventiveChoppers;
import com.example.examplemod.common.item.FellingAxeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.io.IOException;

@Mod.EventBusSubscriber(modid=InventiveChoppers.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class InventiveChoppersEventHandlers {

    @SubscribeEvent
    public static void blockBreakEvent(BlockEvent.BreakEvent e) {

    }

}
