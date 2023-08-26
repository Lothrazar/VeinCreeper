package com.lothrazar.veincreeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.lothrazar.library.config.ConfigTemplate;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ConfigManager extends ConfigTemplate {

  private static ForgeConfigSpec CONFIG;
  public static ConfigValue<List<String>> ENTITIES;
  //default entities
  private static final List<String> DFLT = Arrays.asList(new String[] {
      "coal_creeper,255,0,0", "iron_creeper,0,0,255", "diamond_creeper,0,255,0"
  });
  static {
    initConfig();
  }

  private static void initConfig() {
    final ForgeConfigSpec.Builder BUILDER = builder();
    BUILDER.comment("General settings").push(VeinCreeperMod.MODID);
    ENTITIES = BUILDER.comment("id,red,green,blue.  Entities that will be registered to exist with the color filters. Use ids as normal such as /summon veincreeper:coal_creeper").define("create_creeper", DFLT);
    //   TESTING = CFG.comment("Testing mixin spam log if holding filled map").define("serverTest", true);
    BUILDER.pop(); // one pop for every push
    CONFIG = BUILDER.build();
  }

  public ConfigManager() {
    CONFIG.setConfig(setup(VeinCreeperMod.MODID));
  }

  public static List<CreepType> getMobs() {
    // TODO Auto-generated method stub
    List<CreepType> types = new ArrayList<>();
    for (String line : ENTITIES.get()) {
      String[] arr = line.split(",");
      //      String id = arr[0];
      int[] color = new int[] { Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]) };
      types.add(new CreepType(arr[0], color));
    }
    return types;
  }
}
