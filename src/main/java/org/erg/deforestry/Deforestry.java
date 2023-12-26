package org.erg.deforestry;

import org.erg.deforestry.common.network.DeforestryPacketHandler;
import org.erg.deforestry.common.registries.DeforestryBlocks;
import org.erg.deforestry.common.registries.DeforestryCreativeModeTabs;
import org.erg.deforestry.common.registries.DeforestryItems;
import org.erg.deforestry.common.registries.DeforestrySounds;
import org.erg.deforestry.data.DeforestryDataGenerator;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;


@Mod(Deforestry.MODID)
public class Deforestry
{
    public static final String MODID = "deforestry";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Deforestry(IEventBus modEventBus)
    {
        modEventBus.register(new DeforestryDataGenerator());
        modEventBus.register(new DeforestryPacketHandler());

        modEventBus.addListener(this::commonSetup);

        DeforestryBlocks.BLOCKS.register(modEventBus);
        DeforestryItems.ITEMS.register(modEventBus);
        DeforestryCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        DeforestrySounds.SOUNDS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
