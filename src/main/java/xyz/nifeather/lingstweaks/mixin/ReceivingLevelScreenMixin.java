package xyz.nifeather.lingstweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.misc.LevelScreenRenderHelper;

import java.util.function.BooleanSupplier;

@Mixin(ReceivingLevelScreen.class)
public abstract class ReceivingLevelScreenMixin extends Screen
{
    @Shadow @Final private BooleanSupplier levelReceived;

    @Shadow @Final private static Component DOWNLOADING_TERRAIN_TEXT;

    protected ReceivingLevelScreenMixin(Component component)
    {
        super(component);
    }

    @Unique
    private final LevelScreenRenderHelper lingsTweaks$renderHelper = new LevelScreenRenderHelper();

    @Unique
    private final TweakClient lingsTweaks$tweakClient = TweakClient.instance();

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    public void onInit(BooleanSupplier booleanSupplier, ReceivingLevelScreen.Reason reason, CallbackInfo ci)
    {
        lingsTweaks$renderHelper.refresh(this, DOWNLOADING_TERRAIN_TEXT);
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V")
    )
    public void wrapRenderString(GuiGraphics instance, Font font, Component component, int i, int j, int k, Operation<Void> original)
    {
        if (!lingsTweaks$tweakClient.getConfigData().altLoadingTerrain)
            original.call(instance, font, component, i, j, k);
    }

    @Inject(
            method = "renderBackground",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onRenderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci)
    {
        if (lingsTweaks$tweakClient.getConfigData().altLoadingTerrain)
        {
            lingsTweaks$renderHelper.render(guiGraphics, i, j, f);
            ci.cancel();
        }
    }
}
