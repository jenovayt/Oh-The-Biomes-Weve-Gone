package net.potionstudios.biomeswevegone.world.entity.pumpkinwarden;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * The Pumpkin Warden Entity
 * @see PathfinderMob
 * @see GeoEntity
 * @author YaBoiChips
 */
public class PumpkinWarden extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private BlockPos jukebox;
    private boolean party;
    private static final EntityDataAccessor<Boolean> HIDING = SynchedEntityData.defineId(PumpkinWarden.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TIMER = SynchedEntityData.defineId(PumpkinWarden.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> DATA_CARRY_STATE = SynchedEntityData.defineId(PumpkinWarden.class, EntityDataSerializers.BLOCK_STATE);

    public Goal moveGoal = new WaterAvoidingRandomStrollGoal(this, 1.0D,0.7F);
    public Goal runGoal = new AvoidEntityGoal<>(this, Zombie.class, 8.0F, 1.0D, 1.0D);
    public Goal lookGoal = new LookAtPlayerGoal(this, Player.class, 2.0F);
    public Goal randLookGoal = new RandomLookAroundGoal(this);

    public PumpkinWarden(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_CARRY_STATE, Blocks.AIR.defaultBlockState());
        builder.define(HIDING, false);
        builder.define(TIMER, 0);
        super.defineSynchedData(builder);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.4D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(9, new FloatGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(Items.PUMPKIN_PIE), false));
        this.goalSelector.addGoal(3, new PumpkinWardenLeaveBlockGoal(this, 1, 32, 5));
        this.goalSelector.addGoal(2, new PumpkinWardenTakeBlockGoal(this, 1, 32, 5));
        this.goalSelector.addGoal(5, new StayByBellGoal(this, 1, 5000));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        super.registerGoals();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void checkDespawn() {
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    private static final RawAnimation HIDE_START = RawAnimation.begin().thenPlay("animation.pumpkinwarden.hidestart");
    private static final RawAnimation HIDE = RawAnimation.begin().thenPlay("animation.pumpkinwarden.hide");
    private static final RawAnimation HIDE_END = RawAnimation.begin().thenPlay("animation.pumpkinwarden.hideend");
    private static final RawAnimation HOLDING_WALKING = RawAnimation.begin().thenPlay("animation.pumpkinwarden.holding_walking");
    private static final RawAnimation HOLDING_IDLE = RawAnimation.begin().thenPlay("animation.pumpkinwarden.holding_idle");
    private static final RawAnimation WALKING = RawAnimation.begin().thenPlay("animation.pumpkinwarden.walking");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("animation.pumpkinwarden.idle");

    private static final RawAnimation WAVE = RawAnimation.begin().thenPlay("animation.pumpkinwarden.wave");


    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        AnimationController<E> controller = event.getController();
        controller.transitionLength(0);
        if (this.isHiding()) {
            if (this.getTimer() < 10) {
                controller.setAnimation(HIDE_START);
                return PlayState.CONTINUE;
            } else if ((this.getTimer() > 10 && this.getTimer() < 180) || !this.level().isDay() && this.getTimer() > 10) {
                controller.setAnimation(HIDE);
                return PlayState.CONTINUE;
            } else if (this.getTimer() > 180) {
                if (this.level().getBrightness(LightLayer.SKY, this.getOnPos()) > 2) {
                    controller.setAnimation(HIDE_END);
                } else {
                    controller.setAnimation(HIDE);
                }
                return PlayState.CONTINUE;
            }
        }
        if (this.getCarriedBlock() != null) {
            if (event.isMoving()) {
                controller.setAnimation(HOLDING_WALKING);
            } else {
                controller.setAnimation(HOLDING_IDLE);
            }
            return PlayState.CONTINUE;
        } else if (event.isMoving() && this.getCarriedBlock() == null) {
            controller.setAnimation(WALKING);
            return PlayState.CONTINUE;
        } else if (this.party) {
            controller.setAnimation(WAVE);
            return PlayState.CONTINUE;
        } else {
            controller.setAnimation(IDLE);
            return PlayState.CONTINUE;
        }
    }

    public void checkGoals() {
        if (this.goalSelector.getAvailableGoals().stream().noneMatch(goal -> goal.getGoal().getClass() == WaterAvoidingRandomStrollGoal.class)) {
            this.goalSelector.addGoal(1, moveGoal);
        }
        if (this.goalSelector.getAvailableGoals().stream().noneMatch(goal -> goal.getGoal().getClass() == AvoidEntityGoal.class)) {
            this.goalSelector.addGoal(2, runGoal);
        }
        if (this.goalSelector.getAvailableGoals().stream().noneMatch(goal -> goal.getGoal().getClass() == LookAtPlayerGoal.class)) {
            this.goalSelector.addGoal(7, lookGoal);
        }
        if (this.goalSelector.getAvailableGoals().stream().noneMatch(goal -> goal.getGoal().getClass() == RandomLookAroundGoal.class)) {
            this.goalSelector.addGoal(3, randLookGoal);
        }
    }

    @Override
    public void setRecordPlayingNearby(@NotNull BlockPos pPos, boolean pIsPartying) {
        this.jukebox = pPos;
        this.party = pIsPartying;
    }

    public void aiStep() {
        super.aiStep();
        if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 10D) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
            this.party = false;
            this.jukebox = null;
        }
        if (!this.level().isClientSide) {
            if (!this.level().isDay()) {
                this.setTimer(this.getTimer() + 1);
                this.setHiding(true);
            } else if (this.getTimer() > 0 && this.getLastHurtByMob() == null) {
                this.setTimer(0);
                this.setHiding(false);
            }
            if (this.getLastHurtByMob() != null) {
                if (this.getTimer() < 200) {
                    this.setTimer(this.getTimer() + 1);
                    this.setHiding(true);
                } else {
                    this.setTimer(0);
                    this.setHiding(false);
                }
            }
        }
        if (this.isHiding()) {
            this.goalSelector.removeGoal(moveGoal);
            this.goalSelector.removeGoal(runGoal);
            this.goalSelector.removeGoal(lookGoal);
            this.goalSelector.removeGoal(randLookGoal);
            if (this.getCarriedBlock() != null) {
                BehaviorUtils.throwItem(this, this.getCarriedBlock().getBlock().asItem().getDefaultInstance(), new Vec3(this.getX() + 2, this.getY(), this.getZ()));
                this.setCarriedBlock(null);
            }
        } else {
            checkGoals();
        }
        if (this.getCarriedBlock() != null) {
            this.setItemInHand(this.getUsedItemHand(), this.getCarriedBlock().getBlock().asItem().getDefaultInstance());
        } else {
            this.setItemInHand(this.getUsedItemHand(), ItemStack.EMPTY);
        }
    }


    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
    }


    public boolean isHiding() {
        return entityData.get(HIDING);
    }

    public void setHiding(boolean flag) {
        entityData.set(HIDING, flag);
    }

    public int getTimer() {
        return entityData.get(TIMER);
    }

    public void setTimer(int flag) {
        entityData.set(TIMER, flag);
    }

    public void setCarriedBlock(BlockState pState) {
        this.entityData.set(DATA_CARRY_STATE, pState == null ? Blocks.AIR.defaultBlockState() : pState);
    }

    public BlockState getCarriedBlock() {
        BlockState blockState = this.entityData.get(DATA_CARRY_STATE);
        return blockState == Blocks.AIR.defaultBlockState() ? null : blockState;
    }


    static class PumpkinWardenTakeBlockGoal extends MoveToBlockGoal {
        private final PumpkinWarden warden;

        /**
         * The delay until {@link PumpkinWardenTakeBlockGoal#findNearestBlock()}
         * attempts to find for a new melon or pumpkin block nearby.
         * This delay is counted in ticks and is continuously decremented until it hits
         * 0, at which point a new nearest block will be searched for.
         * The value of this field will be -1 if the goal has already found a
         * valid target block.
         *
         * <p>This field mainly exists for performance reasons.
         */
        private int searchNearestBlockDelay = 0;

        public PumpkinWardenTakeBlockGoal(PumpkinWarden p, double speed, int range, int y) {
            super(p, speed, range, y);
            this.warden = p;
        }

        @Override
        public boolean canUse() {
            if (this.searchNearestBlockDelay > 0) {
                this.searchNearestBlockDelay--;
            }
            if (this.warden.getCarriedBlock() != null) {
                return false;
            }
            return super.canUse();
        }

        @Override
        protected boolean findNearestBlock() {
            if (this.searchNearestBlockDelay > 0) {
              return false;
            } else if (this.searchNearestBlockDelay == -1 && this.isValidTarget(this.warden.level(), this.blockPos)) {
                return true;
            }
            if (super.findNearestBlock()) {
                this.searchNearestBlockDelay = -1;
                return true;
            } else {
                this.searchNearestBlockDelay = 20;
                return false;
            }
        }

        @Override
        public double acceptedDistance() {
            return 0;
        }

        @Override
        protected int nextStartTick(@NotNull PathfinderMob creature) {
            return 0;
        }

        public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                Level level = this.warden.level();
                BlockState blockstate = level.getBlockState(this.blockPos);
                if (blockstate.getBlock() instanceof AttachedStemBlock) {
                    level.removeBlock(this.blockPos, false);
                    level.gameEvent(GameEvent.BLOCK_DESTROY, this.blockPos, GameEvent.Context.of(this.warden, blockstate));
                    this.warden.setCarriedBlock(blockstate.getBlock().defaultBlockState());
                }
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader level, @NotNull BlockPos pos) {
            BlockState positionState = level.getBlockState(pos);
            if (positionState.is(Blocks.PUMPKIN) || positionState.is(Blocks.MELON))
                return level.getBlockState(pos.relative(Direction.Axis.X, 1)).getBlock() instanceof AttachedStemBlock || level.getBlockState(pos.relative(Direction.Axis.Z, 1)).getBlock() instanceof AttachedStemBlock;
            return false;
        }
    }

    static class PumpkinWardenLeaveBlockGoal extends MoveToBlockGoal {
        public PumpkinWarden warden;

        /**
         * The delay until {@link PumpkinWardenLeaveBlockGoal#findNearestBlock()}
         * attempts to find for a new carved pumpkin block nearby.
         * This delay is counted in ticks and is continuously decremented until it hits
         * 0, at which point a new nearest block will be searched for.
         * The value of this field will be -1 if the goal has already found a
         * valid target block.
         *
         * <p>This field mainly exists for performance reasons.
         */
        private int searchNearestBlockDelay = 0;

        public PumpkinWardenLeaveBlockGoal(PumpkinWarden warden, double speed, int range, int y) {
            super(warden, speed, range, y);
            this.warden = warden;
        }

        @Override
        public double acceptedDistance() {
            return 8D;
        }

        @Override
        protected int nextStartTick(@NotNull PathfinderMob creature) {
            return 0;
        }

        @Override
        public boolean canContinueToUse() {
            return (this.warden.getCarriedBlock() != null);
        }

        @Override
        public boolean canUse() {
            if (this.searchNearestBlockDelay > 0) {
                this.searchNearestBlockDelay--;
            }
            if (this.warden.getCarriedBlock() == null) {
                return false;
            }
            return super.canUse();
        }

        @Override
        protected boolean findNearestBlock() {
            if (this.searchNearestBlockDelay > 0) {
                return false;
            } else if (this.searchNearestBlockDelay == -1 && this.isValidTarget(this.warden.level(), this.blockPos)) {
                return true;
            }
            if (super.findNearestBlock()) {
                this.searchNearestBlockDelay = -1;
                return true;
            } else {
                this.searchNearestBlockDelay = 20;
                return false;
            }
        }

        public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                if (this.warden.getCarriedBlock() != null) {
                    BehaviorUtils.throwItem(this.warden, this.warden.getCarriedBlock().getBlock().asItem().getDefaultInstance(), new Vec3(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()));
                    this.warden.setCarriedBlock(null);
                    this.stop();
                }
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader level, @NotNull BlockPos pos) {
            return level.getBlockState(pos).is(Blocks.CARVED_PUMPKIN);
        }
    }

    static class StayByBellGoal extends MoveToBlockGoal {
        public PumpkinWarden warden;

        public StayByBellGoal(PumpkinWarden pumpkinWarden, double speedModifier, int searchRange) {
            super(pumpkinWarden, speedModifier, searchRange);
            this.warden = pumpkinWarden;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, @NotNull BlockPos pos) {
            List<BlockState> blockStates = level.getBlockStates(new AABB(warden.blockPosition()).inflate(30)).toList();
            return !blockStates.get(warden.random.nextInt(blockStates.size())).isAir();
        }
    }
}
