package com.lothrazar.veincreeper.conf;

import net.minecraft.world.entity.EntityType;

public class CreepType {

  private String id;
  private int[] color = new int[3];//render overrides
  private EntityType entityType; //hold just for registries
  private String blockName;//for display name only

  public CreepType(String id, int[] col, String blockName) {
    if (id == null || col.length != 3) {
      throw new IllegalArgumentException("Check config values and try again for id=" + id);
    }
    this.setId(id);
    setColor(col);
    this.setBlockName(blockName);
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

  public String getBlockName() {
    return blockName;
  }

  public void setBlockName(String blockName) {
    this.blockName = blockName;
  }
}
