package com.lothrazar.veincreeper;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.lothrazar.veincreeper.block.BlockMobTrap;
import com.lothrazar.veincreeper.block.TileMobTrap;
import com.lothrazar.veincreeper.config.CreeperConfigManager;
import com.lothrazar.veincreeper.config.VeinCreeperType;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import com.lothrazar.veincreeper.config.VeinCreeperDTO;
import com.lothrazar.veincreeper.entity.VeinCreeperRender;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe.SerializePartyRecipe;
import com.lothrazar.veincreeper.recipe.TrapRecipe;
import com.lothrazar.veincreeper.recipe.TrapRecipe.SerializeTrapRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = VeinCreeperMod.MODID)
public class CreeperRegistry {

  private static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(VeinCreeperMod.MODID, "tab"));
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VeinCreeperMod.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VeinCreeperMod.MODID);
  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, VeinCreeperMod.MODID);
  public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, VeinCreeperMod.MODID);
  public static final DeferredHolder<Block, BlockMobTrap> TRAP = BLOCKS.register("trap", () -> new BlockMobTrap(Block.Properties.of()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileMobTrap>> TRAP_TILE = TILES.register("trap", () -> BlockEntityType.Builder.of(TileMobTrap::new, TRAP.get()).build(null));
  public static final DeferredHolder<RecipeType<?>, RecipeType<ExplosionRecipe>> EXPLOSION_RECIPE = RECIPE_TYPES.register("explosion", () -> new RecipeType<ExplosionRecipe>() {});
  public static final DeferredHolder<RecipeSerializer<?>, SerializePartyRecipe> R_SERIALIZER = RECIPE_SERIALIZERS.register("explosion", SerializePartyRecipe::new);
  public static final DeferredHolder<RecipeType<?>, RecipeType<TrapRecipe>> TRAP_RECIPE = RECIPE_TYPES.register("trap", () -> new RecipeType<TrapRecipe>() {});
  public static final DeferredHolder<RecipeSerializer<?>, SerializeTrapRecipe> TRAP_SERIALIZER = RECIPE_SERIALIZERS.register("trap", SerializeTrapRecipe::new);
  public static final String JSON_FILENAME = "config/veincreeper/veincreeper.json";
  static Builder<VeinCreeper> BUILDER = EntityType.Builder.<VeinCreeper> of(VeinCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(10);
  public static Map<String, VeinCreeperType> CREEPERS = null;
  private static final ArrayList<DeferredSpawnEggItem> EGGIES = new ArrayList<>();



  /**
   * the purpose if this is to fill in CREEPERS with data from veincreeper.json
   *
   * @param event
   * @return
   */
  private static List<VeinCreeperDTO> getEntityJsonOrDefault() {
    VeinCreeperMod.LOGGER.info("[VeinCreeperMod] Loading veincreeper.json ...");

    Path configPath = FMLPaths.GAMEDIR.get().resolve(JSON_FILENAME);
    if (!Files.exists(configPath)) {
      // Fall back to copying the default version
      writeDefaultFile(configPath);
    }

    Type listType = new TypeToken<List<VeinCreeperDTO>>(){}.getType();

    // Read and parse the JSON config file
    List<VeinCreeperDTO> creepers = new ArrayList<>();
    Gson gson = new Gson();
    try (Reader reader = Files.newBufferedReader(configPath)) {
      JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();


//      boolean spawnEggs = json.get("spawn_eggs").getAsBoolean();

      JsonArray enabledArray = json.getAsJsonArray("creepers");

     creepers = gson.fromJson(enabledArray, listType);


    } catch (IOException | RuntimeException e) {
      // Handle file not found or parse errors (log and use defaults)
      VeinCreeperMod.LOGGER.warn("Could not load config.  Fix the file, or delete it to restore the default " + JSON_FILENAME, e);

    }
    return creepers;
  }

  private static void writeDefaultFile(Path configPath) {
    try (var in = CreeperRegistry.class.getResourceAsStream("/veincreeper.defaults.json")) {
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

  private static void rebuildCreepers(List<VeinCreeperDTO> creepers) {
    CREEPERS = new HashMap<>();
    for (VeinCreeperDTO creeper : creepers) {
      CREEPERS.put(creeper.id(), new VeinCreeperType(creeper));
      VeinCreeperMod.LOGGER.info("[VeinCreeperMod] Loading " + creeper);
    }
  }

  @SubscribeEvent
  public static void onRegistry(RegisterEvent event) {

    // load custom JSON config
    if (CREEPERS == null || CREEPERS.isEmpty()) {
      var creepers = getEntityJsonOrDefault();
      rebuildCreepers(creepers);
    }

    // register entities
    event.register(Registries.ENTITY_TYPE, reg -> {
//      CreeperConfigManager.parseConfig();
      for (VeinCreeperType type : CREEPERS.values()) {
        type.setEntityType(BUILDER.build(type.getId()));
        reg.register(ResourceLocation.fromNamespaceAndPath(VeinCreeperMod.MODID, type.getId()), type.getEntityType());
      }
    });

    // register spawn eggs
    event.register(Registries.ITEM, reg -> {
      reg.register(ResourceLocation.fromNamespaceAndPath(VeinCreeperMod.MODID, "trap"), new BlockItem(TRAP.get(), new Item.Properties()));
//      CreeperConfigManager.parseConfig();
//      if (CreeperConfigManager.SPAWN_EGGS.get()) {
        for (VeinCreeperType type : CREEPERS.values()) {

          if(!type.createSpawnEgg()){
            continue;
          }

          var egg = new DeferredSpawnEggItem(
              () -> type.getEntityType(),
              type.getColor().getRGB(), 0,
              new Item.Properties()) {

            @Override
            public Component getName(ItemStack s) {
              return type.getDisplayName();
            }
          };
          reg.register(ResourceLocation.fromNamespaceAndPath(VeinCreeperMod.MODID, "spawn_egg_" + type.getId()), egg);
          EGGIES.add(egg);
        }
//      }
    });

    // set eggs into creative tab, if any
    event.register(Registries.CREATIVE_MODE_TAB, helper -> {
      helper.register(TAB.location(), CreativeModeTab.builder().icon(() -> new ItemStack(TRAP.get()))
          .title(Component.translatable("itemGroup." + VeinCreeperMod.MODID))
          .displayItems((enabledFlags, populator) -> {
            populator.accept(TRAP.get());
//            if (CreeperConfigManager.SPAWN_EGGS.get()) {
              for (Item egg : EGGIES) {
                populator.accept(egg);
              }
//            }
          }).build());
    });
  }

  @SubscribeEvent
  public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
    for (var c : CREEPERS.values()) {
      event.put(c.getEntityType(), VeinCreeper.createAttributes().build());
    }
  }

  @SubscribeEvent
  public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    for (var c : CREEPERS.values()) {
      event.registerEntityRenderer(c.getEntityType(), VeinCreeperRender::new);
    }
  }

  @SubscribeEvent
  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TRAP_TILE.get(), (be, side) -> be.getInventory());
  }
}
