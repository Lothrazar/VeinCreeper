package com.lothrazar.veincreeper.conf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.lothrazar.library.config.ConfigTemplate;
import com.lothrazar.veincreeper.PartyCreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.entity.PartyCreeper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ConfigManager extends ConfigTemplate {

  private static ForgeConfigSpec CONFIG;
  public static ConfigValue<List<? extends String>> ENTITIES;
  //default entities
  private static final List<String> DFLT = Arrays.asList(new String[] {
      //TODO: minecraft:deepslate_ore_replaceables  | minecraft:deepslate_coal_ore 
      "coal_creeper,40,20,20,Coal",
      "iron_creeper,130,60,20,Iron",
      "diamond_creeper,0,120,200,Diamond",
      "copper_creeper,200,90,0,Copper",
      "gold_creeper,240,240,0,Gold", //nether variant
      "redstone_creeper,235,35,0,Redstone",
      "lapis_creeper,0,0,255,Lapis",
      "emerald_creeper,0,255,0,Emerald"
      //quartz? (nether)
  });
  static {
    initConfig();
  }

  private static void initConfig() {
    final ForgeConfigSpec.Builder BUILDER = builder();
    BUILDER.comment("General settings").push(VeinCreeperMod.MODID);
    ENTITIES = BUILDER.comment("IMPORTANT: new creepers added here may not generate ore without adding custom recipes of type 'veincreeper:explosion', add more using datapacks.  Each row here will register one new creeper entity type. (unique_id,red,green,blue,display_name). The 'unique_id' string must exactly match the property used in the explosion recipe, this will link the creeper mob to the ore explosion recipe.  Color values are used as an overlay to existing creeper texture.  Test them out /summon veincreeper:coal_creeper")
        .defineList("creepers", DFLT, s -> s instanceof String);
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
        PartyCreeperRegistry.CREEPERS.put(id, new CreepType(arr[0], color, arr[4]));
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
        return Component.literal(creeper.getBlockName()).append(" ").append(EntityType.CREEPER.getDescription());
      }
    }
    return null;
  }
}
