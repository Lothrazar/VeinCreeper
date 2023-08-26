package com.lothrazar.veincreeper;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
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

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MODID);
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MODID);
  //  public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModMain.MODID);
  static EntityType.Builder BUILDER = EntityType.Builder.<PartyCreeper> of(PartyCreeper::new, MobCategory.MISC).sized(1.4F, 2.7F - 0.3F).clientTrackingRange(10);
  //  public static final RegistryObject<EntityType<PartyCreeper>> CREEP = ENTITIES.register("party", () ->
  //  //BUILDER
  //  BUILDER.build("party"));
  static EntityType party;

  @SubscribeEvent
  public static void onRegistry(RegisterEvent event) {
    event.register(Registries.ENTITY_TYPE, reg -> {
      //
      party = BUILDER.build("party");
      reg.register("party", party);
      //
      //
    });
  }

  @SubscribeEvent
  public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
    event.put(party, PartyCreeper.createAttributes().build());
  }

  @SubscribeEvent
  public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(party, PartyCreeperRender::new);
  }
}
