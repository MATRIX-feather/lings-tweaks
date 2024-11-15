package xyz.nifeather.lingstweaks.mixin.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.NativeImage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;

@Mixin(TrueTypeGlyphProvider.Glyph.class)
public abstract class TrueTypeGlyphMixin implements GlyphInfo
{
    @Unique
    @NotNull
    private TweakClient lingsTweaks$tweakClient;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(TrueTypeGlyphProvider trueTypeGlyphProvider, float f, float g, int i, int j, float h, int k, CallbackInfo ci)
    {
        this.lingsTweaks$tweakClient = TweakClient.instance();
    }

    @Override
    public float getBoldOffset()
    {
        var config = lingsTweaks$tweakClient.getConfigData().ttfSettings;

        if (!config.enableBoldOffset)
            return GlyphInfo.super.getBoldOffset();

        return config.boldOffset;
    }

    @Override
    public float getShadowOffset()
    {
        var config = lingsTweaks$tweakClient.getConfigData().ttfSettings;

        if (!config.enableShadowOffset)
            return GlyphInfo.super.getShadowOffset();

        return config.shadowOffset;
    }
}
