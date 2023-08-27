package com.lothrazar.veincreeper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.lothrazar.library.config.ConfigTemplate;
import com.lothrazar.veincreeper.entity.PartyCreeper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;

public class ConfigManager extends ConfigTemplate {

  private static ForgeConfigSpec CONFIG;
  public static ConfigValue<List<String>> ENTITIES;
  //default entities
  private static final List<String> DFLT = Arrays.asList(new String[] {
      //TODO: minecraft:deepslate_ore_replaceables  | minecraft:deepslate_coal_ore 
      "coal_creeper,40,20,20,minecraft:deepslate_ore_replaceables,minecraft:coal_ore",
      "iron_creeper,130,60,20,minecraft:stone_ore_replaceables,minecraft:iron_ore",
      "diamond_creeper,0,120,200,minecraft:stone_ore_replaceables,minecraft:diamond_ore",
      "copper_creeper,200,90,0,minecraft:stone_ore_replaceables,minecraft:copper_ore",
      "gold_creeper,240,240,0,minecraft:stone_ore_replaceables,minecraft:gold_ore", //nether variant
      "redstone_creeper,235,35,0,minecraft:stone_ore_replaceables,minecraft:redstone_ore",
      "lapis_creeper,0,0,255,minecraft:stone_ore_replaceables,minecraft:lapis_ore",
      "emerald_creeper,0,255,0,minecraft:stone_ore_replaceables,minecraft:emerald_ore"
      //quartz? (nether)
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
        String strTags = arr[4];
        TagKey<Block> replaceMe = TagKey.create(Registries.BLOCK, new ResourceLocation(strTags));
        String strBlock = arr[5];
        Block ore = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(strBlock));
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
    VeinCreeperMod.LOGGER.error("ERROR! no color found for mob " + key);
    return new int[] { 200, 0, 0 };
  }

  public static String getKeyFromEntity(Entity entity) {
    final String key = entity.getType().getDescriptionId().replace("entity.veincreeper.", "");
    return key;
  }

  public static Component getDisplayName(PartyCreeper partyCreeper) {
    for (var creeper : PartyCreeperRegistry.CREEPERS.values()) {
      if (creeper.getEntityType() == partyCreeper.getType()) {
        return creeper.getOre().getName().append(" ").append(EntityType.CREEPER.getDescription());
      }
    }
    return null;
  }
}
