package xyz.nifeather.lingstweaks.mixin.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.lingstweaks.client.TweakClient;

@Mixin(UnihexProvider.Glyph.class)
public abstract class UnihexGlyphMixin implements GlyphInfo
{
    @Unique
    @NotNull
    private TweakClient lingsTweaks$tweakClient;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(UnihexProvider.LineData lineData, int i, int j, CallbackInfo ci)
    {
        this.lingsTweaks$tweakClient = TweakClient.instance();
    }

    @Inject(method = "getShadowOffset", at = @At("HEAD"), cancellable = true)
    public void onGetShadow(CallbackInfoReturnable<Float> cir)
    {
        var config = lingsTweaks$tweakClient.getConfigData().unihexSettings;

        if (!config.enableShadowOffset) return;

        cir.setReturnValue(config.shadowOffset);
    }

    @Inject(method = "getBoldOffset", at = @At("HEAD"), cancellable = true)
    public void onGetBold(CallbackInfoReturnable<Float> cir)
    {
        var config = lingsTweaks$tweakClient.getConfigData().unihexSettings;

        if (!config.enableBoldOffset) return;

        cir.setReturnValue(config.boldOffset);
    }
}
