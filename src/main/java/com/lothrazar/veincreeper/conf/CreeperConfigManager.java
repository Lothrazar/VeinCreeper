package com.lothrazar.veincreeper.conf;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.lothrazar.library.config.ConfigTemplate;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CreeperConfigManager extends ConfigTemplate {

  private static ForgeConfigSpec CONFIG;
  public static ConfigValue<List<? extends String>> ENTITIES;
  public static BooleanValue SPAWN_EGGS;
  //default entities
  private static final List<String> DFLT = Arrays.asList(new String[] {
      "coal_creeper,#281414,Coal,false,false,3",
      "iron_creeper,#823C14,Iron,false,false,2.5",
      "diamond_creeper,#39ADFA,Diamond,false,false,3",
      "copper_creeper,#f06d02,Copper,true,false,4",
      "gold_creeper,#F0F000,Gold,true,false,2.5",
      "redstone_creeper,#EB2300,Redstone,true,false,2.5",
      "lapis_creeper,#0000FF,Lapis,true,false,3",
      "emerald_creeper,#00FF00,Emerald,false,false,2.6",
      "quartz_creeper,#C7C5C5,Quartz,false,true,2.5",
      "purple_creeper,#FF00FF,Purple,true,true,5" });
  static {
    initConfig();
  }

  private static void initConfig() {
    final ForgeConfigSpec.Builder BUILDER = builder();
    BUILDER.comment("Mod settings").push(VeinCreeperMod.MODID);
    ENTITIES = BUILDER.comment("\t  (id,colour,displayName,dropsExp,isDestructive,radius). id must be unique. There are two recipe types you will want to add after adding new creepers: 'veincreeper:explosion' recipes to make creeper explosions convert ore, and 'veincreeper:trap' recipes to convert mobs into other mobs on the trap block, so add these to your modpack/datapack/scripts based on how all vanilla ores are set up by default https://github.com/Lothrazar/VeinCreeper/tree/trunk/1.20/src/main/resources/data/veincreeper/recipes . Yes each creeper have multiple recipes of each type .  The 'id' string must exactly match the \"veincreeer\" property used in the explosion recipe, this will link the creeper mob to the ore explosion recipe.  Colour values are used as an overlay to existing creeper texture. ")
        .defineList("creepers", DFLT, s -> s instanceof String);
    SPAWN_EGGS = BUILDER.comment("   Register spawn eggs items. false here will remove the eggs.  If you added new mobs to the config you will also need to make a resource pack to setup your new eggs with one file per egg from assets/veincreeper/models/item/spawn_egg_[id] in your modpack's resource-pack.  Find the eggs in the creative tab")
        .define("spawn_eggs", true);
    BUILDER.pop(); // one pop for every push
    CONFIG = BUILDER.build();
  }

  public CreeperConfigManager() {
    CONFIG.setConfig(setup(VeinCreeperMod.MODID));
  }

  public static void parseConfig() {
    CreeperRegistry.CREEPERS = new HashMap<>();
    // TODO Auto-generated method stub 
    for (String line : ENTITIES.get()) {
      String[] arr = line.split(",");
      try {
        String id = arr[0];
        String hexColor = arr[1];
        Color c = Color.decode(hexColor);
        var displayName = arr[2];
        var exp = Boolean.parseBoolean(arr[3]);
        var isDestructive = Boolean.parseBoolean(arr[4]);
        float rad = Float.parseFloat(arr[5]);
        CreeperRegistry.CREEPERS.put(id, new VeinCreeperType(id, c, displayName, exp, isDestructive, rad));
      }
      catch (Exception e) {
        //////////////////////////////////////////////////// 0      1    2     3    4      5             6
        VeinCreeperMod.LOGGER.error("Error parsing config   " + Arrays.toString(arr));
        VeinCreeperMod.LOGGER.error("see veincreeper.toml , or delete the file for a fresh copy", e);
      }
    }
  }

  public static Color getCreeperColor(String key) {
    if (CreeperRegistry.CREEPERS.containsKey(key)) {
      return CreeperRegistry.CREEPERS.get(key).getColor();
    }
    VeinCreeperMod.LOGGER.error("ERROR! no color found for mob " + key);
    return Color.RED;
  }

  public static String getKeyFromEntity(Entity entity) {
    final String key = entity.getType().getDescriptionId().replace("entity.veincreeper.", "");
    return key;
  }

  @SuppressWarnings("rawtypes")
  public static VeinCreeperType getCreepType(EntityType partyCreeper) {
    for (var creeper : CreeperRegistry.CREEPERS.values()) {
      if (creeper.getEntityType() == partyCreeper) {
        return creeper;
      }
    }
    return null;
  }

  public static VeinCreeperType getCreepType(VeinCreeper partyCreeper) {
    return getCreepType(partyCreeper.getType());
  }
}
