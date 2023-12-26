package org.erg.deforestry.common.registration;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.erg.deforestry.Deforestry.MODID;

public class DeferredItemRegister {

    private final List<DeferredItem<Item>> allItems = new ArrayList<>();
    private final DeferredRegister.Items internal = DeferredRegister.createItems(MODID);

    public DeferredItemRegister() {}

    public DeferredItem<Item> registerItem(String name, Function<Item.Properties, ? extends Item> sup) {
        DeferredItem<Item> registeredItem = internal.registerItem(name, sup);
        this.allItems.add(registeredItem);
        return registeredItem;
    }

    public void register(IEventBus bus) {
        this.internal.register(bus);
    }

    public List<DeferredItem<Item>> getAllItems() {
        return this.allItems;
    }

}
