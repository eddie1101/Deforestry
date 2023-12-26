package org.erg.deforestry.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.erg.deforestry.data.providers.DeforestryItemModelProvider;
import org.erg.deforestry.data.providers.DeforestryLanguageProvider;
import org.erg.deforestry.data.providers.DeforestryRecipeProvider;
import org.erg.deforestry.data.providers.DeforestrySoundDefinitionsProvider;


public class DeforestryDataGenerator {

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper efh = event.getExistingFileHelper();
        PackOutput output = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new DeforestryLanguageProvider(output));
        gen.addProvider(event.includeClient(), new DeforestryItemModelProvider(output, efh));
        gen.addProvider(event.includeClient(), new DeforestrySoundDefinitionsProvider(output, efh));
        gen.addProvider(event.includeServer(), new DeforestryRecipeProvider(output, event.getLookupProvider()));
    }

}
