package org.erg.deforestry.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import org.erg.deforestry.common.registries.DeforestrySounds;

import static org.erg.deforestry.Deforestry.MODID;
import static org.erg.deforestry.common.registries.DeforestrySounds.CHAINSAW_SOUND;
import static org.erg.deforestry.common.registries.DeforestrySounds.REMOTE_CHOPPER_SOUND;

public class DeforestrySoundDefinitionsProvider extends SoundDefinitionsProvider {

    public DeforestrySoundDefinitionsProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, MODID, efh);
    }

    @Override
    public void registerSounds() {
        add(REMOTE_CHOPPER_SOUND.get(), definition()
                .subtitle("Click, Beep")
                .with(sound(REMOTE_CHOPPER_SOUND.getId()))
        );

        add(CHAINSAW_SOUND.get(), definition()
                .subtitle("Chainsaw revving")
                .with(sound(CHAINSAW_SOUND.getId()))
        );
    }

}
