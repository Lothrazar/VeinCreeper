package com.lothrazar.veincreeper;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.veincreeper.conf.ConfigManager;
import com.lothrazar.veincreeper.conf.CreepType;
import com.lothrazar.veincreeper.entity.PartyCreeper;
import com.lothrazar.veincreeper.entity.PartyCreeperRender;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe.SerializePartyRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unchecked")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PartyCreeperRegistry {

  static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VeinCreeperMod.MODID);
  static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, VeinCreeperMod.MODID);
  public static final RegistryObject<RecipeType<ExplosionRecipe>> RECIPE = RECIPE_TYPES.register("explosion", () -> new RecipeType<ExplosionRecipe>() {
    //yep leave it empty its fine
  });
  public static final RegistryObject<SerializePartyRecipe> R_SERIALIZER = RECIPE_SERIALIZERS.register("explosion", SerializePartyRecipe::new);
  //
  static Builder<PartyCreeper> BUILDER = EntityType.Builder.<PartyCreeper> of(PartyCreeper::new, MobCategory.MISC).sized(1.4F, 2.7F - 0.3F).clientTrackingRange(10);
  public static Map<String, CreepType> CREEPERS = new HashMap<>();

  @SubscribeEvent
  public static void onRegistry(RegisterEvent event) {
    event.register(Registries.ENTITY_TYPE, reg -> {
      //TODO: parse/refresh config from that one event
      ConfigManager.parseConfig();
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
      event.put(c.getEntityType(), PartyCreeper.createAttributes().build());
    }
  }

  @SubscribeEvent
  public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    for (var c : CREEPERS.values()) {
      c.getColor(); /////////////// 
      event.registerEntityRenderer(c.getEntityType(), PartyCreeperRender::new);
    }
  }
}
