package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.crafting.RecipeAccess;
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

import java.text.DecimalFormat;
import java.util.Arrays;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Shadow public abstract Connection getConnection();

    @Unique
    private final double lingsTweaks$worldMax = 29999984 * 2d;

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
                && lingsTweaks$isValueOutOfRange(lingsTweaks$worldMax, value))
        {
            lingsTweaks$cancelPacket(packet, ci, "handleTeleportPacket: '%s' too big".formatted(relMove));
        }
    }

/*
    @Inject(
            method = "handleMoveEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onMoveEntity(ClientboundMoveEntityPacket clientboundMoveEntityPacket, CallbackInfo ci)
    {
        lingsTweaks$checkEntityPositionPacket(clientboundMoveEntityPacket, this.lingsTweaks$worldMax, ci);
    }

    @Unique
    private void lingsTweaks$checkEntityPositionPacket(ClientboundMoveEntityPacket packet,
                                                       double worldMax, CallbackInfo ci)
    {
        if (packet.hasPosition())
        {
            if (lingsTweaks$isValueOutOfRange(worldMax, packet.getXa(), packet.getYa(), packet.getZa()))
            {
                lingsTweaks$cancelPacket(packet, ci, "handleMoveEntity: Position too big");
                return;
            }
        }

        if (packet.hasRotation())
        {
            if (lingsTweaks$isValueOutOfRange(worldMax, packet.getxRot(), packet.getyRot()))
            {
                lingsTweaks$cancelPacket(packet, ci, "handleMoveEntity: Invalid rotation.");
                return;
            }
        }
    }
*/

    @Inject(
            method = "handleMovePlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onPosAndLookPacket(ClientboundPlayerPositionPacket packet, CallbackInfo ci)
    {
        var position = packet.change().position();

        lingsTweaks$checkPlayerPosPacket(packet, Relative.X, lingsTweaks$worldMax, position.x(), ci);
        lingsTweaks$checkPlayerPosPacket(packet, Relative.Y, lingsTweaks$worldMax, position.y(), ci);
        lingsTweaks$checkPlayerPosPacket(packet, Relative.Z, lingsTweaks$worldMax, position.z(), ci);
        lingsTweaks$checkPlayerPosPacket(packet, Relative.X_ROT, lingsTweaks$worldMax, packet.change().xRot(), ci);
        lingsTweaks$checkPlayerPosPacket(packet, Relative.Y_ROT, lingsTweaks$worldMax, packet.change().yRot(), ci);

        if (ci.isCancelled())
        {
            var player = Minecraft.getInstance().player;

            if (player != null) // Make IDEA happy, and make crash service sad.
            {
                this.getConnection().send(new ServerboundMovePlayerPacket.PosRot(
                        player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround(), player.horizontalCollision));
            }
        }

        //TweakClient.LOGGER.info("On POSLOOKPACKET! " + " :: canceled? " + ci.isCancelled() + " :: " + packet.change());
    }

    @Unique
    private void lingsTweaks$checkPlayerPosPacket(ClientboundPlayerPositionPacket packet,
                                                  Relative relMove,
                                                  double worldMax, double value, CallbackInfo ci)
    {
        //TweakClient.LOGGER.info("Packet contains " + relMove + " ? " + packet.relatives().contains(relMove) + " :: and value is " + value);

        if (!packet.relatives().contains(relMove) && lingsTweaks$isValueOutOfRange(worldMax, value))
        {
            lingsTweaks$cancelPacket(packet, ci, "handleMovePlayer: not declared '%s' but value is out of range '%s'".formatted(relMove, value));
            return;
        }

        if (lingsTweaks$isValueOutOfRange(worldMax, value))
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

        if (lingsTweaks$isValueOutOfRange(lingsTweaks$worldMax,
                center.x(), center.y(), center.z()))
        {
            lingsTweaks$cancelPacket(packet, ci, "Position too large");
        }

        var knockbackOptional = packet.playerKnockback();
        if (knockbackOptional.isPresent())
        {
            var knockback = knockbackOptional.get();
            if (lingsTweaks$isValueOutOfRange(lingsTweaks$worldMax,
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
        if (lingsTweaks$isValueOutOfRange(lingsTweaks$worldMax,
                packet.getX(), packet.getY(), packet.getZ(),
                packet.getXDist(), packet.getYDist(), packet.getZDist(),
                packet.getMaxSpeed()))
        {
            lingsTweaks$cancelPacket(packet, ci, "Invalid position, speed, or offset data");
        }

        if (packet.getCount() > ParticleLimit.INSTANCE.getMaxLimit())
            lingsTweaks$cancelPacket(packet, ci, "Particle count larger than %s, ignoring".formatted(ParticleLimit.INSTANCE.getMaxLimit()));
    }

    @Unique
    private void lingsTweaks$cancelPacket(Packet<?> packet, CallbackInfo ci, @Nullable String reason)
    {
        ci.cancel();

        String logString = null;

        if (reason == null)
            logString = "Cancelling invalid %s packet from server!".formatted(packet.getClass().getSimpleName());
        else
            logString = "Cancelling invalid %s packet from server: %s".formatted(packet.getClass().getSimpleName(), reason);

        TweakClient.LOGGER.info(logString);

        if (TweakClient.instance().getConfigData().blockPacketShowOnGui)
        {
            String messageString = "Bad packet: %s".formatted(reason);

            var player = Minecraft.getInstance().player;
            if (player != null)
                player.displayClientMessage(Component.literal(messageString), true);

        }
    }

    @Unique
    private boolean lingsTweaks$isValueOutOfRange(double max, double... values)
    {
        return lingsTweaks$configData.blockPossibleCrashPackets
                && Arrays.stream(values).anyMatch(v ->
        {
            return Double.isInfinite(max) || Double.isNaN(v) || Math.abs(v) > max;
        });
    }
}
