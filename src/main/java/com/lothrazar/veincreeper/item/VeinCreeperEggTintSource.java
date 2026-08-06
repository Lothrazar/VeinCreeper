package com.lothrazar.veincreeper.item;

import com.lothrazar.veincreeper.config.VeinCreeperData;
import com.lothrazar.veincreeper.config.VeinCreeperType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

// Spawn egg colors are no longer settable via a constructor (DeferredSpawnEggItem was removed, and
// SpawnEggItem now has no color API at all) - this mod's creeper types (and their colors) are loaded
// from a runtime-editable JSON config, so the color can't be baked into a static per-item asset either.
// This custom ItemTintSource reads the entity type back off the stack's DataComponents.ENTITY_DATA
// (via SpawnEggItem.getType) and looks up that creeper's currently-configured color at render time,
// registered once and shared by every spawn egg item's "tints" array (see assets/veincreeper/items/*.json).
public record VeinCreeperEggTintSource(int layer) implements ItemTintSource {

  public static final MapCodec<VeinCreeperEggTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(com.mojang.serialization.Codec.INT.fieldOf("layer").forGetter(VeinCreeperEggTintSource::layer))
          .apply(i, VeinCreeperEggTintSource::new));

  @Override
  public int calculate(ItemStack stack, ClientLevel level, LivingEntity owner) {
    var entityType = SpawnEggItem.getType(stack);
    VeinCreeperType creeper = entityType == null ? null : VeinCreeperData.getCreepType(entityType);
    if (creeper == null) {
      return 0xFFFFFFFF;
    }
    return layer == 0 ? ARGB.opaque(creeper.getColor().getRGB()) : 0xFF000000;
  }

  @Override
  public MapCodec<VeinCreeperEggTintSource> type() {
    return MAP_CODEC;
  }
}
