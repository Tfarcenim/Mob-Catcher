package com.tfar.mobcatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;


public class NetRenderer extends SpriteRenderer<NetEntity>{

  public NetRenderer(EntityRendererManager render) {
    super(render, Minecraft.getInstance().getItemRenderer());
  }
}
