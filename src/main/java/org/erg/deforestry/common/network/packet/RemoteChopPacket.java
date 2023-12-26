package org.erg.deforestry.common.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

public class RemoteChopPacket {

    BlockPos pos;
    ItemStack remoteChopper;

    public RemoteChopPacket(BlockPos pos, ItemStack item) {
        this.pos = pos;
        this.remoteChopper = item;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeItem(remoteChopper);
    }

    public static RemoteChopPacket decode(FriendlyByteBuf buf) {
        return new RemoteChopPacket(buf.readBlockPos(), buf.readItem());
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {

            //Do Stuff!

        });

        ctx.setPacketHandled(true);
    }

}
