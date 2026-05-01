package tfar.mobcatcher.client;

import net.fabricmc.api.ClientModInitializer;
import tfar.mobcatcher.MobCatcherClient;

public class MobCatcherClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MobCatcherClient.renderer();
    }
}
