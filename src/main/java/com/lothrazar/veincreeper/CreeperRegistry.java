package com.lothrazar.veincreeper;

import com.lothrazar.veincreeper.block.BlockMobTrap;
import com.lothrazar.veincreeper.block.IItemHandlerResourceHandler;
import com.lothrazar.veincreeper.block.TileMobTrap;
import com.lothrazar.veincreeper.config.VeinCreeperData;
import com.lothrazar.veincreeper.config.VeinCreeperType;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import com.lothrazar.veincreeper.entity.VeinCreeperRender;
import com.lothrazar.veincreeper.item.VeinCreeperEggItem;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import com.lothrazar.veincreeper.recipe.TrapRecipe;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = VeinCreeperMod.MODID)
public class CreeperRegistry {

  private static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, "tab"));
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VeinCreeperMod.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VeinCreeperMod.MODID);
  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, VeinCreeperMod.MODID);
  public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, VeinCreeperMod.MODID);
  public static final DeferredHolder<Block, BlockMobTrap> TRAP = BLOCKS.register("trap", () -> new BlockMobTrap(Block.Properties.of()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileMobTrap>> TRAP_TILE = TILES.register("trap", () -> new BlockEntityType<>(TileMobTrap::new, TRAP.get()));
  public static final DeferredHolder<RecipeType<?>, RecipeType<ExplosionRecipe>> EXPLOSION_RECIPE = RECIPE_TYPES.register("explosion", () -> new RecipeType<ExplosionRecipe>() {});
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExplosionRecipe>> R_SERIALIZER = RECIPE_SERIALIZERS.register("explosion", () -> ExplosionRecipe.SERIALIZER);
  public static final DeferredHolder<RecipeType<?>, RecipeType<TrapRecipe>> TRAP_RECIPE = RECIPE_TYPES.register("trap", () -> new RecipeType<TrapRecipe>() {});
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TrapRecipe>> TRAP_SERIALIZER = RECIPE_SERIALIZERS.register("trap", () -> TrapRecipe.SERIALIZER);
  static Builder<VeinCreeper> BUILDER = Builder.<VeinCreeper> of(VeinCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(10);

  @SubscribeEvent
  public static void onRegistry(RegisterEvent event) {

    // load custom JSON config
    if (VeinCreeperData.CREEPERS == null || VeinCreeperData.CREEPERS.isEmpty()) {
      var creepers = VeinCreeperData.getEntityJsonOrDefault();
      VeinCreeperData.rebuildCreepers(creepers);
    }

    // register entities
    event.register(Registries.ENTITY_TYPE, reg -> {
//      CreeperConfigManager.parseConfig();
      for (VeinCreeperType type : VeinCreeperData.CREEPERS.values()) {
        Identifier entityId = Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, type.getId());
        type.setEntityType(BUILDER.build(ResourceKey.create(Registries.ENTITY_TYPE, entityId)));
        reg.register(entityId, type.getEntityType());
      }
    });

    // register spawn eggs
    event.register(Registries.ITEM, reg -> {
      reg.register(Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, "trap"), new BlockItem(TRAP.get(), new Item.Properties().useBlockDescriptionPrefix()));
//      CreeperConfigManager.parseConfig();
//      if (CreeperConfigManager.SPAWN_EGGS.get()) {
        for (VeinCreeperType type : VeinCreeperData.CREEPERS.values()) {

          if (!type.createSpawnEgg()) {
            continue;
          }

          var egg = new VeinCreeperEggItem(
              new Item.Properties().component(DataComponents.ENTITY_DATA, TypedEntityData.of(type.getEntityType(), new CompoundTag())),
              type.getDisplayName());
          reg.register(Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, "spawn_egg_" + type.getId()), egg);
          VeinCreeperData.EGGIES.add(egg);
        }
//      }
    });

    // set eggs into creative tab, if any
    event.register(Registries.CREATIVE_MODE_TAB, helper -> {
      helper.register(TAB.identifier(), CreativeModeTab.builder().icon(() -> new ItemStack(TRAP.get()))
          .title(Component.translatable("itemGroup." + VeinCreeperMod.MODID))
          .displayItems((enabledFlags, populator) -> {
            populator.accept(TRAP.get());
//            if (CreeperConfigManager.SPAWN_EGGS.get()) {
              for (Item egg : VeinCreeperData.EGGIES) {
                populator.accept(egg);
              }
//            }
          }).build());
    });
  }

  @SubscribeEvent
  public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
    for (var c : VeinCreeperData.CREEPERS.values()) {
      event.put(c.getEntityType(), VeinCreeper.createAttributes().build());
    }
  }

  @SubscribeEvent
  public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    for (var c : VeinCreeperData.CREEPERS.values()) {
      event.registerEntityRenderer(c.getEntityType(), VeinCreeperRender::new);
    }
  }

  @SubscribeEvent
  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(Capabilities.Item.BLOCK, TRAP_TILE.get(), (be, side) -> new IItemHandlerResourceHandler(be.getInventory()));
  }
}
