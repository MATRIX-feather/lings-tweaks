package xyz.nifeather.lingstweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.config.ModConfigData;

import java.nio.file.Path;
import java.util.Optional;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerMixin
{
    @Unique
    private ModConfigData lingsTweaks$config;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    public void onInit(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie, CallbackInfo ci)
    {
        this.lingsTweaks$config = TweakClient.instance().getConfigData();
    }

    @Inject(
            method = "onPacketError",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;disconnect(Lnet/minecraft/network/DisconnectionDetails;)V",
                    shift = At.Shift.BEFORE),
            cancellable = true
    )
    public void onPacketError(Packet<?> packet, Exception exception, CallbackInfo ci, @Local(ordinal = 0) Optional<Path> reportPathOptional)
    {
        if (lingsTweaks$config.noDisconnectOnPacketError)
        {
            ci.cancel();

            var clientPlayer = Minecraft.getInstance().player;
            assert clientPlayer != null;

            clientPlayer.displayClientMessage(Component.translatable("message.lingsTweaks.handledPacketError"), false);
            clientPlayer.displayClientMessage(Component.translatable("message.lingsTweaks.handledPacketError2"), false);

            if (reportPathOptional != null && reportPathOptional.isPresent())
            {
                var path = reportPathOptional.get();

                clientPlayer.displayClientMessage(
                        Component.translatable("message.lingsTweaks.handledPacketError3")
                                .withStyle(Style.EMPTY.withClickEvent(
                                        new ClickEvent(
                                                ClickEvent.Action.OPEN_FILE, path.toString()
                                        )
                                ))
                                .withStyle(Style.EMPTY.withUnderlined(true)),
                        false
                );
            }
        }
    }
}
