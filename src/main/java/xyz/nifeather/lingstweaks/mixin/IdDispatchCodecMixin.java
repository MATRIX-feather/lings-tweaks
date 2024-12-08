package xyz.nifeather.lingstweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.IdDispatchCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;

@Mixin(IdDispatchCodec.class)
public class IdDispatchCodecMixin<B extends ByteBuf, V, T>
{
    @Inject(
            method = "encode(Lio/netty/buffer/ByteBuf;Ljava/lang/Object;)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap;getOrDefault(Ljava/lang/Object;I)I",
                    shift = At.Shift.AFTER),
            cancellable = true)
    public void tryAvoidException(B byteBuf, V object, CallbackInfo ci, @Local(ordinal = 0) int packetIndex)
    {
        if (packetIndex == -1 && TweakClient.cancelInvalidPackets)
            ci.cancel();
    }
}
