package me.jjm_223.smartgiants.entities.v1_14_r1.nms;

import me.jjm_223.smartgiants.api.util.Configuration;
import net.minecraft.server.v1_14_R1.*;

public class SmartGiantHostile extends SmartGiant {
    public SmartGiantHostile(final EntityTypes<? extends EntityGiantZombie> entityTypes, final World world) {
        super(entityTypes, world);
    }

    @Override
    public void initAttributes() {
        super.initAttributes();

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(Configuration.getInstance().attackDamage());
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 0.5F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }
}
