package xyz.nifeather.lingstweaks.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.phys.Vec3;
import xyz.nifeather.lingstweaks.client.TweakClient;

import java.util.function.Supplier;

public class OpacityRenderHelper
{
    public void onRender(EntityModel<?> model,
                         PoseStack poseStack,
                         VertexConsumer vertexConsumer,
                         int light, int overlay, int color,
                         LivingEntityRenderState state,
                         Supplier<VertexConsumer> vertexConsumerSupplier)
    {
        Runnable defaultRender = () ->
        {
            model.renderToBuffer(poseStack, vertexConsumer, light, overlay, color);
        };

        var config = TweakClient.instance().getConfigData();

        var currentPlayer = Minecraft.getInstance().player;
        if (currentPlayer == null || !config.entityFadeEnabled)
        {
            defaultRender.run();
            return;
        }

        if (state instanceof PlayerRenderState playerRenderState && playerRenderState.id == currentPlayer.getId())
        {
            defaultRender.run();
            return;
        }

        var distance = currentPlayer.position().distanceTo(new Vec3(state.x, state.y, state.z));
        var opacity = Math.clamp(distance / config.entityFadeStartDistance, config.entityFadeMinOpacity, 1d);

        if (opacity == 1d)
        {
            defaultRender.run();
            return;
        }

        int alpha = (int) (opacity * 255);
        int colorModified = (color & 0xFFFFFF) | alpha << 24;
        model.renderToBuffer(poseStack, vertexConsumerSupplier.get(), light, -1, colorModified);
    }
}
