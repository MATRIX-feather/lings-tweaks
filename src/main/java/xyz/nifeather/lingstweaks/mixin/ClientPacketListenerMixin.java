package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.Relative;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.config.ModConfigData;
import xyz.nifeather.lingstweaks.misc.ParticleLimit;

import java.util.Arrays;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Shadow public abstract RegistryAccess.Frozen registryAccess();

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
        var change = packet.change().position();

        lingsTweaks$checkTeleportPacket(packet, Relative.X, change.x(), ci);
        lingsTweaks$checkTeleportPacket(packet, Relative.Y, change.y(), ci);
        lingsTweaks$checkTeleportPacket(packet, Relative.Z, change.z(), ci);

        lingsTweaks$checkTeleportPacket(packet, Relative.X_ROT, packet.change().xRot(), ci);
        lingsTweaks$checkTeleportPacket(packet, Relative.Z, packet.change().yRot(), ci);
    }

    @Unique
    private void lingsTweaks$checkTeleportPacket(ClientboundTeleportEntityPacket packet,
                                                  Relative relMove,
                                                  double value, CallbackInfo ci)
    {
        if (packet.relatives().contains(relMove)
                && lingsTweaks$isValueOutOfRange(worldMax, value))
        {
            lingsTweaks$cancelPacket(packet, ci, "handleTeleportPacket: '%s' too big".formatted(relMove));
        }
    }

    @Inject(
            method = "handleMovePlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onPosAndLookPacket(ClientboundPlayerPositionPacket packet, CallbackInfo ci)
    {
        var position = packet.change().position();

        lingsTweaks$checkPlayerPosPacket(packet, Relative.X, worldMax, position.x(), ci);
        lingsTweaks$checkPlayerPosPacket(packet, Relative.Y, worldMax, position.y(), ci);
        lingsTweaks$checkPlayerPosPacket(packet, Relative.Z, worldMax, position.z(), ci);
        lingsTweaks$checkPlayerPosPacket(packet, Relative.X_ROT, worldMax, packet.change().xRot(), ci);
        lingsTweaks$checkPlayerPosPacket(packet, Relative.Y_ROT, worldMax, packet.change().yRot(), ci);
    }

    @Unique
    private void lingsTweaks$checkPlayerPosPacket(ClientboundPlayerPositionPacket packet,
                                                  Relative relMove,
                                                  double worldMax, double value, CallbackInfo ci)
    {
        if (packet.relatives().contains(relMove)
            && lingsTweaks$isValueOutOfRange(worldMax, value))
        {
            lingsTweaks$cancelPacket(packet, ci, "handleMovePlayer: '%s' too big".formatted(relMove));
        }
    }

    @Inject(
            method = "handleExplosion",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onExplosion(ClientboundExplodePacket packet, CallbackInfo ci)
    {
        var center = packet.center();

        if (lingsTweaks$isValueOutOfRange(worldMax,
                center.x(), center.y(), center.z()))
        {
            lingsTweaks$cancelPacket(packet, ci, "Position too large");
        }

        var knockbackOptional = packet.playerKnockback();
        if (knockbackOptional.isPresent())
        {
            var knockback = knockbackOptional.get();
            if (lingsTweaks$isValueOutOfRange(worldMax,
                    knockback.x(), knockback.y(), knockback.z()))
            {
                lingsTweaks$cancelPacket(packet, ci, "Knockback velocity too large");
            }
        }
    }

    @Inject(
            method = "handleParticleEvent",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onParticle(ClientboundLevelParticlesPacket packet, CallbackInfo ci)
    {
        if (lingsTweaks$isValueOutOfRange(worldMax,
                packet.getX(), packet.getY(), packet.getZ(),
                packet.getXDist(), packet.getYDist(), packet.getZDist(),
                packet.getMaxSpeed()))
        {
            lingsTweaks$cancelPacket(packet, ci, "Invalid position, speed, or offset data");
        }

        if (packet.getCount() > ParticleLimit.INSTANCE.getMaxLimit())
            lingsTweaks$cancelPacket(packet, ci, "Particle count larger than %s, ignoring".formatted(ParticleLimit.INSTANCE.getMaxLimit()));
    }

    @Redirect(
            method = "handleSetPlayerTeamPacket",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V")
    )
    private void lingsTweaks$muteTeamWarning(Logger instance, String s, Object[] objects)
    {
    }

    @Redirect(
            method = "handleAddEntity",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V")
    )
    private void lingsTweaks$muteUnknownEntityWarning(Logger instance, String s, Object o)
    {
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
    private boolean lingsTweaks$isValueOutOfRange(double max, double... values)
    {
        return lingsTweaks$configData.blockPossibleCrashPackets
                && Arrays.stream(values).anyMatch(v ->
        {
            return Double.isNaN(v) || Math.abs(v) > max;
        });
    }
}
