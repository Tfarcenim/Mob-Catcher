package tfar.mobcatcher;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class MobCatcherClient {
    public static void renderer() {
        EntityRenderers.register(ModItems.net, ThrownItemRenderer::new);
    }
}
