package xyz.nifeather.lingstweaks.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer
{
    @Shadow @Final public ClientPacketListener connection;

    @Shadow public ClientInput input;

    @Shadow private Input lastSentInput;

    public MixinLocalPlayer(ClientLevel clientLevel, GameProfile gameProfile)
    {
        super(clientLevel, gameProfile);
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendShiftKeyState()V", shift = At.Shift.AFTER)
    )
    private void lingsTweaks$onTick(CallbackInfo ci)
    {
        if (this.getVehicle() != null
                && TweakClient.instance().getConfigData().pre1_21_3_steer_vehicle_packets
                && this.lastSentInput.equals(this.input.keyPresses))
        {
            this.connection.send(new ServerboundPlayerInputPacket(this.input.keyPresses));
        }
    }
}
