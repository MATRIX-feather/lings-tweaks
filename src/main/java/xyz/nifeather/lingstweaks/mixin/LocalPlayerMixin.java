package xyz.nifeather.lingstweaks.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.config.ModConfigData;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer
{
    @Unique
    private ModConfigData lingsTweaks$modConfig;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void lingsTweaks$init(Minecraft minecraft, ClientLevel clientLevel, ClientPacketListener clientPacketListener, StatsCounter statsCounter, ClientRecipeBook clientRecipeBook, boolean bl, boolean bl2, CallbackInfo ci)
    {
        lingsTweaks$modConfig = TweakClient.instance().getConfigData();
    }

    @Override
    public boolean isCurrentlyGlowing()
    {
        if (lingsTweaks$modConfig.playerGlow && (Object)this == Minecraft.getInstance().player)
            return true;

        return super.isCurrentlyGlowing();
    }
}
