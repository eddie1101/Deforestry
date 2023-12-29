package org.erg.deforestry.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.erg.deforestry.Deforestry.MODID;

public class DeforestrySounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> REMOTE_CHOPPER_SOUND = SOUNDS.register("remote_chopper_sound", (rloc) -> SoundEvent.createFixedRangeEvent(rloc, 5f));
    public static final DeferredHolder<SoundEvent, SoundEvent> CHAINSAW_SOUND = SOUNDS.register("chainsaw_sound", (rloc) -> SoundEvent.createFixedRangeEvent(rloc, 10f));

    public static final DeferredHolder<SoundEvent, SoundEvent> BOOMERANG_THROW = SOUNDS.register("boomerang_throw", (rloc) -> SoundEvent.createFixedRangeEvent(rloc, 4f));
    public static final DeferredHolder<SoundEvent, SoundEvent> BOOMERANG_FLYING = SOUNDS.register("boomerang_flying", (rloc) -> SoundEvent.createFixedRangeEvent(rloc, 6f));
    public static final DeferredHolder<SoundEvent, SoundEvent> BOOMERANG_CLANG = SOUNDS.register("boomerang_clang", (rloc) -> SoundEvent.createFixedRangeEvent(rloc, 16f));

}
