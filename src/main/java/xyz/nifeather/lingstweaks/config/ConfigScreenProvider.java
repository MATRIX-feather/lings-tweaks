package xyz.nifeather.lingstweaks.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import xyz.nifeather.lingstweaks.client.TweakClient;

import java.util.function.Consumer;

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
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.clearTitlesOnDisconnect.name"), config.clearTitlesOnDisconnect)
                        .setDefaultValue(TweakClient.DEFAULT_CLEAR_TITLE_ON_DISCONNECT)
                        .setSaveConsumer(v -> config.clearTitlesOnDisconnect = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.legacyVehiclePackets.name"), config.pre1_21_3_steer_vehicle_packets)
                        .setTooltip(Component.translatable("options.lingsTweaks.legacyVehiclePackets.desc"))
                        .setDefaultValue(TweakClient.DEFAULT_PRE_1_21_3_STEER_VEHICLE)
                        .setSaveConsumer(v -> config.pre1_21_3_steer_vehicle_packets = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.playerGlow.name"), config.playerGlow)
                        .setDefaultValue(TweakClient.DEFAULT_PLAYER_GLOW)
                        .setSaveConsumer(v -> config.playerGlow = v)
                        .build()
        );

        ConfigCategory categoryPacket = builder.getOrCreateCategory(Component.translatable("stat.lingsTweaks.packets"));

        categoryPacket.addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.blockPossibleCrashPackets.name"), config.blockPossibleCrashPackets)
                        .setTooltip(Component.translatable("options.lingsTweaks.blockPossibleCrashPackets.desc"))
                        .setDefaultValue(TweakClient.DEFAULT_BLOCK_POSSIBLE_CRASH_PACKETS)
                        .setSaveConsumer(v -> config.blockPossibleCrashPackets = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.blockPacketShowOnGui.name"), config.blockPacketShowOnGui)
                        .setDefaultValue(TweakClient.DEFAULT_BLOCK_PACKET_SHOW_ON_GUI)
                        .setSaveConsumer(v -> config.blockPacketShowOnGui = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.blockPacketSendChat.name"), config.blockSendChat)
                        .setDefaultValue(TweakClient.DEFAULT_BLOCK_PACKET_SEND_CHAT)
                        .setSaveConsumer(v -> config.blockSendChat = v)
                        .build()
        ).addEntry(
                entryBuilder.startStrField(Component.translatable("options.lingsTweaks.blockPacketChatContent.name"), config.blockChatMessage)
                        .setTooltip(Component.translatable("options.lingsTweaks.blockPacketChatContent.desc"))
                        .setDefaultValue(TweakClient.DEFAULT_BLOCK_CHAT_MESSAGE)
                        .setSaveConsumer(v -> config.blockChatMessage = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.dontSitOnMe.name"), config.dontSitOnMe)
                        .setTooltip(Component.translatable("options.lingsTweaks.dontSitOnMe.desc"))
                        .setDefaultValue(TweakClient.DEFAULT_DONT_SIT_ON_ME)
                        .setSaveConsumer(v -> config.dontSitOnMe = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.noPacketErrorDisconnect.name"), config.noDisconnectOnPacketError)
                        .setTooltip(Component.translatable("options.lingsTweaks.noPacketErrorDisconnect.desc"))
                        .setDefaultValue(TweakClient.DEFAULT_NO_DISCONNECT_ON_PACKET_ERROR)
                        .setSaveConsumer(v -> config.noDisconnectOnPacketError = v)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.literal("触发协议错误"), false)
                        .setSaveConsumer(v ->
                        {
                            if (!v) return;

                            var packet = new ClientboundPingPacket(0);
                            Minecraft.getInstance().player.connection.onPacketError(packet, new RuntimeException("Oops"));
                        })
                        .build()
        );

        buildFontConfiguration(
                builder.getOrCreateCategory(Component.translatable("stat.lingsTweaks.font.bitmap")),
                entryBuilder,
                config.bitmapSettings
        );

        buildFontConfiguration(
                builder.getOrCreateCategory(Component.translatable("stat.lingsTweaks.font.unihex")),
                entryBuilder,
                config.unihexSettings
        );

        buildFontConfiguration(
                builder.getOrCreateCategory(Component.translatable("stat.lingsTweaks.font.ttf")),
                entryBuilder,
                config.ttfSettings
        );

        ConfigCategory otherFontSettings = builder.getOrCreateCategory(Component.translatable("stat.lingsTweaks.font.other_settings"));

        otherFontSettings.addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.highlightBold"), config.lightenBoldTexts)
                        .setDefaultValue(false)
                        .setSaveConsumer(v -> config.lightenBoldTexts = v)
                        .build()
        ).addEntry(
                entryBuilder.startIntSlider(Component.translatable("options.lingsTweaks.highlightFactor"), (int)(config.lightenFactor * 100), 0, 100)
                        .setTextGetter((value) -> Component.literal(value + "%"))
                        .setDefaultValue(10)
                        .setSaveConsumer(v -> config.lightenFactor = v / 100f)
                        .build()
        );

        builder.setParentScreen(parentScreen)
                .setTitle(Component.translatable("title.lingsTweaks.config"))
                .transparentBackground()
                .setSavingRunnable(tweakClient::saveConfig);

        return builder.build();
    }

    private static void buildFontConfiguration(ConfigCategory category, ConfigEntryBuilder entryBuilder, FontConfiguration config)
    {
        category.addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.enableFontShadowTweak"), config.enableShadowOffset)
                        .setDefaultValue(false)
                        .setSaveConsumer(v -> config.enableShadowOffset = v)
                        .build()
        ).addEntry(
                entryBuilder.startIntSlider(Component.translatable("options.lingsTweaks.shadowOffset"), (int)(config.shadowOffset * 100), 0, 100)
                        .setDefaultValue(50)
                        .setTextGetter((value) -> Component.literal(value + "%"))
                        .setSaveConsumer(v -> config.shadowOffset = v / 100f)
                        .build()
        ).addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("options.lingsTweaks.enableFontBoldTweak"), config.enableBoldOffset)
                        .setDefaultValue(false)
                        .setSaveConsumer(v -> config.enableBoldOffset = v)
                        .build()
        ).addEntry(
                entryBuilder.startIntSlider(Component.translatable("options.lingsTweaks.boldOffset"), (int)(config.boldOffset * 100), 0, 100)
                        .setDefaultValue(50)
                        .setTextGetter((value) -> Component.literal(value + "%"))
                        .setSaveConsumer(v -> config.boldOffset = v / 100f)
                        .build()
        );

    }
}
