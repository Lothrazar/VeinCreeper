package com.lothrazar.veincreeper.entity;

import com.lothrazar.veincreeper.conf.CreeperConfigManager;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

@SuppressWarnings("rawtypes")
public class VeinCreeperRender extends CreeperRenderer {

  public static boolean doRefresh = false;

  public VeinCreeperRender(EntityRendererProvider.Context ctx) {
    super(ctx);
    this.model = new VeinCreeperModel<>(ctx.bakeLayer(ModelLayers.CREEPER));
  }

  @Override
  public ResourceLocation getTextureLocation(Creeper entity) {
    var cm = (VeinCreeperModel) this.model;
    if (cm.getColor() == null
        || doRefresh) {
      final String key = CreeperConfigManager.getKeyFromEntity(entity);
      var col = CreeperConfigManager.getCreeperColor(key);
      cm.setColor(new int[] { col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() });
    }
    return super.getTextureLocation(entity);
  }
}
