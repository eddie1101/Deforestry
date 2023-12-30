package org.erg.deforestry.common.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;
import org.erg.deforestry.Config;
import org.erg.deforestry.common.registries.DeforestryItems;
import org.erg.deforestry.common.registries.DeforestrySounds;


public class BoomerangEntity extends Projectile {

    public static final int MAX_DAMAGE_DIFFERENTIAL = 5;
    private static final double P = 0.0072d, I = 0.0012d, D = 0.0650d;

    private Vec3 positionErrorIntegral = new Vec3(0.0d, 0.0d, 0.0d);

    private boolean moving = true;
    private int flyingSoundPlayed = 0;
    private int tickStamp = 0;
    private float launchRadius = 0;
    private int entitiesPierced = 0;

    private final int minDamage;
    private final ItemStack boomerangItemStack;

    BoomerangState currentState;
    BoomerangState nextState;

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang, double x, double y, double z, float power) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(x, y, z);

        if(boomerang == null) {
            boomerangItemStack = new ItemStack(DeforestryItems.BOOMERANG.get());
        } else {
            boomerangItemStack = boomerang;
        }

        this.launchRadius = Math.max(Config.boomerangDefaultRange / 2f, Config.boomerangDefaultRange * power);

        int sharpnessLevels = boomerangItemStack.getEnchantmentLevel(Enchantments.SHARPNESS);
        this.minDamage = 1 + sharpnessLevels;

        currentState = BoomerangState.ATTACKING;
        nextState = BoomerangState.ATTACKING;

    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang, float power) {
        this(type, level, owner, boomerang, owner.getX(), owner.getEyeY() - 0.5f, owner.getZ(), power);
    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, double x, double y, double z) {
        this(type, level, null, null, x, y, z, 1.0f);
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

        if(this.isInFluidType(Fluids.LAVA.defaultFluidState())) {
            level().playSound(null, this.position().x, this.position().y, this.position().z, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0f, 1.0f);
            this.discard();
        }

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
            this.currentState = this.nextState;
        }
    }

    protected void handleAttackState() {

        boolean shouldReturn = false;

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
                this.onHit(hitResult);
                this.hasImpulse = true;
                shouldReturn = true;
            }
        }

        if(shouldReturn || pos.distanceTo(getOwner().position()) > this.launchRadius) {
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

            int timeAlive = tickCount - tickStamp;

            HitResult hitResult = this.level().clip(new ClipContext(pos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if(hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                this.onHit(hitResult);
            }


            Vec3 targetPos = ownerEntity.getPosition(1.0f);
            if(ownerEntity instanceof Player) {
                targetPos = targetPos.add(0.0f, 1.0f, 0.0f);
            }

            Vec3 positionDelta = pos.subtract(targetPos);

            double timeDelta = 0.05; //20 ticks per second
            positionErrorIntegral = positionErrorIntegral.add(positionDelta.scale(timeDelta));

            Vec3 acceleration = positionDelta.scale(-P).add(positionErrorIntegral.scale(-I)).add(velocity.scale(-D));

            //Prevents "whiplash" when the PID controller takes over, which can look very glitchy
            double easing;
            if(timeAlive < 5) {
                easing = timeAlive / 5d;
            } else {
                easing = 1d;
            }

            this.setDeltaMovement(this.getDeltaMovement().add(acceleration.scale(easing)));

            if(timeAlive > Config.boomerangLifespan || this.getBoundingBox().intersects(ownerEntity.getBoundingBox().inflate(0.2d))) {
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

        this.entitiesPierced = 0;
        this.moving = false;

        this.discard();
    }

    public void prepareToHome() {
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
        if(!level().isClientSide())
            this.bounce(hitResult.getDirection().getAxis());
    }

    public void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        int unbreaking = boomerangItemStack.getEnchantmentLevel(Enchantments.UNBREAKING);
        double chance = 1d / (unbreaking + 1);
        boomerangItemStack.hurtAndBreak(level().getRandom().nextDouble() <= chance ? 1 : 0, (LivingEntity) getOwner(), (e) -> {
            if (e!= null) {
                e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            }
        });


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

        double velocity = Math.min(this.getDeltaMovement().length(), 1.0d);
        int damage = minDamage + (int) Math.round(velocity * MAX_DAMAGE_DIFFERENTIAL);

        hitEntity.hurt(damageSource, (float) damage);

        int knockback = boomerangItemStack.getEnchantmentLevel(Enchantments.KNOCKBACK);
        if(knockback > 0) {
            Vec3 knockbackVector = this.getDeltaMovement().scale(knockback);
            hitEntity.push(knockbackVector.x, knockbackVector.y, knockbackVector.z);
        }

        int piercing = boomerangItemStack.getEnchantmentLevel(Enchantments.PIERCING);
        if(++entitiesPierced >= piercing) {
//            this.bounce(); entity bouncing is not supported yet
        }

        int fire = boomerangItemStack.getEnchantmentLevel(Enchantments.FIRE_ASPECT);
        if(fire > 0) {
            hitEntity.setSecondsOnFire(5 * fire);
        }

    }

    public void bounce(Direction.Axis surfaceHitAxis) {
        if(surfaceHitAxis.getName().equals("y")) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0d, -1.0d, 1.0d).scale(0.8d));
        } else if(surfaceHitAxis.getName().equals("x")) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-1.0d, 1.0d, 1.0d).scale(0.8d));
        } else if(surfaceHitAxis.getName().equals("z")) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0d, 1.0d, -1.0d).scale(0.8d));
        }
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
