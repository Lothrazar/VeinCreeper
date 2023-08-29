package com.lothrazar.veincreeper;

import java.util.HashMap;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreeperRegistry {

  static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VeinCreeperMod.MODID);
  static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VeinCreeperMod.MODID);
  static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VeinCreeperMod.MODID);
  static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VeinCreeperMod.MODID);
  static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, VeinCreeperMod.MODID);
  public static final RegistryObject<Block> TRAP = BLOCKS.register("trap", () -> new CreeperTrap(Block.Properties.of()));
  public static final RegistryObject<Item> TRAP_ITEM = ITEMS.register("trap", () -> new BlockItem(TRAP.get(), new Item.Properties()));
  public static final RegistryObject<BlockEntityType<TileCreeperTrap>> TRAP_TILE = TILES.register("trap", () -> BlockEntityType.Builder.of(TileCreeperTrap::new, TRAP.get()).build(null));
  public static final RegistryObject<RecipeType<ExplosionRecipe>> EXPLOSION_RECIPE = RECIPE_TYPES.register("explosion", () -> new RecipeType<ExplosionRecipe>() {});
  public static final RegistryObject<SerializePartyRecipe> R_SERIALIZER = RECIPE_SERIALIZERS.register("explosion", SerializePartyRecipe::new);
  public static final RegistryObject<RecipeType<TrapRecipe>> RECIPE_TRAP = RECIPE_TYPES.register("trap", () -> new RecipeType<TrapRecipe>() {});
  public static final RegistryObject<SerializeTrapRecipe> TRAP_SERIALIZER = RECIPE_SERIALIZERS.register("trap", SerializeTrapRecipe::new);
  static Builder<VeinCreeper> BUILDER = EntityType.Builder.<VeinCreeper> of(VeinCreeper::new, MobCategory.MISC).sized(1.4F, 2.7F - 0.3F).clientTrackingRange(10);
  public static Map<String, CreepType> CREEPERS = new HashMap<>();

  @SubscribeEvent
  public static void onRegistry(RegisterEvent event) {
    event.register(Registries.ENTITY_TYPE, reg -> {
      CreeperConfigManager.parseConfig();
      for (CreepType type : CREEPERS.values()) {
        createCreeper(reg, type);
      }
    });
  }

  private static void createCreeper(RegisterHelper<EntityType<?>> reg, CreepType type) {
    type.setEntityType(BUILDER.build(type.getId()));
    reg.register(type.getId(), type.getEntityType());
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
