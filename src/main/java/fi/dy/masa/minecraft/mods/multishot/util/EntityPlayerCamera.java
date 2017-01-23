package fi.dy.masa.minecraft.mods.multishot.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityPlayerCamera extends EntityOtherPlayerMP
{
    public EntityPlayerCamera(World worldIn, GameProfile gameProfileIn)
    {
        super(worldIn, gameProfileIn);
    }

    @Override
    public boolean isInvisible()
    {
        return true;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player)
    {
        return false;
    }

    @Override
    public boolean isSpectator()
    {
        return true;
    }
}
