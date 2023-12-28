package org.erg.deforestry.common.event;

import org.erg.deforestry.Deforestry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod.EventBusSubscriber(modid=Deforestry.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class DeforestryEventHandlers {

    @SubscribeEvent
    public static void blockBreakEvent(BlockEvent.BreakEvent e) {

    }

}
