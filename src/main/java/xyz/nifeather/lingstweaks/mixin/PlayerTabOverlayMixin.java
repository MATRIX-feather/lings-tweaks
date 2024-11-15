package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.config.ModConfigData;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin
{
    @Unique
    private ModConfigData lingstweaks$configData;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void lingstweaks$init(Minecraft minecraft, Gui gui, CallbackInfo ci)
    {
        this.lingstweaks$configData = TweakClient.instance().getConfigData();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isLocalServer()Z"))
    public boolean lingstweaks$alwaysDisplayTabHead(Minecraft instance)
    {
        return lingstweaks$configData.alwaysShowHeadInTab || instance.isLocalServer();
    }
}
