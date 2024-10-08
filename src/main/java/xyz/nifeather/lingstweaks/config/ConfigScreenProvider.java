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
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.blockPossibleCrashPackets.name"), config.blockPossibleCrashPackets)
                        .setTooltip(Component.translatable("options.lingsTweaks.blockPossibleCrashPackets.desc"))
                        .setDefaultValue(TweakClient.DEFAULT_BLOCK_POSSIBLE_CRASH_PACKETS)
                        .setSaveConsumer(v -> config.blockPossibleCrashPackets = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.clearTitlesOnDisconnect.name"), config.clearTitlesOnDisconnect)
                        .setDefaultValue(TweakClient.DEFAULT_CLEAR_TITLE_ON_DISCONNECT)
                        .setSaveConsumer(v -> config.clearTitlesOnDisconnect = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.playerGlow.name"), config.playerGlow)
                        .setDefaultValue(TweakClient.DEFAULT_PLAYER_GLOW)
                        .setSaveConsumer(v -> config.playerGlow = v)
                        .build()
        );

        builder.setParentScreen(parentScreen)
                .setTitle(Component.translatable("title.lingsTweaks.config"))
                .transparentBackground()
                .setSavingRunnable(tweakClient::saveConfig);

        return builder.build();
    }
}
