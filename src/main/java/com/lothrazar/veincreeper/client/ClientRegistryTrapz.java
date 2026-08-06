package com.lothrazar.veincreeper.client;

import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.block.RenderMobTrap;
import com.lothrazar.veincreeper.item.VeinCreeperEggTintSource;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = VeinCreeperMod.MODID, value = Dist.CLIENT)
public class ClientRegistryTrapz {

  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(CreeperRegistry.TRAP_TILE.get(), RenderMobTrap::new);
  }

  //item colors are fully data-driven now; this just registers the tint source *type*,
  //actual per-item tint assignment happens in assets/veincreeper/items/*.json
  @SubscribeEvent
  public static void onRegisterItemColorHandlers(RegisterColorHandlersEvent.ItemTintSources event) {
    event.register(Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, "creeper_egg"), VeinCreeperEggTintSource.MAP_CODEC);
  }
}
