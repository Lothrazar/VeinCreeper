package com.lothrazar.veincreeper.recipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class EntityIngredient {

  private ResourceLocation entityId;
  private CompoundTag nbt = new CompoundTag();

  public EntityIngredient(ResourceLocation entityId, CompoundTag nbt) {
    super();
    this.entityId = entityId;
    this.nbt = nbt;
  }

  public boolean isPlayer() {
    return false; //TODO
  }

  public ResourceLocation getEntityId() {
    return entityId;
  }

  public void setEntityId(ResourceLocation entityId) {
    this.entityId = entityId;
  }

  public CompoundTag getNbt() {
    return nbt;
  }

  public void setNbt(CompoundTag nbt) {
    this.nbt = nbt;
  }
}
