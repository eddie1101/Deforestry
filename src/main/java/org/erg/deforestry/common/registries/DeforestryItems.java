package org.erg.deforestry.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.erg.deforestry.common.item.*;
import org.erg.deforestry.common.registration.DeferredItemRegister;

import static org.erg.deforestry.Deforestry.MODID;

public class DeforestryItems {

    public static final DeferredItemRegister ITEMS = new DeferredItemRegister();

    public static final DeferredItem<Item> REMOTE_CHOPPER = ITEMS.registerItem("remote_chopper", RemoteChopperItem::new);
    public static final DeferredItem<Item> FELLING_AXE = ITEMS.registerItem("felling_axe", FellingAxeItem::new);
    public static final DeferredItem<Item> CHAINSAW = ITEMS.registerItem("chainsaw", ChainsawItem::new);
    public static final DeferredItem<Item> BOOMERANG = ITEMS.registerItem("boomerang", BoomerangItem::new);
    public static final DeferredItem<Item> BOOMERANG_CHOPPER = ITEMS.registerItem("boomerang_chopper", BoomerangChopperItem::new);

}
