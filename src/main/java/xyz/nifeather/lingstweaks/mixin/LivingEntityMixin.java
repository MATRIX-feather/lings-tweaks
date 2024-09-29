package xyz.nifeather.lingstweaks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.lingstweaks.client.TweakClient;
import xyz.nifeather.lingstweaks.config.ModConfigData;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Unique
    private ModConfigData lingsTweaks$modConfig;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void lingsTweaks$init(EntityType<?> entityType, Level level, CallbackInfo ci)
    {
        lingsTweaks$modConfig = TweakClient.instance().getConfigData();
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    public void lingsTweaks$onGlowingCall(CallbackInfoReturnable<Boolean> cir)
    {
        if (lingsTweaks$modConfig.playerGlow && (Object)this == Minecraft.getInstance().player)
            cir.setReturnValue(true);
    }
}
