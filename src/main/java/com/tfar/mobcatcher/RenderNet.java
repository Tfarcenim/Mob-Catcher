package com.tfar.mobcatcher;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class RenderNet extends RenderSnowball {

  private final ResourceLocation entityTexture;

  public RenderNet(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn,ResourceLocation entityTexture) {
    super(renderManagerIn, itemIn, itemRendererIn);
    this.entityTexture = entityTexture;
  }

  @Override
  protected ResourceLocation getEntityTexture(Entity entity) {
    return entityTexture;
  }
}
