package org.erg.deforestry.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.erg.deforestry.Config;
import org.erg.deforestry.Deforestry;
import org.erg.deforestry.common.registries.DeforestryItems;
import org.erg.deforestry.common.registries.DeforestrySounds;


public class BoomerangEntity extends Projectile {

    private static final int BASE_DAMAGE = 6;
    private static final double P = 0.0072d, I = 0.0012d, D = 0.0650d;
    private Vec3 positionErrorIntegral = new Vec3(0.0d, 0.0d, 0.0d);

    private boolean moving = true;
    private int flyingSoundPlayed = 0;
    private int tickStamp = 0;

    private final ItemStack boomerangItemStack;

    BoomerangState currentState;
    BoomerangState nextState;

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang, double x, double y, double z) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(x, y, z);

        if(boomerang == null) {
            boomerangItemStack = new ItemStack(DeforestryItems.BOOMERANG.get());
        } else {
            boomerangItemStack = boomerang;
        }

        currentState = BoomerangState.ATTACKING;
        nextState = BoomerangState.ATTACKING;

    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang) {
        this(type, level, owner, boomerang, owner.getX(), owner.getEyeY() - 0.5f, owner.getZ());
    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, double x, double y, double z) {
        this(type, level, null, null, x, y, z);
    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> entityType, Level level) {
        this(entityType, level, 0.0d, 0.0d, 0.0d);
    }

    @Override
    public void shoot(double xAngle, double yAngle, double zAngle, float power, float scale) {
        super.shoot(xAngle, yAngle, zAngle, power, scale);
        this.moving = true;
    }

    @Override
    public void tick() {
        super.tick();

        //This makes me hate myself even more
        if(tickCount - flyingSoundPlayed > 24) {
            level().playSound(null, this.position().x, this.position().y, this.position().z, DeforestrySounds.BOOMERANG_FLYING.get(), SoundSource.NEUTRAL);
            flyingSoundPlayed = tickCount;
        }

        if (this.moving)
            this.setPos(this.position().add(this.getDeltaMovement()));

        if (this.currentState == BoomerangState.ATTACKING) {
            handleAttackState();
        } else if (this.currentState == BoomerangState.RETURNING) {
            handleReturningState();
        } else if (this.currentState == BoomerangState.RETURNED) {
            handleReturnedState();
        }

        if (this.currentState != this.nextState) {
            Deforestry.LOGGER.debug("current state: " + currentState + ", next state: " + nextState + ", Owner:\n" + getOwner());
            this.currentState = this.nextState;
        }

    }

    protected void handleAttackState() {

        Vec3 velocity = this.getDeltaMovement();
        Vec3 pos = this.position();
        Vec3 nextPos = pos.add(velocity);

        HitResult hitResult = this.level().clip(new ClipContext(pos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            nextPos = hitResult.getLocation();
        }

        EntityHitResult entityHitResult = findHitEntity(this.position(), nextPos);
        if (entityHitResult != null) {
            hitResult = entityHitResult;
        }

        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            Entity hitEntity = ((EntityHitResult) hitResult).getEntity();
            Entity ownerEntity = this.getOwner();
            if (hitEntity instanceof Player && ownerEntity instanceof Player && !((Player) ownerEntity).canHarmPlayer((Player) hitEntity) || this.ownedBy(hitEntity)) {
                hitResult = null;
            }
        }

        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            if (!EventHooks.onProjectileImpact(this, hitResult)) {
                boomerangItemStack.hurtAndBreak(1, (LivingEntity) getOwner(), (e) -> {
                    if (e!= null) {
                        e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                    }
                });
                this.onHit(hitResult);
                this.hasImpulse = true;
                this.prepareToHome();
                this.nextState = BoomerangState.RETURNING;
            }
        }

        if(pos.distanceTo(getOwner().position()) > Config.boomerangRange) {
            this.prepareToHome();
            this.nextState = BoomerangState.RETURNING;
        }
    }

    protected void handleReturningState() {

        Vec3 velocity = this.getDeltaMovement();
        Vec3 pos = this.position();
        Vec3 nextPos = pos.add(velocity);

        Entity ownerEntity = getOwner();

        if (ownerEntity == null) {
            this.nextState = BoomerangState.RETURNED;
        } else {

            Vec3 targetPos = ownerEntity.getPosition(1.0f);
            if(ownerEntity instanceof Player) {
                targetPos = targetPos.add(0.0f, 1.2f, 0.0f);
            }

            Vec3 positionDelta = pos.subtract(targetPos);

            double timeDelta = 0.05; //20 ticks per second
            positionErrorIntegral = positionErrorIntegral.add(positionDelta.scale(timeDelta));

            Vec3 acceleration = positionDelta.scale(-P).add(positionErrorIntegral.scale(-I)).add(velocity.scale(-D));
            this.setDeltaMovement(this.getDeltaMovement().add(acceleration));

            EntityHitResult entityHitResult = findHitEntity(this.position(), nextPos);
            if (entityHitResult != null) {
                Entity hitEntity = entityHitResult.getEntity();
                if(this.ownedBy(hitEntity)) {
                    this.moving = false;
                    this.nextState = BoomerangState.RETURNED;
                }
            }

            if(this.getBoundingBox().intersects(ownerEntity.getBoundingBox().inflate(0.25d))) {
                this.moving = false;
                this.nextState = BoomerangState.RETURNED;
            }

            int timeAlive = tickCount - tickStamp;

            if(timeAlive > Config.boomerangLifespan) {
                this.moving = false;
                this.nextState = BoomerangState.RETURNED;
            }
        }
    }

    protected void handleReturnedState() {
        if (getOwner() instanceof Player player) {
            if (!player.addItem(boomerangItemStack)) {
                level().addFreshEntity(new ItemEntity(this.level(), player.getX(), player.getY(), player.getZ(), boomerangItemStack));
            }
        }

        this.discard();
    }

    public void prepareToHome() {
        this.setDeltaMovement(this.getDeltaMovement().scale(-1.0f));
        tickStamp = tickCount;
        this.positionErrorIntegral = new Vec3(0.0d, 0.0d, 0.0d);
    }

    public EntityHitResult findHitEntity(Vec3 pos, Vec3 vel) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pos, vel, this.getBoundingBox().expandTowards(vel).inflate(1.0), this::canHitEntity);
    }

    public int getTicksForRotation() {
        return tickCount;
    }
    protected boolean canHitEntity(Entity entity) {
        return entity.canBeHitByProjectile();
    }

    @Override
    public void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        Vec3 soundLocation = hitResult.getLocation();
        level().playSound(null, soundLocation.x, soundLocation.y, soundLocation.z, DeforestrySounds.BOOMERANG_CLANG.get(), SoundSource.NEUTRAL);
    }

    public void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        Entity hitEntity = hitResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource;
        if (owner == null) {
            damageSource = this.damageSources().thrown(this, this);
        } else {
            damageSource = this.damageSources().thrown(this, owner);
            if (owner instanceof LivingEntity) {
                ((LivingEntity)owner).setLastHurtMob(hitEntity);
            }
        }
        hitEntity.hurt(damageSource, (float) BASE_DAMAGE);
    }


    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }

    protected enum BoomerangState {
        ATTACKING,
        RETURNING,
        RETURNED
    }

}
