package org.erg.deforestry.common.network;

import org.erg.deforestry.Deforestry;
import org.erg.deforestry.common.network.packet.RemoteChopPacket;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import java.util.Optional;

public class DeforestryPacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static int packetID = 0;
    public static final SimpleChannel INVENTIVE_CHOPPERS_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Deforestry.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextID() {
        return packetID++;
    }

    @SubscribeEvent
    public void registerPackets(FMLCommonSetupEvent event) {
        Deforestry.LOGGER.info("Registering " + Deforestry.MODID + " network packets");

        INVENTIVE_CHOPPERS_CHANNEL.registerMessage(
                nextID(),
                RemoteChopPacket.class,
                RemoteChopPacket::encode,
                RemoteChopPacket::decode,
                RemoteChopPacket::handle,
                Optional.of(PlayNetworkDirection.PLAY_TO_SERVER)
        );
    }

}
