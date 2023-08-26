package com.lothrazar.veincreeper.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class PartyCreeperModel<T extends Entity> extends CreeperModel<T> {

  public PartyCreeperModel(ModelPart mp) {
    super(mp);
  }

  public void renderToBuffer(PoseStack ps, VertexConsumer vc, int l, int overlay, float r, float g, float b, float a) {
    //overlay is used when explosion is happening
    g = 0;
    b = 0;
    //TODO: from property
    //  //(String.format("creep render %s, %s, %s ;; alpha = %s",r,g,b,a));
    super.renderToBuffer(ps, vc, l, overlay, r, g, b, a);
  }
}
