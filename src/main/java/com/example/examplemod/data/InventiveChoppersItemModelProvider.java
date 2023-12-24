package com.example.examplemod.data;

import com.example.examplemod.InventiveChoppers;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class InventiveChoppersItemModelProvider extends ItemModelProvider {

    public InventiveChoppersItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, InventiveChoppers.MODID, efh);
    }

    @Override
    public void registerModels() {
        basicItem(InventiveChoppers.REMOTE_CHOPPER.get());
        basicItem(InventiveChoppers.FELLING_AXE.get());
    }

}
