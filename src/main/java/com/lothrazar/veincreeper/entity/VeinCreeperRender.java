package com.lothrazar.veincreeper.entity;

import com.lothrazar.veincreeper.conf.CreeperConfigManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class VeinCreeperRender extends CreeperRenderer {

  public static boolean doRefresh = false;

  public VeinCreeperRender(EntityRendererProvider.Context ctx) {
    super(ctx);
    this.model = new VeinCreeperModel<>(ctx.bakeLayer(ModelLayers.CREEPER));
  }

  @Override
  public ResourceLocation getTextureLocation(Creeper entity) {
    if (((VeinCreeperModel) this.model).color == null
        || doRefresh) {
      final String key = CreeperConfigManager.getKeyFromEntity(entity);
      ((VeinCreeperModel) this.model).color = CreeperConfigManager.getCreeperColor(key);
    }
    return super.getTextureLocation(entity);
  }

  @Override
  public void render(Creeper cree, float p_115456_, float p_115457_, PoseStack ps, MultiBufferSource buff, int light) {
    super.render(cree, p_115456_, p_115457_, ps, buff, light);
  }
}
