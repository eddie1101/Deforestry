package org.erg.deforestry.data.providers;

import org.erg.deforestry.Deforestry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.erg.deforestry.common.registries.DeforestryItems;

public class DeforestryItemModelProvider extends ItemModelProvider {

    public DeforestryItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, Deforestry.MODID, efh);
    }

    @Override
    public void registerModels() {
        basicItem(DeforestryItems.REMOTE_CHOPPER.get());
        basicItem(DeforestryItems.FELLING_AXE.get());
        basicItem(DeforestryItems.BOOMERANG.get());
        basicItem(DeforestryItems.BOOMERANG_CHOPPER.get());
        basicItem(DeforestryItems.CHAINSAW.get());
    }

}
