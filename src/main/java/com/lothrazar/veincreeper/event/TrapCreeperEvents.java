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
          event.setCanceled(true);
        }
        else {
          //if 1 at a time, do split. otherwise just dump everything
          int original = event.getItemStack().getCount();
          ItemStack result = caps.insertItem(0, event.getItemStack().copy(), false);
          if (original != result.getCount()) {
            event.getItemStack().setCount(result.getCount());
          }
          else {
            //if caps item was empty we would not end up here.
            //so they are both not empt 
            var fromHand = event.getItemStack().copy();
            var fromBlock = caps.extractItem(0, 64, false);
            player.setItemInHand(event.getHand(), fromBlock);
            caps.insertItem(0, fromHand, false);
            //so extract first and do the empty flow then replace hand
          }
          event.setCanceled(true);
        }
      }
    }
  }
}
