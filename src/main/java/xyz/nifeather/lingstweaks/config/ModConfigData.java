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
}
