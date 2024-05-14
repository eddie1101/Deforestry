package org.erg.deforestry.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;
import org.erg.deforestry.Config;
import org.erg.deforestry.Deforestry;
import org.erg.deforestry.common.registries.DeforestryItems;
import org.erg.deforestry.common.registries.DeforestrySounds;


public class BoomerangEntity extends Projectile {

    public static final int MAX_DAMAGE_DIFFERENTIAL = 5;
    protected static final double P = 0.0072d, I = 0.0012d, D = 0.0650d;

    protected Vec3 positionErrorIntegral = new Vec3(0.0d, 0.0d, 0.0d);

    protected boolean moving = true;
    protected int flyingSoundPlayed = 0;
    protected int tickStamp = 0;
    protected int tickTimeout = 0;
    protected float launchRadius = 0;
    protected int entitiesPierced = 0;

    protected int minDamage;
    protected int itemSlot;
    protected final ItemStack boomerangItemStack;

    BoomerangState lastState;
    BoomerangState currentState;
    BoomerangState nextState;

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang, int itemSlot, double x, double y, double z, float power) {
        super(type, level);
        this.setOwner(owner);
        this.setPos(x, y, z);

        this.itemSlot = itemSlot;
        if(boomerang == null) {
            boomerangItemStack = new ItemStack(DeforestryItems.BOOMERANG.get());
        } else {
            boomerangItemStack = boomerang;
        }

        this.launchRadius = Math.max(Config.boomerangDefaultRange / 2f, Config.boomerangDefaultRange * power);

        int sharpnessLevels = boomerangItemStack.getEnchantmentLevel(Enchantments.SHARPNESS);
        this.minDamage = 1 + sharpnessLevels;

        lastState = BoomerangState.TIMEOUT;
        currentState = BoomerangState.ATTACKING;
        nextState = BoomerangState.ATTACKING;

    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, LivingEntity owner, ItemStack boomerang, int itemSlot, float power) {
        this(type, level, owner, boomerang, itemSlot, owner.getX(), owner.getEyeY() - 0.5f, owner.getZ(), power);
    }

    public BoomerangEntity(EntityType<? extends BoomerangEntity> type, Level level, double x, double y, double z) {
        this(type, level, null, null, -1, x, y, z, 1.0f);
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

        Vec3 nextPos = position().add(getDeltaMovement());
        HitResult hitResult = this.level().clip(new ClipContext(position(), position().add(getDeltaMovement()), ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER, this));
        if(hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
            Level level = level();
            if(level.getBlockState(pos).is(Blocks.WATER) && !this.isInWater()) {
                this.doWaterSplashEffect();
                this.wasTouchingWater = true;
            }
        } else if(level().getBlockState(new BlockPos((int) nextPos.x, (int) nextPos.y, (int) nextPos.z)).is(Blocks.AIR) && this.isInWater()) {
            this.doWaterSplashEffect();
            this.wasTouchingWater = false;
        }

        if(this.isInFluidType(Fluids.LAVA.defaultFluidState())) {
            level().playSound(null, this.position().x, this.position().y, this.position().z, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0f, 1.0f);
            this.discard();
        }

        //This makes me hate myself even more
        if(tickCount - flyingSoundPlayed > 40) {
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
        } else if (this.currentState == BoomerangState.TIMEOUT) {
            handleTimeoutState();
        }

        if (this.currentState != this.nextState) {
            this.lastState = this.currentState;
            this.currentState = this.nextState;
        }
    }

    protected void handleAttackState() {

        Entity ownerEntity = getOwner();

        if (ownerEntity == null) {
            nextState = BoomerangState.TIMEOUT;
        } else {
            boolean shouldReturn = false;

            Vec3 velocity = this.getDeltaMovement();
            Vec3 pos = this.position();
            Vec3 nextPos = pos.add(velocity);

            HitResult hitResult = this.getImminentCollision(pos, nextPos);

            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                if (!EventHooks.onProjectileImpact(this, hitResult)) {
                    this.onHit(hitResult);
                    this.hasImpulse = true;
                    shouldReturn = true;
                }
            }

            if (shouldReturn || pos.distanceTo(getOwner().position()) > this.launchRadius) {
                this.prepareToHome();
                this.nextState = BoomerangState.RETURNING;
            }
        }
    }

    protected void handleReturningState() {

        Entity ownerEntity = getOwner();

        if (ownerEntity == null) {
            this.nextState = BoomerangState.TIMEOUT;
        } else {

            Vec3 velocity = this.getDeltaMovement();
            Vec3 pos = this.position();
            Vec3 nextPos = pos.add(velocity);

            int timeAlive = tickCount - tickStamp;

            HitResult hitResult = getImminentCollision(pos, nextPos);
            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                if (!EventHooks.onProjectileImpact(this, hitResult)) {
                    this.onHit(hitResult);
                }
            }

            Vec3 targetPos = ownerEntity.position();
            if(ownerEntity instanceof Player) {
                targetPos = targetPos.add(0.0f, 1.0f, 0.0f);
            }

            Vec3 positionDelta = pos.subtract(targetPos);

            double timeDelta = 0.05; //20 ticks per second
            positionErrorIntegral = positionErrorIntegral.add(positionDelta.scale(timeDelta));

            Vec3 acceleration = positionDelta.scale(-P).add(positionErrorIntegral.scale(-I)).add(velocity.scale(-D));

            //Prevents "whiplash" when the PID controller takes over, which can look very glitchy
            double easing = 1d;
            if(timeAlive < 5) {
                easing = timeAlive / 5d;
            }

            Vec3 newVelocity = this.getDeltaMovement().add(acceleration.scale(easing));
            double waterSpeedFactor = this.wasTouchingWater ? 0.75d : 1d;
            this.setDeltaMovement(newVelocity.scale(waterSpeedFactor));

            if(timeAlive > Config.boomerangLifespan || this.getBoundingBox().intersects(ownerEntity.getBoundingBox().inflate(0.2d))) {
                this.moving = false;
                this.nextState = BoomerangState.RETURNED;
            }
        }
    }

    protected void handleReturnedState() {
        if(getOwner() instanceof Player player && !level().isClientSide()) {
            int slot = player.getInventory().getItem(itemSlot).isEmpty() ? itemSlot : -1;
            if (!player.getInventory().add(slot, this.boomerangItemStack)) {
                level().addFreshEntity(new ItemEntity(this.level(), player.getX(), player.getY(), player.getZ(), boomerangItemStack));
            }
        }

        this.entitiesPierced = 0;
        this.moving = false;

        this.discard();
    }

    protected void handleTimeoutState() {
        if(getOwner() == null) {
            if(tickTimeout++ > Config.boomerangOwnerTimeout) {
                this.nextState = BoomerangState.RETURNED;
            }
        } else {
            tickTimeout = 0;
            this.nextState = this.lastState;
        }
    }

    public void prepareToHome() {
        tickStamp = tickCount;
        this.positionErrorIntegral = new Vec3(0.0d, 0.0d, 0.0d);
    }

    public EntityHitResult findHitEntity(Vec3 pos, Vec3 vel) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pos, vel, this.getBoundingBox().expandTowards(vel).inflate(1.0), this::canHitEntity);
    }

    public HitResult getImminentCollision(Vec3 pos, Vec3 nextPos) {
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
        return hitResult;
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
            this.bounceOffBlock(hitResult.getDirection().getAxis());
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
        if(++entitiesPierced > piercing && !level().isClientSide()) {
            this.bounceOffEntity(hitEntity);
        }

        int fire = boomerangItemStack.getEnchantmentLevel(Enchantments.FIRE_ASPECT);
        if(fire > 0) {
            hitEntity.setSecondsOnFire(4 * fire);
        }

    }

    public void bounceOffBlock(Direction.Axis surfaceHitAxis) {
        if(surfaceHitAxis.getName().equals("y")) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0d, -1.0d, 1.0d).scale(0.8d));
        } else if(surfaceHitAxis.getName().equals("x")) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-1.0d, 1.0d, 1.0d).scale(0.8d));
        } else if(surfaceHitAxis.getName().equals("z")) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0d, 1.0d, -1.0d).scale(0.8d));
        }
    }

    //Placeholder, works better than the old placeholder
    public void bounceOffEntity(Entity entity) {

        // OPTIMAL SOLUTION:
        // "elliptical cow"
        // construct equation for ellipsoid from bounding box dimensions
        // - each side length becomes a semi-axis (Might be too small?)
        // - or find ellipsoid that circumscribes bounding box (might be too big?)
        // - or do the former, and calculate a scaling factor based on the volume of the bounding box for a good in-between
        // project boomerang position onto it's nearest position on ellipsoid surface
        // - create line from boomerang motion vector
        // - solve for intersection of line with ellipsoid
        // - take the closest result
        // find surface normal at projected position, use that as bounce vector

        // WHAT I WILL DO INSTEAD (BECAUSE IT IS FAR EASIER):
        // subtract positions

        Vec3 boomerangPos = this.getBoundingBox().getCenter();
        AABB entityBB = entity.getBoundingBox();
        Vec3 entityPos = new Vec3(entityBB.getCenter().x, entity.getY() + entityBB.getYsize() / 2, entityBB.getCenter().z);
        Vec3 bounceDirection = new Vec3(boomerangPos.x - entityPos.x, boomerangPos.y - entityPos.y, boomerangPos.z - entityPos.z);
        this.setDeltaMovement(this.getDeltaMovement().add(bounceDirection.normalize().scale(this.getDeltaMovement().length())).scale(0.8d));
    }


    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        Deforestry.LOGGER.debug("Saving Boomerang (owner): " + this.ownerUUID);
        super.addAdditionalSaveData(tag);
        tag.putInt("slot", this.itemSlot);
        tag.putInt("stamp", tickStamp);
        tag.putInt("state", this.currentState.ordinal());
        tag.putInt("nextState", this.nextState.ordinal());
        double x = positionErrorIntegral.x, y = positionErrorIntegral.y, z = positionErrorIntegral.z;
        double dx = getDeltaMovement().x, dy = getDeltaMovement().y, dz = getDeltaMovement().z;
        tag.putDouble("dxi", x);
        tag.putDouble("dyi", y);
        tag.putDouble("dzi", z);
        tag.putDouble("dx", dx);
        tag.putDouble("dy", dy);
        tag.putDouble("dz", dz);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        Deforestry.LOGGER.debug("Reading Boomerang (owner): " + this.ownerUUID + "\n" + "Owner entity: " + getOwner());
        this.itemSlot = tag.getInt("slot");
        this.tickStamp = tag.getInt("stamp");
        this.currentState = BoomerangState.values()[tag.getInt("state")];
        this.nextState = BoomerangState.values()[tag.getInt("nextState")];
        double dxi = tag.getDouble("dxi");
        double dyi = tag.getDouble("dyi");
        double dzi = tag.getDouble("dzi");
        double dx = tag.getDouble("dx");
        double dy = tag.getDouble("dy");
        double dz = tag.getDouble("dz");
        this.positionErrorIntegral = new Vec3(dxi, dyi, dzi);
        this.setDeltaMovement(dx, dy, dz);
    }

    protected enum BoomerangState {
        ATTACKING,
        RETURNING,
        RETURNED,
        TIMEOUT
    }

}
