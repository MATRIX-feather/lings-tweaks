package xyz.nifeather.lingstweaks.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xyz.nifeather.lingstweaks.client.TweakClient;

public class ConfigScreenProvider
{
    public static Screen createScreen(TweakClient tweakClient, Screen parentScreen)
    {
        ConfigBuilder builder = ConfigBuilder.create();
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory categoryGeneral = builder.getOrCreateCategory(Component.translatable("stat.generalButton"));

        var config = tweakClient.getConfigData();

        categoryGeneral.addEntry(
                entryBuilder.startIntField(Component.translatable("options.lingsTweaks.minWidth.name"), config.minimumWidth)
                        .setTooltip(Component.translatable("options.lingsTweaks.minWidth.desc"))
                        .setDefaultValue(TweakClient.DEFAULT_MINIMUM_WIDTH)
                        .setSaveConsumer(v -> config.minimumWidth = v)
                        .build()
        ).addEntry(
                entryBuilder.startIntField(Component.translatable("options.lingsTweaks.minHeight.name"), config.minimumHeight)
                        .setTooltip(Component.translatable("options.lingsTweaks.minHeight.desc"))
                        .setDefaultValue(TweakClient.DEFAULT_MINIMUM_HEIGHT)
                        .setSaveConsumer(v -> config.minimumHeight = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.alwaysShowHeadInTabOverlay.name"), config.alwaysShowHeadInTab)
                        .setDefaultValue(TweakClient.DEFAULT_ALWAYS_SHOW_HEAD_IN_TAB)
                        .setSaveConsumer(v -> config.alwaysShowHeadInTab = v)
                        .build()
        );

        builder.setParentScreen(parentScreen)
                .setTitle(Component.translatable("title.lingsTweaks.config"))
                .transparentBackground()
                .setSavingRunnable(tweakClient::saveConfig);

        return builder.build();
    }
}
