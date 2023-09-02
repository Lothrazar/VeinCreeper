package com.lothrazar.veincreeper.conf;

import java.awt.Color;
import java.util.function.Supplier;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

public class VeinCreeperType {

  private String id;
  private Color color;
  private EntityType<VeinCreeper> entityType; //hold just for registries
  private String blockName;//for display name only
  private boolean shouldDropExperience;
  public Supplier<EntityType<VeinCreeper>> hack;
  private boolean isDestructive;

  public VeinCreeperType(String id, Color col, String blockName, boolean exp, boolean isDestructive) {
    this.setId(id);
    this.setColor(col);
    this.setBlockName(blockName);
    this.shouldDropExperience = exp;
    this.isDestructive = isDestructive;
  }

  public EntityType<VeinCreeper> getEntityType() {
    return entityType;
  }

  public void setEntityType(EntityType<VeinCreeper> entityType) {
    this.entityType = entityType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public String getBlockName() {
    return blockName;
  }

  public void setBlockName(String blockName) {
    this.blockName = blockName;
  }

  public Component getDisplayName() {
    return Component.literal(this.getBlockName()).append(" ").append(EntityType.CREEPER.getDescription());
  }

  public boolean shouldDropExperience() {
    return this.shouldDropExperience;
  }

  public boolean isDestructive() {
    return isDestructive;
  }
}
