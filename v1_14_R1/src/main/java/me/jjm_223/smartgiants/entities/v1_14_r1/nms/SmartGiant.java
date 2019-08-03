package me.jjm_223.smartgiants.entities.v1_14_r1.nms;

import me.jjm_223.smartgiants.api.entity.ISmartGiant;
import me.jjm_223.smartgiants.api.util.Configuration;
import net.minecraft.server.v1_14_R1.*;

public class SmartGiant extends EntityGiantZombie implements ISmartGiant {
    @Override
    protected void initAttributes() {
        super.initAttributes();

        double health = Configuration.getInstance().maxHealth();

        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(health);
        setHealth((float) health);

        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(Configuration.getInstance().movementSpeed());
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(Configuration.getInstance().followRange());
    }

    public SmartGiant(final EntityTypes<? extends EntityGiantZombie> entityTypes, final World world) {
        super(entityTypes, world);

        if (this instanceof SmartGiantHostile) {
            return;
        }

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalTempt(this, 1.0D, RecipeItemStack.a(Items.APPLE), false));
        this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 16.0F));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    public boolean isHostile() {
        return this instanceof SmartGiantHostile;
    }

    @Override
    public boolean c(NBTTagCompound nbtTagCompound) {
        boolean success = super.c(nbtTagCompound);
        if (!success && !this.dead) {
            nbtTagCompound.setString("id", "minecraft:giant");
            this.save(nbtTagCompound);
            return true;
        } else {
            return success;
        }
    }

    @Override
    public boolean d(NBTTagCompound nbtTagCompound) {
        boolean success = super.d(nbtTagCompound);
        if (!success && !this.dead && !this.isPassenger()) {
            nbtTagCompound.setString("id", "minecraft:giant");
            this.save(nbtTagCompound);
            return true;
        } else {
            return success;
        }
    }
}
