package com.lothrazar.veincreeper.event;

import com.lothrazar.veincreeper.CreeperRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TrapCreeperEvents {

  @SubscribeEvent
  public void onHit(PlayerInteractEvent.RightClickBlock event) {
    if (event.getHand() == InteractionHand.OFF_HAND) {
      return;
    }
    BlockPos pos = event.getPos();
    Player player = event.getEntity();
    Level level = player.getCommandSenderWorld();
    BlockState state = level.getBlockState(pos);
    if (state.getBlock() == CreeperRegistry.TRAP.get()) {
      var caps = level.getBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
      if (caps != null) {
        if (event.getItemStack().isEmpty()) {
          var found = caps.extractItem(0, 64, false);
          player.setItemInHand(event.getHand(), found);
          //          player.addItem(found);
        }
        else {
          //if 1 at a time, do split. otherwise just dump everything
          ItemStack result = caps.insertItem(0, event.getItemStack().copy(), false);
          event.getItemStack().setCount(result.getCount());
        }
      }
    }
  }
}
