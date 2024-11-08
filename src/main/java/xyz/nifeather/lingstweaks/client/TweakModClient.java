package xyz.nifeather.lingstweaks.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.lwjgl.glfw.GLFW;
import xyz.nifeather.lingstweaks.config.ConfigScreenProvider;

@Environment(EnvType.CLIENT)
public class TweakModClient implements ClientModInitializer
{
    public static boolean enabled;

    private TweakClient tweakClient;

    private KeyMapping keyOpenSettings;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient()
    {
        enabled = true;

        this.tweakClient = TweakClient.instance();
        tweakClient.modInit();

        ClientTickEvents.END_CLIENT_TICK.register(this::tick);

        keyOpenSettings = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lingsTweaks.openSettings", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, "category.lingsTweaks.keybind"
        ));
    }

    private void tick(Minecraft minecraft)
    {
        tweakClient.tick();

        if (keyOpenSettings.consumeClick())
            minecraft.setScreen(ConfigScreenProvider.createScreen(tweakClient, null));
    }
}
