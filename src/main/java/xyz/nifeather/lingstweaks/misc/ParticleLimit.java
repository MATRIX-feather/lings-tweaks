package xyz.nifeather.lingstweaks.misc;

public class ParticleLimit
{
    public static final ParticleLimit INSTANCE = new ParticleLimit();

    public int getMaxLimit()
    {
        return 16384;
    }
}
