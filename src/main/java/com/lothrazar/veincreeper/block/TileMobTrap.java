package com.lothrazar.veincreeper.block;

import com.lothrazar.veincreeper.CreeperRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class TileMobTrap extends BlockEntity {

  public static final String NBTINV = "inv";
  private final ItemStackHandler inventory = new ItemStackHandler(1);

  public TileMobTrap(BlockPos pos, BlockState state) {
    super(CreeperRegistry.TRAP_TILE.get(), pos, state);
  }

  public ItemStackHandler getInventory() {
    return inventory;
  }

  @Override
  public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    super.loadAdditional(tag, provider);
    inventory.deserializeNBT(provider, tag.getCompound(NBTINV));
  }

  @Override
  public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    super.saveAdditional(tag, provider);
    tag.put(NBTINV, inventory.serializeNBT(provider));
  }
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag syncData = super.getUpdateTag(registries);
    this.saveAdditional(syncData, registries);
    return syncData;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
    this.loadAdditional(tag, registries);
  }

}
