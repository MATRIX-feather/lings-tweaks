package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.config.ModConfigData;

import java.util.Arrays;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin
{
    @Unique
    private final double worldMax = 29999984 * 2d;

    @Unique
    private ModConfigData lingsTweaks$configData;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void lingsTweaks$init(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie, CallbackInfo ci)
    {
        this.lingsTweaks$configData = TweakClient.instance().getConfigData();
    }

    @Inject(
            method = "handleTeleportEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onEntityPosPacket(ClientboundTeleportEntityPacket packet, CallbackInfo ci)
    {
        if (lingsTweaks$isValueInRange(worldMax, packet.getX(), packet.getY(), packet.getZ()))
        {
            lingsTweaks$cancelPacket(packet, ci, "Invalid position");
        }
    }

    @Inject(
            method = "handleMovePlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onPosAndLookPacket(ClientboundPlayerPositionPacket packet, CallbackInfo ci)
    {
        if (lingsTweaks$isValueInRange(worldMax, packet.getX(), packet.getY(), packet.getZ(), packet.getXRot(), packet.getYRot(), packet.getId()))
        {
            lingsTweaks$cancelPacket(packet, ci, "Position or Yaw/Pitch/TeleportId invalid");
        }
    }

    @Inject(
            method = "handleExplosion",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onExplosion(ClientboundExplodePacket packet, CallbackInfo ci)
    {
        if (lingsTweaks$isValueInRange(worldMax,
                packet.getX(), packet.getY(), packet.getZ(),
                packet.getKnockbackX(), packet.getKnockbackY(), packet.getKnockbackZ()))
        {
            lingsTweaks$cancelPacket(packet, ci, "Position or Velocity too large");
        }
    }

    @Inject(
            method = "handleParticleEvent",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onParticle(ClientboundLevelParticlesPacket packet, CallbackInfo ci)
    {
        if (lingsTweaks$isValueInRange(worldMax,
                packet.getX(), packet.getY(), packet.getZ(),
                packet.getXDist(), packet.getYDist(), packet.getZDist(),
                packet.getMaxSpeed()))
        {
            lingsTweaks$cancelPacket(packet, ci, "Invalid position, speed, or offset data");
        }

        if (packet.getCount() >= 30000)
            lingsTweaks$cancelPacket(packet, ci, "Particle count larger than 30000");
    }

    @Unique
    private void lingsTweaks$cancelPacket(Packet<?> packet, CallbackInfo ci)
    {
        lingsTweaks$cancelPacket(packet, ci, null);
    }

    @Unique
    private void lingsTweaks$cancelPacket(Packet<?> packet, CallbackInfo ci, @Nullable String reason)
    {
        if (reason == null)
            TweakClient.LOGGER.info("Cancelling invalid %s packet from server!".formatted(packet));
        else
            TweakClient.LOGGER.info("Cancelling invalid %s packet from server: %s".formatted(packet, reason));

        ci.cancel();
    }

    @Unique
    private boolean lingsTweaks$isValueInRange(double max, double... values)
    {
        return lingsTweaks$configData.blockPossibleCrashPackets
                && Arrays.stream(values).anyMatch(v -> Math.abs(v) > max);
    }
}
