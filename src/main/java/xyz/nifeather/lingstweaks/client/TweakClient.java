package xyz.nifeather.lingstweaks.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
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

    public static final boolean DEFAULT_BLOCK_POSSIBLE_CRASH_PACKETS = true;
    public static final boolean DEFAULT_BLOCK_PACKET_SHOW_ON_GUI = false;
    public static final boolean DEFAULT_BLOCK_PACKET_SEND_CHAT = false;
    public static final String DEFAULT_BLOCK_CHAT_MESSAGE = "杂鱼服务器想要崩掉此客户端，但是这次不行喵！[%s]";

    public static final boolean DEFAULT_PLAYER_GLOW = false;
    public static final boolean DEFAULT_PRE_1_21_3_STEER_VEHICLE = false;

    public static final boolean DEFAULT_NO_DISCONNECT_ON_PACKET_ERROR = false;

    public static final boolean DEFAULT_ALT_TERRAIN_LOADING = true;

    public static final boolean DEFAULT_SEAMLESS_RECONFIGURE = false;

    public static final boolean DEFAULT_DONT_SIT_ON_ME = false;

    public static final Logger LOGGER = LoggerFactory.getLogger("Lings-Tweaks");

    @Nullable
    private static TweakClient instance;

    public static boolean cancelInvalidPackets;

    @NotNull
    public static TweakClient instance()
    {
        if (instance == null)
            instance = new TweakClient();

        return instance;
    }

    public TweakClient()
    {
        if (!TweakModClient.enabled)
            earlyInit();
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

    private boolean earlyInitDone;

    public void earlyInit()
    {
        initConfig();

        earlyInitDone = true;
    }

    public void modInit()
    {
        if (!earlyInitDone)
            earlyInit();

        ClientPlayConnectionEvents.DISCONNECT.register((packetListener, client) ->
        {
            cancelInvalidPackets = false;
        });
    }

    public void tick()
    {
        var player = Minecraft.getInstance().player;
        if (player != null) tickPlayer(player);
    }

    public void tickPlayer(Player player)
    {
        if (player instanceof LocalPlayer localPlayer)
            tickNoPassenger(localPlayer);
    }

    private TriState cancelNextTime = TriState.DEFAULT;

    // Dirty, we need to set the client's data as well
    private void tickNoPassenger(LocalPlayer player)
    {
        if (cancelNextTime == TriState.TRUE && !player.isCrouching())
        {
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY));

            cancelNextTime = TriState.DEFAULT;
            return;
        }

        var passengers = player.getPassengers();
        if (!passengers.isEmpty() && !player.isCrouching() && configData.dontSitOnMe)
        {
                player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY));

            cancelNextTime = TriState.TRUE;
            return;
        }
    }
}
