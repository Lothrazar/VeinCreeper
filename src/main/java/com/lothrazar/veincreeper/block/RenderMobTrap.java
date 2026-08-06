package com.lothrazar.veincreeper.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.jspecify.annotations.Nullable;

public class RenderMobTrap implements BlockEntityRenderer<TileMobTrap, RenderMobTrap.TrapRenderState> {

  public RenderMobTrap(BlockEntityRendererProvider.Context d) {}

  @Override
  public TrapRenderState createRenderState() {
    return new TrapRenderState();
  }

  @Override
  public void extractRenderState(TileMobTrap blockEntity, TrapRenderState state, float partialTicks, Vec3 cameraPosition,
      ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    IItemHandler itemHandler = blockEntity.getInventory();
    ItemStack stack = itemHandler == null ? ItemStack.EMPTY : itemHandler.getStackInSlot(0);
    if (!stack.isEmpty()) {
      Minecraft.getInstance().getItemModelResolver().updateForTopItem(
          state.itemRenderState, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, (int) blockEntity.getBlockPos().asLong());
    }
    else {
      state.itemRenderState.clear();
    }
  }

  @Override
  public void submit(TrapRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
    if (!state.itemRenderState.isEmpty()) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 0.44921875F, 0.5F);
      poseStack.scale(0.375F, 0.375F, 0.375F);
      state.itemRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
      poseStack.popPose();
    }
  }

  public static class TrapRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
  }
}
