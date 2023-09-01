package com.lothrazar.veincreeper.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class RenderMobTrap implements BlockEntityRenderer<TileMobTrap> {

  public RenderMobTrap(BlockEntityRendererProvider.Context d) {}

  @Override
  public void render(TileMobTrap tile, float v, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlayLight) {
    IItemHandler itemHandler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
    if (itemHandler != null) {
      ItemStack stack = itemHandler.getStackInSlot(0);
      if (!stack.isEmpty()) {
        matrixStack.pushPose();
        matrixStack.translate(0.5F, 0.44921875F, 0.5F);
        matrixStack.scale(0.375F, 0.375F, 0.375F);
        CampfireRenderer test;//TODO fix
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, overlayLight, matrixStack, buffer, tile.getLevel(), (int) tile.getBlockPos().asLong());
        matrixStack.popPose();
      }
    }
  }
}
