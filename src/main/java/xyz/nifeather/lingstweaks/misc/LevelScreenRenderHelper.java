package xyz.nifeather.lingstweaks.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LevelScreenRenderHelper
{
    private FocusableTextWidget textWidget;
    private Screen renderingScreen;

    public LevelScreenRenderHelper()
    {
    }

    public void refresh(Screen screen, Component component)
    {
        this.textWidget = new FocusableTextWidget(
                1000,
                component,
                Minecraft.getInstance().font,
                10
        );

        this.renderingScreen = screen;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta)
    {
        if (textWidget == null || renderingScreen == null)
            return;

        var centerX = Math.round((guiGraphics.guiWidth() - textWidget.getWidth()) / 2f);
        var centerY = Math.round((guiGraphics.guiHeight() - textWidget.getHeight()) / 3.5f);

        textWidget.setX(centerX);
        textWidget.setY(centerY);

        Screen.renderMenuBackgroundTexture(
                guiGraphics,
                ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_background.png"),
                0, 0,
                0, 0,
                renderingScreen.width, renderingScreen.height
        );

        textWidget.render(guiGraphics, mouseX, mouseY, tickDelta);
    }
}
