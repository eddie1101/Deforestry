package com.example.examplemod.data;

import com.example.examplemod.InventiveChoppers;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class InventiveChoppersLanguageProvider extends LanguageProvider {

    public InventiveChoppersLanguageProvider(PackOutput output) {
        super(output, InventiveChoppers.MODID, "en_us");
    }

    @Override
    public void addTranslations() {
        add(InventiveChoppers.REMOTE_CHOPPER.get(), "Remote Chopper");
        add(InventiveChoppers.FELLING_AXE.get(), "Felling Axe");
        add("itemGroup.inventive_choppers_tab", "Inventive Choppers");
    }



}
