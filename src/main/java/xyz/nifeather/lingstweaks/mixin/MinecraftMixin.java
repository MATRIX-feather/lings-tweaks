package xyz.nifeather.lingstweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;

import java.util.function.BooleanSupplier;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Shadow @Nullable public ClientLevel level;
    @Shadow @Nullable public LocalPlayer player;

    @Shadow protected abstract void updateScreenAndTick(Screen screen);

    @Shadow @Final public Gui gui;

    @Shadow public abstract @Nullable ClientPacketListener getConnection();

    @Shadow @Final private GameNarrator narrator;

    @Unique
    private final TweakClient lingsTweaks$tweakClient = TweakClient.instance();

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")
    )
    public void muteSend(ClientPacketListener instance, Packet<?> packet, Operation<Void> original)
    {
        if (!clientWorldFrozen)
            original.call(instance, packet);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tick(Ljava/util/function/BooleanSupplier;)V")
    )
    public void muteTick(ClientLevel instance, BooleanSupplier booleanSupplier, Operation<Void> original)
    {
        if (!clientWorldFrozen)
            original.call(instance, booleanSupplier);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tickEntities()V")
    )
    public void muteTick(ClientLevel instance, Operation<Void> original)
    {
        if (!clientWorldFrozen)
            original.call(instance);
    }

    @Unique
    private boolean clientWorldFrozen;

    @Inject(
            method = "setLevel",
            at = @At("HEAD")
    )
    public void onSet(ClientLevel clientLevel, ReceivingLevelScreen.Reason reason, CallbackInfo ci)
    {
        if (this.clientWorldFrozen)
            this.player = null;

        this.clientWorldFrozen = false;
    }

    @Inject(
            method = "clearClientLevel",
            at = @At("HEAD"), cancellable = true)
    public void onHead(Screen screen, CallbackInfo ci)
    {
        if (lingsTweaks$tweakClient.getConfigData().seamlessReconfigure)
        {
            ClientPacketListener clientPacketListener = this.getConnection();

            if (clientPacketListener != null)
                clientPacketListener.clearLevel();

            this.narrator.clear();

            this.updateScreenAndTick(screen);
            this.gui.onDisconnected();
            SkullBlockEntity.clear();

            clientWorldFrozen = true;
            ci.cancel();
        }
        else
        {
            clientWorldFrozen = false;
        }
    }
}
