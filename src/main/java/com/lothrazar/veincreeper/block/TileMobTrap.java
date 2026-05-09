package com.lothrazar.veincreeper.block;

import com.lothrazar.library.entity.BlockEntityFlib;
import com.lothrazar.veincreeper.CreeperRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class TileMobTrap extends BlockEntityFlib {

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
  public int getField(int k) {
    return 0;
  }

  @Override
  public void setField(int k, int val) {}
}
