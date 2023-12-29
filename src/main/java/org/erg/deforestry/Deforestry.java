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


//TODO: (we don't have a kanban board so comments will do)
// ----------
// BOOMERANG:
// Don't reverse velocity when boomerang goes out of range, just let PID take over
// Maybe add a return radius based on launched power?
// Make bounces appropriate for the side of the block that they hit
// Add collisions during returning state (block and entity?)
// Make boomerang pitch and roll with angular velocity
// Make boomerang damage depend on relative velocity of hit entity
// ----------
// Chopper Util:
// Make chops remove leaves in range of the appropriate type for the wood being chopped
// ----------
// Boomerang Chopper:
// Everything is in place you could just do a prototype now if you wanted

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
