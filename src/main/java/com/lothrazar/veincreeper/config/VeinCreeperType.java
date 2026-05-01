package com.lothrazar.veincreeper.config;

import java.awt.Color;
import java.util.function.Supplier;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

public class VeinCreeperType {

  private String id;
  private Color colour;
  private EntityType<VeinCreeper> entityType; //hold just for registries
  private String name;//for display name only
  private boolean shouldDropExperience;
  public Supplier<EntityType<VeinCreeper>> hack;
  private boolean isDestructive;
  private float radius = 2.1F;
  private boolean doesFire;
  private boolean createSpawnEgg;

  public VeinCreeperType(VeinCreeperDTO in) {
    this.id = in.id();
    this.colour = Color.decode(in.colour()); // map string hex color to awt obj
    this.name = in.displayName();
    this.shouldDropExperience = in.dropsExp();
    this.isDestructive = in.destructive();
    this.setRadius(in.radius());
    this.doesFire = in.fire() ;
    this.createSpawnEgg = in.spawnEgg();
  }

  public boolean createSpawnEgg() {
    return this.createSpawnEgg;
  };
//  public VeinCreeperType(String id, Color col, String blockName, boolean exp, boolean isDestructive, float radius, boolean fire) {
//    this.setId(id);
//    this.setColor(col);
//    this.setBlockName(blockName);
//    this.shouldDropExperience = exp;
//    this.isDestructive = isDestructive;
//    this.setRadius(radius);
//    this.doesFire = fire;
//  }

  public EntityType<VeinCreeper> getEntityType() {
    return entityType;
  }

  public void setEntityType(EntityType<VeinCreeper> entityType) {
    this.entityType = entityType;
  }

  public String getId() {
    return id;
  }

//  public void setId(String id) {
//    this.id = id;
//  }

  public Color getColor() {
    return colour;
  }

//  public void setColor(Color color) {
//    this.colour = color;
//  }

  public String getBlockName() {
    return name;
  }

//  public void setBlockName(String blockName) {
//    this.displayName = blockName;
//  }

  public Component getDisplayName() {
    return Component.literal(this.getBlockName()).append(" ").append(EntityType.CREEPER.getDescription());
  }

  public boolean shouldDropExperience() {
    return this.shouldDropExperience;
  }

  public boolean isDestructive() {
    return isDestructive;
  }

  public float getRadius() {
    return this.radius;
  }

  public void setRadius(float f) {
    if (f < 0.5) {
      f = 0.5F;
    }
    this.radius = f;
  }

  public boolean doesFire() {
    return this.doesFire;
  }
}
