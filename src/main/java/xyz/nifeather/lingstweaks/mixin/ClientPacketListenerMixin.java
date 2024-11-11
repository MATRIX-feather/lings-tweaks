package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.Relative;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.config.ModConfigData;
import xyz.nifeather.lingstweaks.misc.ParticleLimit;

import java.util.Arrays;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Shadow public abstract Connection getConnection();

    @Shadow public abstract void sendChat(String string);

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
            lingsTweaks$cancelPacket(packet, ci, "handleTeleportPacket: Relative '%s' too big".formatted(relMove));
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

        if (!packet.relatives().contains(relMove)
                && !Double.isNaN(value)
                && lingsTweaks$isValueOutOfRange(worldMax, value))
        {
            lingsTweaks$cancelPacket(packet, ci, "handleMovePlayer: Not declared Relative '%s' but has a value that is out of range: '%s'".formatted(relMove, value));
            return;
        }

        if (lingsTweaks$isValueOutOfRange(worldMax, value))
        {
            lingsTweaks$cancelPacket(packet, ci, "handleMovePlayer: Relative '%s' too big".formatted(relMove));
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
            lingsTweaks$cancelPacket(packet, ci, "handleExplosion: Position too large");
        }

        var knockbackOptional = packet.playerKnockback();
        if (knockbackOptional.isPresent())
        {
            var knockback = knockbackOptional.get();
            if (lingsTweaks$isValueOutOfRange(lingsTweaks$worldMax,
                    knockback.x(), knockback.y(), knockback.z()))
            {
                lingsTweaks$cancelPacket(packet, ci, "handleExplosion: Knockback velocity too large");
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
            lingsTweaks$cancelPacket(packet, ci, "handleParticleEvent: Invalid position, speed, or offset data");
        }

        if (packet.getCount() > ParticleLimit.INSTANCE.getMaxLimit())
            lingsTweaks$cancelPacket(packet, ci, "handleParticleEvent: Particle count larger than %s, ignoring".formatted(ParticleLimit.INSTANCE.getMaxLimit()));
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

        lingsTweaks$chatMessage = reason;
    }

    @Nullable
    @Unique
    private String lingsTweaks$chatMessage = null;

    @Unique
    private int lingsTweaks$sendCooldown;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci)
    {
        lingsTweaks$sendCooldown--;

        if (lingsTweaks$sendCooldown < 0)
        {
            if (TweakClient.instance().getConfigData().blockSendChat
                    && lingsTweaks$chatMessage != null)
            {
                var msg = lingsTweaks$configData.blockChatMessage;
                lingsTweaks$sendCooldown = 30;

                try
                {
                    this.sendChat(msg.formatted(lingsTweaks$chatMessage));
                }
                catch (Throwable t)
                {
                    this.sendChat(msg.formatted("Failed sending raw output"));

                    TweakClient.LOGGER.error("Failed sending raw output: " + t.getMessage());
                    t.printStackTrace();
                }
            }

            lingsTweaks$chatMessage = null;
        }
    }

    @Unique
    private boolean lingsTweaks$isValueOutOfRange(double max, double... values)
    {
        return lingsTweaks$configData.blockPossibleCrashPackets
                && Arrays.stream(values).anyMatch(v ->
        {
            return Double.isInfinite(v) || Double.isNaN(v) || Math.abs(v) > max;
        });
    }
}
