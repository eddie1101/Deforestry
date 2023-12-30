package org.erg.deforestry;

import org.erg.deforestry.client.ClientRegistration;
import org.erg.deforestry.common.network.DeforestryPacketHandler;
import org.erg.deforestry.common.registries.*;
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


//Progress tracker: https://github.com/users/eddie1101/projects/1/views/1

@Mod(Deforestry.MODID)
public class Deforestry
{
    public static final String MODID = "deforestry";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Deforestry(IEventBus modEventBus)
    {
        modEventBus.register(new DeforestryDataGenerator());
        modEventBus.register(new DeforestryPacketHandler());
        modEventBus.register(ClientRegistration.class);

        modEventBus.addListener(this::commonSetup);

        DeforestryBlocks.BLOCKS.register(modEventBus);
        DeforestryItems.ITEMS.register(modEventBus);
        DeforestryEntityTypes.ENTITIES.register(modEventBus);
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

}
