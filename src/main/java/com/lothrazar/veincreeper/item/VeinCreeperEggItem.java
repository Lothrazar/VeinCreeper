package com.lothrazar.veincreeper.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class VeinCreeperEggItem extends SpawnEggItem {

  private final Component displayName;

  public VeinCreeperEggItem(Properties properties, Component displayName) {
    super(properties);
    this.displayName = displayName;
  }

  @Override
  public Component getName(ItemStack stack) {
    return displayName;
  }
}
