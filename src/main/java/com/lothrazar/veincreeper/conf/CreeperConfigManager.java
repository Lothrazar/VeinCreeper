package com.lothrazar.veincreeper.conf;

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
      "coal_creeper,40,20,20,150,Coal,false",
      "iron_creeper,130,60,20,200,Iron,true",
      "diamond_creeper,0,120,200,255,Diamond,false",
      "copper_creeper,200,90,0,200,Copper,true",
      "gold_creeper,240,240,0,200,Gold,true",
      "redstone_creeper,235,35,0,255,Redstone,true",
      "lapis_creeper,0,0,255,240,Lapis,true",
      "emerald_creeper,0,255,0,255,Emerald,false",
      "quartz_creeper,100,100,100,255,Quartz,false",
      "purple_creeper,255,0,255,255,Purple,true" });
  static {
    initConfig();
  }

  private static void initConfig() {
    final ForgeConfigSpec.Builder BUILDER = builder();
    BUILDER.comment("General settings").push(VeinCreeperMod.MODID);
    ENTITIES = BUILDER.comment("IMPORTANT: new creepers added here may not generate ore without adding custom recipes of type 'veincreeper:explosion', add more using datapacks.  Each row here will register one new creeper entity type. (unique_id,red,green,blue,display_name). The 'unique_id' string must exactly match the property used in the explosion recipe, this will link the creeper mob to the ore explosion recipe.  Color values are used as an overlay to existing creeper texture.  Test them out /summon veincreeper:coal_creeper")
        .defineList("creepers", DFLT, s -> s instanceof String);
    SPAWN_EGGS = BUILDER.comment("Register spawn eggs for testing. They have no textures so you would have to add a resource pack if you need these visible in a modpack.  Without eggs you can still use the trap block, or the /summon command for spawning veincreeper's").define("spawn_eggs", false);
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
        int[] color = new int[] { Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), Integer.parseInt(arr[4]) };
        CreeperRegistry.CREEPERS.put(id, new VeinCreeperType(arr[0], color, arr[5], Boolean.parseBoolean(arr[6]))); // TODO: is destructive (does it explode what it doesnt convert)
      }
      catch (Exception e) {
        VeinCreeperMod.LOGGER.debug(" CSV debug: ", e);
        //////////////////////////////////////////////////// 0      1    2     3    4      5             6
        VeinCreeperMod.LOGGER.error("Error parsing config (entityId,red,green,blue,alpha,displayName,shouldDropExp)  " + Arrays.toString(arr));
      }
    }
  }

  public static int[] getCreeperColor(String key) {
    if (CreeperRegistry.CREEPERS.containsKey(key)) {
      return CreeperRegistry.CREEPERS.get(key).getColor();
    }
    VeinCreeperMod.LOGGER.error("ERROR! no color found for mob " + key);
    return new int[] { 200, 0, 0 };
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
  //  public static Component getDisplayName(PartyCreeper partyCreeper) {
  //    for (var creeper : PartyCreeperRegistry.CREEPERS.values()) {
  //      if (creeper.getEntityType() == partyCreeper.getType()) {
  //        return Component.literal(creeper.getBlockName()).append(" ").append(EntityType.CREEPER.getDescription());
  //      }
  //    }
  //    return null;
  //  }
}
