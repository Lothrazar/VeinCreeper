package com.lothrazar.veincreeper.block;

import com.lothrazar.library.entity.BlockEntityFlib;
import com.lothrazar.veincreeper.CreeperRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileMobTrap extends BlockEntityFlib {

  public static final String NBTINV = "inv";
  private ItemStackHandler inventory = new ItemStackHandler(1);
  private LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

  public TileMobTrap(BlockPos pos, BlockState state) {
    super(CreeperRegistry.TRAP_TILE.get(), pos, state);
  }

  @Override
  public void invalidateCaps() {
    inventoryCap.invalidate();
    super.invalidateCaps();
  }

  @Override
  public void load(CompoundTag tag) {
    inventory.deserializeNBT(tag.getCompound(NBTINV));
    super.load(tag);
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.put(NBTINV, inventory.serializeNBT());
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) {
      return inventoryCap.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public int getField(int k) {
    return 0;
  }

  @Override
  public void setField(int k, int val) {}
}
