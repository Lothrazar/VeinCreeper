package com.lothrazar.veincreeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.veincreeper.event.TrapCreeperEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(VeinCreeperMod.MODID)
public class VeinCreeperMod {

  public static final String MODID = "veincreeper";
  public static final Logger LOGGER = LogManager.getLogger();

  public VeinCreeperMod(IEventBus bus, ModContainer modContainer) {
//    modContainer.registerConfig(ModConfig.Type.COMMON, CreeperConfigManager.CONFIG);
    CreeperRegistry.RECIPE_SERIALIZERS.register(bus);
    CreeperRegistry.RECIPE_TYPES.register(bus);
    CreeperRegistry.BLOCKS.register(bus);
    CreeperRegistry.TILES.register(bus);
    NeoForge.EVENT_BUS.register(new TrapCreeperEvents());
  }
}
