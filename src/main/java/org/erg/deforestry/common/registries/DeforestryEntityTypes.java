package org.erg.deforestry.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.erg.deforestry.common.entity.BoomerangEntity;
import static org.erg.deforestry.Deforestry.MODID;

public class DeforestryEntityTypes {


    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<?>> BOOMERANG_ENTITY = ENTITIES.register("boomerang_entity", () -> EntityType.Builder.<BoomerangEntity>of(BoomerangEntity::new, MobCategory.MISC).sized(1, (float) 3 / 16).build("boomerang_entity"));

}