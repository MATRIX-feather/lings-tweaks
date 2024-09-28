package xyz.nifeather.lingstweaks.mixin;

import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.misc.ITweakWindow;

@Mixin(Window.class)
public abstract class WindowMixin implements ITweakWindow
{
    @Shadow @Final private long window;

    @Unique
    private TweakClient lingTweaks$modClient;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void lingstweaks$setMinimalSize(CallbackInfo ci)
    {
        lingTweaks$modClient = TweakClient.instance();

        this.lingtweaks$updateMinimumWindowSize();
    }

    @Unique
    @Override
    public void lingtweaks$updateMinimumWindowSize()
    {
        var config = lingTweaks$modClient.getConfigData();

        GLFW.glfwSetWindowSizeLimits(
                this.window,
                config.minimumWidth,
                config.minimumHeight,
                GLFW.GLFW_DONT_CARE,
                GLFW.GLFW_DONT_CARE
        );
    }
}
