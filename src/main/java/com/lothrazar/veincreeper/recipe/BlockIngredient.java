package com.lothrazar.veincreeper.recipe;

import net.minecraft.world.level.block.Block;

public class BlockIngredient {

  private final Block block;
  private final Integer chance;

  public BlockIngredient(Block b) {
    this.block = b;
    this.chance = null;
  }

  public BlockIngredient(Block b, int bonus) {
    this.block = b;
    this.chance = Integer.valueOf(bonus);
  }

  public Block getBlock() {
    return block;
  }

  public Integer getChance() {
    return chance;
  }

  public boolean hasChance() {
    return chance != null;
  }
}
