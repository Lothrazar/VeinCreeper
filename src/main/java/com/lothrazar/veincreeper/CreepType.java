package com.lothrazar.veincreeper;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class CreepType {

  private String id;
  private int[] color = new int[3];
  private EntityType entityType;
  private TagKey<Block> replace = null; // BlockTags.STONE_ORE_REPLACEABLES;
  private Block ore = null; // Blocks.COAL_ORE;

  public CreepType(String id, int[] col, TagKey<Block> tagKey, Block block) {
    if (id == null || col.length != 3 || tagKey == null || block == null) {
      throw new IllegalArgumentException("Check config values and try again for id=" + id);
    }
    this.setId(id);
    setColor(col);
    this.setReplace(tagKey);
    this.setOre(block);
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public void setEntityType(EntityType entityType) {
    this.entityType = entityType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int[] getColor() {
    return color;
  }

  public void setColor(int[] color) {
    this.color = color;
  }

  public TagKey<Block> getReplace() {
    return replace;
  }

  public void setReplace(TagKey<Block> replace) {
    this.replace = replace;
  }

  public Block getOre() {
    return ore;
  }

  public void setOre(Block ore) {
    this.ore = ore;
  }
}
