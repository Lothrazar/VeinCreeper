package com.lothrazar.veincreeper.entity;

import com.lothrazar.veincreeper.ConfigManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class PartyCreeperRender extends CreeperRenderer {

  public PartyCreeperRender(EntityRendererProvider.Context ctx) {
    super(ctx);
    this.model = new PartyCreeperModel<>(ctx.bakeLayer(ModelLayers.CREEPER));
  }

  @Override
  public ResourceLocation getTextureLocation(Creeper entity) {
    if (((PartyCreeperModel) this.model).color == null) {
      final String key = ConfigManager.getKeyFromEntity(entity);
      ((PartyCreeperModel) this.model).color = ConfigManager.getCreeperColor(key);
    }
    return super.getTextureLocation(entity);
  }

  @Override
  public void render(Creeper cree, float p_115456_, float p_115457_, PoseStack ps, MultiBufferSource buff, int light) {
    super.render(cree, p_115456_, p_115457_, ps, buff, light);
  }
}
