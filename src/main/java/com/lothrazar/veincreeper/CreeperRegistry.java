package com.lothrazar.veincreeper;

import java.util.ArrayList;
import java.util.Map;
import com.lothrazar.veincreeper.block.CreeperTrap;
import com.lothrazar.veincreeper.block.TileCreeperTrap;
import com.lothrazar.veincreeper.conf.CreepType;
import com.lothrazar.veincreeper.conf.CreeperConfigManager;
import com.lothrazar.veincreeper.entity.VeinCreeper;
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
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreeperRegistry {

  //  static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VeinCreeperMod.MODID);
  private static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(VeinCreeperMod.MODID, "tab"));
  static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VeinCreeperMod.MODID);
  static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VeinCreeperMod.MODID);
  static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VeinCreeperMod.MODID);
  static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, VeinCreeperMod.MODID);
  public static final RegistryObject<Block> TRAP = BLOCKS.register("trap", () -> new CreeperTrap(Block.Properties.of()));
  public static final RegistryObject<BlockEntityType<TileCreeperTrap>> TRAP_TILE = TILES.register("trap", () -> BlockEntityType.Builder.of(TileCreeperTrap::new, TRAP.get()).build(null));
  public static final RegistryObject<RecipeType<ExplosionRecipe>> EXPLOSION_RECIPE = RECIPE_TYPES.register("explosion", () -> new RecipeType<ExplosionRecipe>() {});
  public static final RegistryObject<SerializePartyRecipe> R_SERIALIZER = RECIPE_SERIALIZERS.register("explosion", SerializePartyRecipe::new);
  public static final RegistryObject<RecipeType<TrapRecipe>> RECIPE_TRAP = RECIPE_TYPES.register("trap", () -> new RecipeType<TrapRecipe>() {});
  public static final RegistryObject<SerializeTrapRecipe> TRAP_SERIALIZER = RECIPE_SERIALIZERS.register("trap", SerializeTrapRecipe::new);
  static Builder<VeinCreeper> BUILDER = EntityType.Builder.<VeinCreeper> of(VeinCreeper::new, MobCategory.MISC).sized(1.4F, 2.7F - 0.3F).clientTrackingRange(10);
  public static Map<String, CreepType> CREEPERS;
  //  public static Map<String, Supplier<EntityType<VeinCreeper>>> EGG_HACKS = new HashMap<>();
  static boolean entitiesLoaded = false;
  static ArrayList<ForgeSpawnEggItem> EGGIES = new ArrayList<>();

  @SubscribeEvent
  public static void onRegistry(RegisterEvent event) {
    event.register(Registries.ENTITY_TYPE, reg -> {
      for (CreepType type : CREEPERS.values()) {
        type.setEntityType(BUILDER.build(type.getId()));
        reg.register(type.getId(), type.getEntityType());
        type.hack = () -> type.getEntityType();
      }
      entitiesLoaded = true;
    });
    event.register(Registries.ITEM, reg -> {
      reg.register("trap", new BlockItem(TRAP.get(), new Item.Properties()));
      CreeperConfigManager.parseConfig();
      for (CreepType type : CREEPERS.values()) {
        type.hack = () -> type.getEntityType();
        //        public static final Item CREEPER_SPAWN_EGG = registerItem("creeper_spawn_egg", new SpawnEggItem(EntityType.CREEPER, 894731, 0, new Item.Properties()));
        var egg = new ForgeSpawnEggItem(type.hack, 894731, 0, new Item.Properties());
        reg.register("spawn_egg_" + type.getId(), egg);
        EGGIES.add(egg);
      }
    });
    event.register(Registries.CREATIVE_MODE_TAB, helper -> {
      helper.register(TAB, CreativeModeTab.builder().icon(() -> new ItemStack(TRAP.get()))
          .title(Component.translatable("itemGroup." + VeinCreeperMod.MODID))
          .displayItems((enabledFlags, populator) -> {
            populator.accept(TRAP.get());
            for (Item egg : EGGIES) {
              populator.accept(egg);
            }
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
}
