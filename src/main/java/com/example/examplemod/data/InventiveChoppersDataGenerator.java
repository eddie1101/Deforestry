package com.example.examplemod.data;

import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;


public class InventiveChoppersDataGenerator {

    private static Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper efh = event.getExistingFileHelper();
        PackOutput output = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new InventiveChoppersLanguageProvider(output));
        gen.addProvider(event.includeClient(), new InventiveChoppersItemModelProvider(output, efh));
        gen.addProvider(event.includeServer(), new InventiveChoppersRecipeProvider(output, event.getLookupProvider()));

    }

}
