package com.lothrazar.veincreeper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.lothrazar.library.config.ConfigTemplate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;

public class ConfigManager extends ConfigTemplate {

  private static ForgeConfigSpec CONFIG;
  public static ConfigValue<List<String>> ENTITIES;
  //default entities
  private static final List<String> DFLT = Arrays.asList(new String[] {
      "coal_creeper,255,0,0,minecraft:stone_ore_replaceables,minecraft:coal_ore",
      "iron_creeper,0,0,255,minecraft:stone_ore_replaceables,minecraft:iron_ore",
      "diamond_creeper,0,255,0,minecraft:stone_ore_replaceables,minecraft:diamond_ore"
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

  public static void parseConfig() {
    PartyCreeperRegistry.CREEPERS = new HashMap<>();
    // TODO Auto-generated method stub 
    for (String line : ENTITIES.get()) {
      String[] arr = line.split(",");
      try {
        String id = arr[0];
        int[] color = new int[] { Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]) };
        System.out.println("Test tag key " + arr[4]);
        System.out.println("Test BLOCK key " + arr[5]);
        TagKey<Block> replaceMe = TagKey.create(Registries.BLOCK, new ResourceLocation(arr[4]));
        Block ore = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(arr[5]));
        PartyCreeperRegistry.CREEPERS.put(id, new CreepType(arr[0], color, replaceMe, ore));
      }
      catch (Exception e) {
        VeinCreeperMod.LOGGER.error("Error parsing config (new_entity,red,green,blue,tag,block)  " + arr);
      }
    }
  }

  public static int[] getCreeperColor(String key) {
    if (PartyCreeperRegistry.CREEPERS.containsKey(key)) {
      return PartyCreeperRegistry.CREEPERS.get(key).getColor();
    }
    //    for (String line : ENTITIES.get()) {
    //      String[] arr = line.split(",");
    //      String id = arr[0];
    //      if (id.equals(key))
    //        return new int[] { Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]) };
    //    }
    //random default
    System.out.println("ERROR! no color found for mob " + key);
    return new int[] { 200, 0, 0 };
  }

  public static String getKeyFromEntity(Entity entity) {
    final String key = entity.getType().getDescriptionId().replace("entity.veincreeper.", "");
    return key;
  }
}
