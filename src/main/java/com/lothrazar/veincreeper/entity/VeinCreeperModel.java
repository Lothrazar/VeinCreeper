package com.lothrazar.veincreeper.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class VeinCreeperModel<T extends Entity> extends CreeperModel<T> {

  private int[] color = null;

  public VeinCreeperModel(ModelPart mp) {
    super(mp);
  }

  @Override
  public void renderToBuffer(PoseStack ps, VertexConsumer vc, int l, int overlay, float r, float g, float b, float a) {
    //overlay is used when explosion is happening 
    if (getColor() != null && getColor().length == 3) {
      super.renderToBuffer(ps, vc, l, overlay, getColor()[0] / 255F, getColor()[1] / 255F, getColor()[2] / 255F, a);
    }
    else if (getColor() != null && getColor().length == 4) {
      super.renderToBuffer(ps, vc, l, overlay, getColor()[0] / 255F, getColor()[1] / 255F, getColor()[2] / 255F, getColor()[3] / 255F);
    }
    else {
      super.renderToBuffer(ps, vc, l, overlay, r, g, b, a);
    }
  }

  public int[] getColor() {
    return color;
  }

  public void setColor(int[] color) {
    this.color = color;
  }
}
