package xyz.nifeather.lingstweaks.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import xyz.nifeather.lingstweaks.client.TweakClient;

@Config(name = "lings-tweaks")
public class ModConfigData implements ConfigData
{
    public int minimumWidth = TweakClient.DEFAULT_MINIMUM_WIDTH;
    public int minimumHeight = TweakClient.DEFAULT_MINIMUM_HEIGHT;

    public boolean alwaysShowHeadInTab = TweakClient.DEFAULT_ALWAYS_SHOW_HEAD_IN_TAB;

    public boolean blockPossibleCrashPackets = TweakClient.DEFAULT_BLOCK_POSSIBLE_CRASH_PACKETS;
    public boolean blockPacketShowOnGui = TweakClient.DEFAULT_BLOCK_PACKET_SHOW_ON_GUI;
    public boolean blockSendChat = TweakClient.DEFAULT_BLOCK_PACKET_SEND_CHAT;
    public String blockChatMessage = TweakClient.DEFAULT_BLOCK_CHAT_MESSAGE;

    public boolean clearTitlesOnDisconnect = TweakClient.DEFAULT_CLEAR_TITLE_ON_DISCONNECT;

    public boolean playerGlow = TweakClient.DEFAULT_PLAYER_GLOW;

    public boolean pre1_21_3_steer_vehicle_packets = TweakClient.DEFAULT_PRE_1_21_3_STEER_VEHICLE;

    public boolean dontSitOnMe = TweakClient.DEFAULT_DONT_SIT_ON_ME;

    public boolean lightenBoldTexts = false;
    public float lightenFactor = 0.1f;

    public FontConfiguration bitmapSettings = new FontConfiguration();
    public FontConfiguration unihexSettings = new FontConfiguration();
    public FontConfiguration ttfSettings = new FontConfiguration();

    public boolean noDisconnectOnPacketError = TweakClient.DEFAULT_NO_DISCONNECT_ON_PACKET_ERROR;

    public boolean altLoadingTerrain = TweakClient.DEFAULT_ALT_TERRAIN_LOADING;
    public boolean seamlessReconfigure = TweakClient.DEFAULT_SEAMLESS_RECONFIGURE;
}
