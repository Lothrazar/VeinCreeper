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
      "coal_creeper,#281414,Coal,false,false",
      "iron_creeper,#823C14,Iron,false,false",
      "diamond_creeper,#39ADFA,Diamond,false,true",
      "copper_creeper,#f06d02,Copper,true,false",
      "gold_creeper,#F0F000,Gold,true,false",
      "redstone_creeper,#EB2300,Redstone,true,false",
      "lapis_creeper,#0000FF,Lapis,true,false",
      "emerald_creeper,#00FF00,Emerald,false,true",
      "quartz_creeper,#C7C5C5,Quartz,false,false",
      "purple_creeper,#FF00FF,Purple,true,true" });
  static {
    initConfig();
  }

  private static void initConfig() {
    final ForgeConfigSpec.Builder BUILDER = builder();
    BUILDER.comment("General settings").push(VeinCreeperMod.MODID);
    ENTITIES = BUILDER.comment("IMPORTANT: new creepers added here may not generate ore without adding custom recipes of type 'veincreeper:explosion', add more using datapacks.  Each row here will register one new creeper entity type. (unique_id,red,green,blue,display_name). The 'unique_id' string must exactly match the property used in the explosion recipe, this will link the creeper mob to the ore explosion recipe.  Color values are used as an overlay to existing creeper texture.  Test them out /summon veincreeper:coal_creeper")
        .defineList("creepers", DFLT, s -> s instanceof String);
    SPAWN_EGGS = BUILDER.comment("Register spawn eggs items. false here will hide remove the eggs.  If you added new mobs to the config you will also need to make a resource pack to setup your new eggs.  Without eggs you can still use the trap block, or the /summon command for spawning veincreeper's")
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
        CreeperRegistry.CREEPERS.put(id, new VeinCreeperType(id, c, displayName, exp, isDestructive)); // TODO: is destructive (does it explode what it doesnt convert)
      }
      catch (Exception e) {
        VeinCreeperMod.LOGGER.debug(" CSV debug: ", e);
        //////////////////////////////////////////////////// 0      1    2     3    4      5             6
        VeinCreeperMod.LOGGER.error("Error parsing config (entityId,red,green,blue,alpha,displayName,shouldDropExp)  " + Arrays.toString(arr));
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
