package xyz.nifeather.lingstweaks.mixin.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;

@Mixin(BitmapProvider.Glyph.class)
public abstract class BitmapGlyphMixin implements GlyphInfo
{
    @Unique
    @NotNull
    private TweakClient lingsTweaks$tweakClient;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(float f, NativeImage nativeImage, int i, int j, int k, int l, int m, int n, CallbackInfo ci)
    {
        this.lingsTweaks$tweakClient = TweakClient.instance();
    }

    @Override
    public float getBoldOffset()
    {
        var config = lingsTweaks$tweakClient.getConfigData().bitmapSettings;

        if (!config.enableBoldOffset)
            return GlyphInfo.super.getBoldOffset();

        return config.boldOffset;
    }

    @Override
    public float getShadowOffset()
    {
        var config = lingsTweaks$tweakClient.getConfigData().bitmapSettings;

        if (!config.enableShadowOffset)
            return GlyphInfo.super.getShadowOffset();

        return config.shadowOffset;
    }
}
