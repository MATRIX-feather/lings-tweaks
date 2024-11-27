package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.misc.ColorUtils;
import xyz.nifeather.lingstweaks.misc.LevelScreenRenderHelper;

@Mixin(ServerReconfigScreen.class)
public abstract class ServerReconfigScreenMixin extends Screen
{
    @Unique
    private final TweakClient lingsTweaks$tweakClient = TweakClient.instance();

    @Unique
    private final LevelScreenRenderHelper lingsTweaks$renderHelper = new LevelScreenRenderHelper();

    protected ServerReconfigScreenMixin(Component component)
    {
        super(component);
    }

    @Inject(
            method = "init",
            at = @At("HEAD")
    )
    public void onScreenInit(CallbackInfo ci)
    {
        lingsTweaks$renderHelper.refresh(this, this.title);
    }

    @Unique
    private final int lingsTweaks$bgColor = ColorUtils.forOpacity(ColorUtils.fromHex("#333333"), 0.3f).getColor();

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f)
    {
        if (lingsTweaks$tweakClient.getConfigData().altLoadingTerrain)
            guiGraphics.fill(0, 0, this.width, this.height, lingsTweaks$bgColor);
        else
            super.renderBackground(guiGraphics, i, j, f);
    }
}
