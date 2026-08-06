package com.lothrazar.veincreeper.recipe;

import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public class EntityIngredient {

  private Identifier entityId;
  private CompoundTag nbt = new CompoundTag();

  public EntityIngredient(Identifier entityId) {
    this(entityId, null);
  }

  public EntityIngredient(Identifier entityId, CompoundTag nbt) {
    this.entityId = entityId;
    this.nbt = (nbt == null ? new CompoundTag() : nbt);
  }

  public boolean isPlayer() {
    return false; //TODO
  }

  public Identifier getEntityId() {
    return entityId;
  }

  public void setEntityId(Identifier entityId) {
    this.entityId = entityId;
  }

  public CompoundTag getNbt() {
    return nbt;
  }

  public void setNbt(CompoundTag nbt) {
    this.nbt = nbt;
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityId, nbt);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    EntityIngredient other = (EntityIngredient) obj;
    return Objects.equals(entityId, other.entityId) && Objects.equals(nbt, other.nbt);
  }

  @Override
  public String toString() {
    return "EntityIngredient [entityId=" + entityId + ", nbt=" + nbt + "]";
  }
}
