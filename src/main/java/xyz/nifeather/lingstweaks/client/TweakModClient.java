package xyz.nifeather.lingstweaks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TweakModClient implements ClientModInitializer
{
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient()
    {
        TweakClient.instance();
    }
}
