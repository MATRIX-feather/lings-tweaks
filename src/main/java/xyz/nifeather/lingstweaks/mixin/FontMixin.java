package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.lingstweaks.client.TweakClient;

@Mixin(Font.StringRenderOutput.class)
public class FontMixin
{
    @Inject(
            method = "accept",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Style;getColor()Lnet/minecraft/network/chat/TextColor;")
    )
    public void onAccept(int i, Style style, int j, CallbackInfoReturnable<Boolean> cir)
    {
        lingsTweaks$currentStyle = style;
    }

    @Unique
    private Style lingsTweaks$currentStyle;

    @ModifyVariable(
            method = "accept",
            at = @At(value = "STORE"),
            index = 9
    )
    public int tweakTextColor(int rawColor)
    {
        var config = TweakClient.instance().getConfigData();

        if (config.lightenBoldTexts && lingsTweaks$currentStyle.isBold())
            return ARGB.scaleRGB(rawColor, 1f + config.lightenFactor);
        else
            return rawColor;
    }
}
