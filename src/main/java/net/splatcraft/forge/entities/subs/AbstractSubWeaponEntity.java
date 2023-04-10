package net.splatcraft.forge.entities.subs;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.splatcraft.forge.client.particles.InkExplosionParticleData;
import net.splatcraft.forge.entities.IColoredEntity;
import net.splatcraft.forge.handlers.WeaponHandler;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AbstractSubWeaponEntity extends Entity implements IColoredEntity
{
    protected static final String SPLASH_DAMAGE_TYPE = "splat";

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(AbstractSubWeaponEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(AbstractSubWeaponEntity.class, EntityDataSerializers.ITEM_STACK);

    public boolean isItem = false;
    public boolean bypassMobDamageMultiplier = false;
    public InkBlockUtils.InkType inkType;
    public ItemStack sourceWeapon = ItemStack.EMPTY;

    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;

    @Deprecated //use AbstractWeaponEntity.create
    public AbstractSubWeaponEntity(EntityType<? extends AbstractSubWeaponEntity> type, Level level)
    {
        super(type, level);
    }

    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, Level level, @NotNull LivingEntity thrower, ItemStack sourceWeapon)
    {
        return create(type, level, thrower, ColorUtils.getInkColor(sourceWeapon), InkBlockUtils.getInkType(thrower), sourceWeapon);
    }


    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, Level level, LivingEntity thrower, int color, InkBlockUtils.InkType inkType, ItemStack sourceWeapon)
    {
        A result = create(type, level, thrower.getX(), thrower.getEyeY() - (double)0.1F, thrower.getZ(), color, inkType, sourceWeapon);
        result.setOwner(thrower);

        return result;
    }
    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, Level level, double x, double y, double z, int color, InkBlockUtils.InkType inkType, ItemStack sourceWeapon)
    {
        A result = type.create(level);
        result.setPos(x, y, z);
        result.setColor(color);
        result.inkType = inkType;
        result.sourceWeapon = sourceWeapon;

        result.readItemData(sourceWeapon.getOrCreateTag().getCompound("EntityData"));

        return result;
    }

    public void readItemData(CompoundTag nbt)
    {

    }

    @Override
    public void tick()
    {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        super.tick();

        if(isInWater())
        {
            level.broadcastEntityEvent(this, (byte) -1);
            discard();
        }

        Vec3 raytraceOffset = new Vec3(getBbWidth()/2f * Math.signum(getDeltaMovement().x), getBbHeight() * Math.max(0, Math.signum(getDeltaMovement().y)), getBbWidth()/2f * Math.signum(getDeltaMovement().z));

        setDeltaMovement(getDeltaMovement().add(raytraceOffset));
        HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        setDeltaMovement(getDeltaMovement().subtract(raytraceOffset));

        boolean flag = false;
        if (raytraceresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                flag = true;
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                if (this.level.getBlockEntity(blockpos) instanceof TheEndGatewayBlockEntity  gate && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(level, blockpos, blockstate, this, gate);
                }

                flag = true;
            }
        }

        if (raytraceresult.getType() != HitResult.Type.MISS && !flag /*!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)*/) {
            this.onHit(raytraceresult);
        }

        this.checkInsideBlocks();
        Vec3 vector3d = this.getDeltaMovement();

        Vec3 newPos = new Vec3(getX()+getDeltaMovement().x, getY()+getDeltaMovement().y, getZ()+getDeltaMovement().z);

        double d2 = this.getX() + vector3d.x;
        double d0 = this.getY() + vector3d.y;
        double d1 = this.getZ() + vector3d.z;
        this.updateRotation();
        float f;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                float f1 = 0.25F;
                this.level.addParticle(ParticleTypes.BUBBLE, d2 - vector3d.x * 0.25D, d0 - vector3d.y * 0.25D, d1 - vector3d.z * 0.25D, vector3d.x, vector3d.y, vector3d.z);
            }

            f = 0.8F;
        } else {
            f = 0.99F;
        }

        this.setDeltaMovement(vector3d.scale(f));
        if (!this.isNoGravity()) {
            Vec3 vector3d1 = this.getDeltaMovement();
            this.setDeltaMovement(vector3d1.x, vector3d1.y - (double)this.getGravity(), vector3d1.z);
        }


        if(handleMovement())
            setPos(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);

        if (id == -1) {
            level.addParticle(new InkExplosionParticleData(getColor(), .5f), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    public void shoot(Entity thrower, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy)
    {
        shootFromRotation(thrower, pitch, yaw, pitchOffset, velocity, inaccuracy);

        Vec3 posDiff = new Vec3(0, 0, 0);

        if (thrower instanceof Player)
        {
            try
            {
                posDiff = thrower.position().subtract(WeaponHandler.getPlayerPrevPos((Player) thrower));
                if(thrower.isOnGround())
                    posDiff.multiply(1, 0, 1);

            } catch (NullPointerException ignored)
            {
            }
        }

        moveTo(getX() + posDiff.x(), getY() + posDiff.y(), getZ() + posDiff.z());
        setDeltaMovement(getDeltaMovement().add(posDiff.multiply(0.8, 0.8, 0.8)));
    }

    public float getGravity() {
        return 0.09f;
    }

    protected boolean handleMovement()
    {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        nbt.putInt("Color", getColor());
        nbt.putBoolean("DypassMobDamageMultiplier", bypassMobDamageMultiplier);
        nbt.putString("InkType", inkType.getSerializedName());
        nbt.put("SourceWeapon", sourceWeapon.save(new CompoundTag()));

        if (this.ownerUUID != null)
            nbt.putUUID("Owner", this.ownerUUID);

        if (this.leftOwner)
            nbt.putBoolean("LeftOwner", true);

        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty())
            nbt.put("Item", itemstack.save(new CompoundTag()));
    }


    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        if(nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));
        bypassMobDamageMultiplier = nbt.getBoolean("DypassMobDamageMultiplier");
        inkType = InkBlockUtils.InkType.values.getOrDefault(new ResourceLocation(nbt.getString("InkType")), InkBlockUtils.InkType.NORMAL);
        sourceWeapon = ItemStack.of(nbt.getCompound("SourceWeapon"));

        if (nbt.hasUUID("Owner"))
            this.ownerUUID = nbt.getUUID("Owner");

        this.leftOwner = nbt.getBoolean("LeftOwner");

        ItemStack itemstack = ItemStack.of(nbt.getCompound("Item"));
        this.setItem(itemstack);
    }


    protected void onBlockHit(BlockHitResult result) { }
    protected void onHitEntity(EntityHitResult result) { }

    protected void onHit(HitResult result)
    {
        HitResult.Type rayType = result.getType();
        if (rayType == HitResult.Type.ENTITY)
        {
            this.onHitEntity((EntityHitResult) result);
        } else if (rayType == HitResult.Type.BLOCK)
        {
            onBlockHit((BlockHitResult) result);
        }
    }

    @Override
    protected void defineSynchedData()
    {
        entityData.define(DATA_ITEM_STACK, ItemStack.EMPTY);
        entityData.define(COLOR, ColorUtils.DEFAULT);
    }

    @Override
    public int getColor() {
        return entityData.get(COLOR);
    }

    @Override
    public void setColor(int color)
    {
        entityData.set(COLOR, color);
    }

    public void setOwner(@Nullable Entity p_212361_1_) {
        if (p_212361_1_ != null) {
            this.ownerUUID = p_212361_1_.getUUID();
            this.ownerNetworkId = p_212361_1_.getId();
        }

    }

    protected abstract Item getDefaultItem();

    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    public void setItem(ItemStack item) {
        if (item.getItem() != this.getDefaultItem() || item.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, Util.make(item.copy(), (p_213883_0_) -> {
                p_213883_0_.setCount(1);
            }));
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            return ((ServerLevel)this.level).getEntity(this.ownerUUID);
        } else {
            return this.ownerNetworkId != 0 ? this.level.getEntity(this.ownerNetworkId) : null;
        }
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for(Entity entity1 : this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_234613_0_) -> {
                return !p_234613_0_.isSpectator() && p_234613_0_.isPickable();
            })) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
        Vec3 vector3d = (new Vec3(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.random.nextGaussian() * (double) 0.0075F * (double) p_70186_8_, this.random.nextGaussian() * (double) 0.0075F * (double) p_70186_8_, this.random.nextGaussian() * (double) 0.0075F * (double) p_70186_8_).scale(p_70186_7_);
        this.setDeltaMovement(vector3d);
        float f = Mth.sqrt((float) distanceToSqr(vector3d));
        this.setYRot((float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        float f = -Mth.sin(p_234612_3_ * ((float)Math.PI / 180F)) * Mth.cos(p_234612_2_ * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(p_234612_3_ * ((float)Math.PI / 180F)) * Mth.cos(p_234612_2_ * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
        Vec3 vector3d = p_234612_1_.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        this.setDeltaMovement(p_70016_1_, p_70016_3_, p_70016_5_);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = Mth.sqrt((float) (p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_));
            this.setXRot((float) (Mth.atan2(p_70016_3_, f) * (double) (180F / (float) Math.PI)));
            this.setYRot((float) (Mth.atan2(p_70016_1_, p_70016_5_) * (double) (180F / (float) Math.PI)));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    public boolean canHitEntity(Entity p_230298_1_) {
        if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
            Entity entity = this.getOwner();
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_);
        } else {
            return false;
        }
    }

    protected void updateRotation() {
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float) distanceToSqr(vector3d));
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI))));

        if(vector3d.multiply(1, 0, 1).length() >= 0.001)
            this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI))));
    }

    protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
        while(p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while(p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_234614_0_, p_234614_1_);
    }
}
