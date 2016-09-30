package me.jjm_223.smartgiants.entities.v1_10_R1.nms;

import net.minecraft.server.v1_10_R1.*;

public class SmartGiant extends EntityGiantZombie
{
    public SmartGiant(World world)
    {
        super(world);

        if (this instanceof SmartGiantHostile)
        {
            return;
        }

        width = 1;

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalTempt(this, .9F, Items.APPLE, false));
        this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, .9F));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 16.0F));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    public float a(BlockPosition position)
    {
        return 0.5F - world.n(position);
    }
}