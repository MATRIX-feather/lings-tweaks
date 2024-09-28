package xyz.nifeather.lingstweaks.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.lingstweaks.config.ModConfigData;
import xyz.nifeather.lingstweaks.misc.ITweakWindow;

public class TweakClient
{
    public static final int DEFAULT_MINIMUM_WIDTH = 854;
    public static final int DEFAULT_MINIMUM_HEIGHT = 480;
    public static final boolean DEFAULT_ALWAYS_SHOW_HEAD_IN_TAB = true;

    public static final Logger LOGGER = LoggerFactory.getLogger("Lings-Tweaks");

    @Nullable
    private static TweakClient instance;

    @NotNull
    public static TweakClient instance()
    {
        if (instance == null)
            instance = new TweakClient();

        return instance;
    }

    public TweakClient()
    {
        initConfig();
    }

    private ModConfigData configData;
    private ConfigHolder<ModConfigData> configHolder;

    public ModConfigData getConfigData()
    {
        return configData;
    }

    public void saveConfig()
    {
        configHolder.save();

        this.updateFromConfig();
    }

    public void updateFromConfig()
    {
        var window = (ITweakWindow) (Object)Minecraft.getInstance().getWindow();

        window.lingtweaks$updateMinimumWindowSize();
    }

    private void initConfig()
    {
        AutoConfig.register(ModConfigData.class, GsonConfigSerializer::new);

        this.configHolder = AutoConfig.getConfigHolder(ModConfigData.class);
        configHolder.load();

        this.configData = configHolder.getConfig();
    }
}
