package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.misc.HudRenderHelper;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Shadow @Nullable public LocalPlayer player;

    @Shadow protected abstract void updateScreenAndTick(Screen screen);

    @Shadow @Final public Gui gui;

    @Shadow public abstract @Nullable ClientPacketListener getConnection();

    @Shadow @Final private GameNarrator narrator;

    @Shadow @Final public Font font;
    @Unique
    private final TweakClient lingsTweaks$tweakClient = TweakClient.instance();

    @Unique
    private boolean clientWorldFrozen;

    @Inject(
            method = "setLevel",
            at = @At("HEAD")
    )
    public void onSet(ClientLevel clientLevel, ReceivingLevelScreen.Reason reason, CallbackInfo ci)
    {
        if (this.clientWorldFrozen)
            this.player = null;

        TweakClient.cancelInvalidPackets = false;
        this.clientWorldFrozen = false;
        HudRenderHelper.INSTANCE.renderText(null, this.font);
    }

    @Inject(
            method = "clearClientLevel",
            at = @At("HEAD"), cancellable = true)
    public void onHead(Screen screen, CallbackInfo ci)
    {
        if (lingsTweaks$tweakClient.getConfigData().seamlessReconfigure)
        {
            ClientPacketListener clientPacketListener = this.getConnection();

            if (clientPacketListener != null)
                clientPacketListener.clearLevel();

            this.narrator.clear();

            if (screen instanceof ServerReconfigScreen serverReconfigScreen)
            {
                this.updateScreenAndTick(null);
                HudRenderHelper.INSTANCE.renderText(screen.getTitle(), this.font);
            }
            else
            {
                this.updateScreenAndTick(screen);
            }

            this.gui.onDisconnected();
            SkullBlockEntity.clear();

            TweakClient.cancelInvalidPackets = true;
            clientWorldFrozen = true;
            ci.cancel();
        }
        else
        {
            clientWorldFrozen = false;
        }
    }
}
