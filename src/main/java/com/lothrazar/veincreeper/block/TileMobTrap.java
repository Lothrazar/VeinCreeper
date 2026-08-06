package com.lothrazar.veincreeper.block;

import com.lothrazar.veincreeper.CreeperRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
  protected void loadAdditional(ValueInput input) {
    super.loadAdditional(input);
    inventory.deserialize(input.childOrEmpty(NBTINV));
  }

  @Override
  protected void saveAdditional(ValueOutput output) {
    super.saveAdditional(output);
    inventory.serialize(output.child(NBTINV));
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
    this.saveAdditional(output);
    return output.buildResult();
  }

  @Override
  public void preRemoveSideEffects(BlockPos pos, BlockState state) {
    if (level != null) {
      for (int i = 0; i < inventory.getSlots(); ++i) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
      }
    }
  }
}
