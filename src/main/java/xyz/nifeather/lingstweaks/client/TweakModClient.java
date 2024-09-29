package xyz.nifeather.lingstweaks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

@Environment(EnvType.CLIENT)
public class TweakModClient implements ClientModInitializer
{
    public static boolean enabled;

    private TweakClient tweakClient;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient()
    {
        enabled = true;

        this.tweakClient = TweakClient.instance();
        tweakClient.modInit();

        ClientTickEvents.START_CLIENT_TICK.register(this::tick);
    }

    private void tick(Minecraft minecraft)
    {
    }
}
