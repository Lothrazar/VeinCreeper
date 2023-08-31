package com.lothrazar.veincreeper.conf;

import java.util.function.Supplier;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

public class CreepType {

  private String id;
  private int[] color = new int[4];//render overrides. format is RGBA in range [0,255] render maps this to [0,1]
  private EntityType<VeinCreeper> entityType; //hold just for registries
  private String blockName;//for display name only
  private boolean shouldDropExperience;
  public Supplier<EntityType<VeinCreeper>> hack;

  public CreepType(String id, int[] col, String blockName, boolean exp) {
    if (col.length != 4) {
      throw new IllegalArgumentException("Check color-config values and try again for id=" + id);
    }
    this.setId(id);
    this.setColor(col);
    this.setBlockName(blockName);
    this.shouldDropExperience = exp;
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

  public Component getDisplayName() {
    return Component.literal(this.getBlockName()).append(" ").append(EntityType.CREEPER.getDescription());
  }

  public boolean shouldDropExperience() {
    return this.shouldDropExperience;
  }
}
