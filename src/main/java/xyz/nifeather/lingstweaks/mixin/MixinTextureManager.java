package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
public class MixinTextureManager
{
    @Shadow @Final private static Logger LOGGER;

    @Inject(
            method = "release",
            at = @At("HEAD")
    )
    public void lingsTweaks$onRelease(ResourceLocation resourceLocation, CallbackInfo ci)
    {
    }
}
