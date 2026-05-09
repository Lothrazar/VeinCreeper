package com.lothrazar.veincreeper.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;

public class VeinCreeperModel<T extends Entity> extends CreeperModel<T> {

  private int[] color = null;

  public VeinCreeperModel(ModelPart mp) {
    super(mp);
  }

  @Override
  public void renderToBuffer(PoseStack ps, VertexConsumer vc, int packedLight, int packedOverlay, int packedColor) {
    if (color != null && color.length >= 3) {
      int r = color[0] & 0xFF;
      int g = color[1] & 0xFF;
      int b = color[2] & 0xFF;
      int a = color.length >= 4 ? color[3] & 0xFF : FastColor.ARGB32.alpha(packedColor);
      super.renderToBuffer(ps, vc, packedLight, packedOverlay, FastColor.ARGB32.color(a, r, g, b));
    }
    else {
      super.renderToBuffer(ps, vc, packedLight, packedOverlay, packedColor);
    }
  }

  public int[] getColor() {
    return color;
  }

  public void setColor(int[] color) {
    this.color = color;
  }
}
