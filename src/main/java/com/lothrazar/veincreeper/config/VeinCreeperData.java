package com.lothrazar.veincreeper.config;

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.lothrazar.veincreeper.VeinCreeperMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLPaths;

public class VeinCreeperData {

  public static final String JSON_FILENAME = "config/" + VeinCreeperMod.MODID + ".json";
  public static Map<String, VeinCreeperType> CREEPERS = null;
  public static final ArrayList<Item> EGGIES = new ArrayList<>();

  public static List<VeinCreeperDTO> getEntityJsonOrDefault() {
    VeinCreeperMod.LOGGER.info("[VeinCreeperMod] Loading  " + JSON_FILENAME);

    Path configPath = FMLPaths.GAMEDIR.get().resolve(JSON_FILENAME);
    if (!Files.exists(configPath)) {
      // Fall back to copying the default version
      writeDefaultFile(configPath);
    }

    Type listType = new TypeToken<List<VeinCreeperDTO>>(){}.getType();

    List<VeinCreeperDTO> creepers = new ArrayList<>();
    Gson gson = new Gson();
    try (Reader reader = Files.newBufferedReader(configPath)) {
      JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

//      boolean spawnEggs = json.get("spawn_eggs").getAsBoolean();

      JsonArray enabledArray = json.getAsJsonArray("creepers");
      creepers = gson.fromJson(enabledArray, listType);

    } catch (IOException | RuntimeException e) {
      VeinCreeperMod.LOGGER.warn("Could not load config.  Fix the file, or delete it to restore the default " + JSON_FILENAME, e);
    }
    return creepers;
  }

  private static void writeDefaultFile(Path configPath) {
    // hardcoded defaults in /resources/ packaged with jar
    try (var in = VeinCreeperData.class.getResourceAsStream("/veincreeper.defaults.json")) {
      if (in == null) {
        VeinCreeperMod.LOGGER.error("veincreeper.defaults.json not found in JAR");
        return; //should not happen unless jar is hacked
      }
      Files.createDirectories(configPath.getParent());
      Files.copy(in, configPath);
      VeinCreeperMod.LOGGER.info("New veincreeper.json config generated from source veincreeper.defaults");
    } catch (IOException e) {
      VeinCreeperMod.LOGGER.warn("Could not write default.json", e);
    }
  }

  public static void rebuildCreepers(List<VeinCreeperDTO> creepers) {
    CREEPERS = new HashMap<>();
    for (VeinCreeperDTO creeper : creepers) {
      CREEPERS.put(creeper.id(), new VeinCreeperType(creeper));
      VeinCreeperMod.LOGGER.info("[VeinCreeperMod] Loading " + creeper);
    }
  }

  public static Color getCreeperColor(String key) {
    if (CREEPERS.containsKey(key)) {
      return CREEPERS.get(key).getColor();
    }
    VeinCreeperMod.LOGGER.error("ERROR! no color found for mob " + key);
    return Color.RED;
  }

  public static String getKeyFromEntity(Entity entity) {
    return entity.getType().getDescriptionId().replace("entity.veincreeper.", "");
  }

  @SuppressWarnings("rawtypes")
  public static VeinCreeperType getCreepType(EntityType partyCreeper) {
    for (var creeper : CREEPERS.values()) {
      if (creeper.getEntityType() == partyCreeper) {
        return creeper;
      }
    }
    return null;
  }

  public static VeinCreeperType getCreepType(com.lothrazar.veincreeper.entity.VeinCreeper partyCreeper) {
    return getCreepType(partyCreeper.getType());
  }
}
