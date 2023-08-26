package com.lothrazar.veincreeper;

import net.minecraft.world.entity.EntityType;

public class CreepType {

  private String id;
  private int[] color = new int[3];
  private EntityType entityType;

  public CreepType(String id, int[] col) {
    this.setId(id);
    setColor(col);
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
}
