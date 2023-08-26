package com.lothrazar.veincreeper.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class PartyCreeperModel<T extends Entity> extends CreeperModel<T> {

  int[] color = null;

  public PartyCreeperModel(ModelPart mp) {
    super(mp);
  }

  @Override
  public void renderToBuffer(PoseStack ps, VertexConsumer vc, int l, int overlay, float r, float g, float b, float a) {
    //overlay is used when explosion is happening
    //TODO: from property
    //  //(String.format("creep render %s, %s, %s ;; alpha = %s",r,g,b,a));
    if (color != null && color.length == 3) {
      super.renderToBuffer(ps, vc, l, overlay, color[0] / 255F, color[1] / 255F, color[2] / 255F, a);
    }
    else {
      super.renderToBuffer(ps, vc, l, overlay, r, g, b, a);
    }
  }
}
