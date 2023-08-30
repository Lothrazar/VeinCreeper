package com.lothrazar.veincreeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.veincreeper.conf.CreeperCmd;
import com.lothrazar.veincreeper.conf.CreeperConfigManager;
import com.lothrazar.veincreeper.event.TrapCreeperEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VeinCreeperMod.MODID)
public class VeinCreeperMod {

  public static final String MODID = "veincreeper";
  public static final Logger LOGGER = LogManager.getLogger();

  public VeinCreeperMod() {
    new CreeperConfigManager();
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    CreeperRegistry.RECIPE_SERIALIZERS.register(bus);
    CreeperRegistry.RECIPE_TYPES.register(bus);
    CreeperRegistry.BLOCKS.register(bus);
    CreeperRegistry.ITEMS.register(bus);
    CreeperRegistry.TILES.register(bus);
    bus.addListener(this::setup);
    bus.addListener(this::setupClient);
    MinecraftForge.EVENT_BUS.register(new TrapCreeperEvents());
    MinecraftForge.EVENT_BUS.register(new CreeperCmd());
  }

  private void setup(final FMLCommonSetupEvent event) {
    //    MinecraftForge.EVENT_BUS.register(new WhateverEvents()); 
  }

  private void setupClient(final FMLClientSetupEvent event) {
    //for client side only setup
  }
}
