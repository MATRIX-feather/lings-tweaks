package xyz.nifeather.lingstweaks.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import xyz.nifeather.lingstweaks.config.ConfigScreenProvider;

public class ModMenuApiImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> ConfigScreenProvider.createScreen(TweakClient.instance(), parent);
    }
}
