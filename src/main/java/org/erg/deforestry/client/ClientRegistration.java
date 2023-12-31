package org.erg.deforestry.client;

import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.erg.deforestry.client.model.BoomerangChopperModel;
import org.erg.deforestry.client.model.BoomerangModel;
import org.erg.deforestry.client.render.entity.BoomerangChopperRenderer;
import org.erg.deforestry.client.render.entity.BoomerangRenderer;
import org.erg.deforestry.common.entity.BoomerangChopperEntity;
import org.erg.deforestry.common.entity.BoomerangEntity;
import org.erg.deforestry.common.registries.DeforestryEntityTypes;

import static org.erg.deforestry.Deforestry.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer( (EntityType<BoomerangEntity>) DeforestryEntityTypes.BOOMERANG_ENTITY.get(), BoomerangRenderer::new);
        event.registerEntityRenderer( (EntityType<BoomerangChopperEntity>) DeforestryEntityTypes.BOOMERANG_CHOPPER_ENTITY.get(), BoomerangChopperRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BoomerangModel.BOOMERANG_LAYER, BoomerangModel::createLayer);
        event.registerLayerDefinition(BoomerangChopperModel.BOOMERANG_CHOPPER_LAYER, BoomerangChopperModel::createLayer);
    }

}
