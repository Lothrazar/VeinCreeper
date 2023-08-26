package com.lothrazar.veincreeper;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.veincreeper.entity.PartyCreeper;
import com.lothrazar.veincreeper.entity.PartyCreeperRender;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

@SuppressWarnings("unchecked")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PartyCreeperRegistry {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VeinCreeperMod.MODID);
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VeinCreeperMod.MODID);
  //
  static Builder<PartyCreeper> BUILDER = EntityType.Builder.<PartyCreeper> of(PartyCreeper::new, MobCategory.MISC).sized(1.4F, 2.7F - 0.3F).clientTrackingRange(10);
  public static Map<String, CreepType> CREEPERS = new HashMap<>();

  @SubscribeEvent
  public static void onRegistry(RegisterEvent event) {
    event.register(Registries.ENTITY_TYPE, reg -> {
      for (CreepType type : ConfigManager.getMobs()) {
        createCreeper(reg, type);
      }
      //      createCreeper(reg, "party_coal");
      //      createCreeper(reg, "party_iron"); 
    });
  }

  private static void createCreeper(RegisterHelper<EntityType<?>> reg, CreepType type) {
    type.setEntityType(BUILDER.build(type.getId()));
    CREEPERS.put(type.getId(), type);
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
