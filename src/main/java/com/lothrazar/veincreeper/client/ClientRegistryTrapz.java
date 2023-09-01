package com.lothrazar.veincreeper.client;

import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.block.RenderMobTrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistryTrapz {

  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(CreeperRegistry.TRAP_TILE.get(), RenderMobTrap::new);
  }
}
