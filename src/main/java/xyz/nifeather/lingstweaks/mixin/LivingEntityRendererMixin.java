package xyz.nifeather.lingstweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nifeather.lingstweaks.misc.OpacityRenderHelper;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
{
    @Shadow protected abstract @Nullable RenderType getRenderType(S livingEntityRenderState, boolean bl, boolean bl2, boolean bl3);

    @Shadow protected abstract boolean isBodyVisible(S livingEntityRenderState);

    @Unique
    private final OpacityRenderHelper lt$renderHelper = new OpacityRenderHelper();

    @WrapOperation(
            method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V")
    )
    private void lingsTweaks$onRender(EntityModel<?> model,
                                      PoseStack poseStack,
                                      VertexConsumer vertexConsumer,
                                      int light, int overlay, int color,
                                      Operation<Void> original,
                                      @Local(argsOnly = true) S state,
                                      @Local(argsOnly = true) MultiBufferSource bufferSource)
    {
        lt$renderHelper.onRender(model, poseStack, vertexConsumer,
                light, overlay, color, state, () ->
                {
                    var transcult = this.getRenderType(state, this.isBodyVisible(state), true, state.appearsGlowing);
                    return bufferSource.getBuffer(transcult);
                });
    }
}
