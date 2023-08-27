package com.lothrazar.veincreeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    new ConfigManager();
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    PartyCreeperRegistry.RECIPE_SERIALIZERS.register(bus);
    PartyCreeperRegistry.RECIPE_TYPES.register(bus);
    bus.addListener(this::setup);
    bus.addListener(this::setupClient);
    MinecraftForge.EVENT_BUS.register(new CreeperCmd());
  }

  private void setup(final FMLCommonSetupEvent event) {
    //    MinecraftForge.EVENT_BUS.register(new WhateverEvents()); 
  }

  private void setupClient(final FMLClientSetupEvent event) {
    //for client side only setup
  }
}
