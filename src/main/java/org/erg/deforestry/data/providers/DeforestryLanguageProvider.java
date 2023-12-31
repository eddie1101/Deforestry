package org.erg.deforestry.data.providers;

import org.erg.deforestry.Deforestry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.erg.deforestry.common.registries.DeforestryEntityTypes;
import org.erg.deforestry.common.registries.DeforestryItems;

public class DeforestryLanguageProvider extends LanguageProvider {

    public DeforestryLanguageProvider(PackOutput output) {
        super(output, Deforestry.MODID, "en_us");
    }

    @Override
    public void addTranslations() {
        add(DeforestryItems.REMOTE_CHOPPER.get(), "Remote Chopper");
        add(DeforestryItems.FELLING_AXE.get(), "Felling Axe");
        add(DeforestryItems.CHAINSAW.get(), "Chainsaw");
        add(DeforestryItems.BOOMERANG.get(), "Boomerang");
        add(DeforestryItems.BOOMERANG_CHOPPER.get(), "Boomerang Chopper");
        add(DeforestryEntityTypes.BOOMERANG_ENTITY.get(), "Boomerang");
        add(DeforestryEntityTypes.BOOMERANG_CHOPPER_ENTITY.get(), "Boomerang Chopper");
        add("tab.deforestry.deforestry_tab", "Deforestry");
    }



}
