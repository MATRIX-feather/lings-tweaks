package xyz.nifeather.lingstweaks.misc;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class HudRenderHelper
{
    public static final HudRenderHelper INSTANCE = new HudRenderHelper();

    @Nullable
    private FocusableTextWidget textWidget;

    public void renderText(@Nullable Component text, Font font)
    {
        if (text == null)
        {
            this.textWidget = null;
            return;
        }

        var widget = new FocusableTextWidget(1000, text, font, 10);

        this.textWidget = widget;
    }

    public void onRender(GuiGraphics drawContext, int mouseX, int mouseY, float tickDelta)
    {
        if (textWidget != null)
        {
            var centerX = Math.round((drawContext.guiWidth() - textWidget.getWidth()) / 2f);
            var centerY = Math.round((drawContext.guiHeight() - textWidget.getHeight()) / 3.5f);

            textWidget.setX(centerX);
            textWidget.setY(centerY);

            textWidget.render(drawContext, mouseX, mouseY, tickDelta);
        }
    }
}
