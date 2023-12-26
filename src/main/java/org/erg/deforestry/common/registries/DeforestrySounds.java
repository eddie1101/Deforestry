package org.erg.deforestry.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.erg.deforestry.Deforestry.MODID;

public class DeforestrySounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> REMOTE_CHOPPER_SOUND = SOUNDS.register("remote_chopper_sound", (rloc) -> SoundEvent.createFixedRangeEvent(rloc, 5f));

}
