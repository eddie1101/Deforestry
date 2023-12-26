package org.erg.deforestry.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.erg.deforestry.common.registration.DeferredBlockRegister;
import org.erg.deforestry.common.registration.DeferredItemRegister;

import static org.erg.deforestry.Deforestry.MODID;

public class DeforestryCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("deforestry_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> DeforestryItems.FELLING_AXE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                addItemsToDisplay(DeforestryItems.ITEMS, output);
                addBlockItemsToDisplay(DeforestryBlocks.BLOCKS, output);
            }).build());

    private static void addItemsToDisplay(DeferredItemRegister register, CreativeModeTab.Output output) {
        for(ItemLike item: register.getAllItems()) {
            output.accept(item);
        }
    }

    private static void addBlockItemsToDisplay(DeferredBlockRegister register, CreativeModeTab.Output output) {
        for(ItemLike item: register.getAllBlocks()) {
            output.accept(item);
        }
    }
}
