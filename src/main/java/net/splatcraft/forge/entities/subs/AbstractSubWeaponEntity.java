package net.splatcraft.forge.entities.subs;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
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
    protected static final DamageSource SPLASH_DAMAGE_SOURCE = new DamageSource(SPLASH_DAMAGE_TYPE);

    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(AbstractSubWeaponEntity.class, DataSerializers.INT);
    private static final DataParameter<ItemStack> DATA_ITEM_STACK = EntityDataManager.defineId(AbstractSubWeaponEntity.class, DataSerializers.ITEM_STACK);


    public boolean isItem = false;
    public boolean damageMobs = false;
    public InkBlockUtils.InkType inkType;
    public ItemStack sourceWeapon = ItemStack.EMPTY;

    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;

    @Deprecated //use AbstractWeaponEntity.create
    public AbstractSubWeaponEntity(EntityType<? extends AbstractSubWeaponEntity> type, World level)
    {
        super(type, level);
    }

    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, World level, @NotNull LivingEntity thrower, ItemStack sourceWeapon)
    {
        return create(type, level, thrower, ColorUtils.getInkColor(sourceWeapon), InkBlockUtils.getInkType(thrower), sourceWeapon);
    }


    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, World level, LivingEntity thrower, int color, InkBlockUtils.InkType inkType, ItemStack sourceWeapon)
    {
        A result = create(type, level, thrower.getX(), thrower.getEyeY() - (double)0.1F, thrower.getZ(), color, inkType, sourceWeapon);
        result.setOwner(thrower);

        return result;
    }
    public static <A extends AbstractSubWeaponEntity> A create(EntityType<A> type, World level, double x, double y, double z, int color, InkBlockUtils.InkType inkType, ItemStack sourceWeapon)
    {
        A result = type.create(level);
        result.setPos(x, y, z);
        result.setColor(color);
        result.inkType = inkType;
        result.sourceWeapon = sourceWeapon;

        return result;
    }

    @Override
    public void tick()
    {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        super.tick();

        RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
        boolean flag = false;
        if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                flag = true;
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                TileEntity tileentity = this.level.getBlockEntity(blockpos);
                if (tileentity instanceof EndGatewayTileEntity && EndGatewayTileEntity.canEntityTeleport(this)) {
                    ((EndGatewayTileEntity)tileentity).teleportEntity(this);
                }

                flag = true;
            }
        }

        if (raytraceresult.getType() != RayTraceResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
        }

        this.checkInsideBlocks();
        Vector3d vector3d = this.getDeltaMovement();

        Vector3d newPos = new Vector3d(getX()+getDeltaMovement().x, getY()+getDeltaMovement().y, getZ()+getDeltaMovement().z);

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
            Vector3d vector3d1 = this.getDeltaMovement();
            this.setDeltaMovement(vector3d1.x, vector3d1.y - (double)this.getGravity(), vector3d1.z);
        }


        if(handleMovement())
            setPos(newPos.x, newPos.y, newPos.z);
    }

    public void shoot(Entity thrower, float pitch, float yaw, float pitchOffset, float velocity, float inaccuracy)
    {
        shootFromRotation(thrower, pitch, yaw, pitchOffset, velocity, inaccuracy);

        Vector3d posDiff = new Vector3d(0, 0, 0);

        if (thrower instanceof PlayerEntity)
        {
            try
            {
                posDiff = thrower.position().subtract(WeaponHandler.getPlayerPrevPos((PlayerEntity) thrower));
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
    public void addAdditionalSaveData(CompoundNBT nbt)
    {
        nbt.putInt("Color", getColor());
        nbt.putBoolean("DamageMobs", damageMobs);
        nbt.putString("InkType", inkType.getSerializedName());
        nbt.put("SourceWeapon", sourceWeapon.save(new CompoundNBT()));

        if (this.ownerUUID != null)
            nbt.putUUID("Owner", this.ownerUUID);

        if (this.leftOwner)
            nbt.putBoolean("LeftOwner", true);

        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty())
            nbt.put("Item", itemstack.save(new CompoundNBT()));
    }


    @Override
    public void readAdditionalSaveData(CompoundNBT nbt)
    {
        if(nbt.contains("Color"))
            setColor(ColorUtils.getColorFromNbt(nbt));
        damageMobs = nbt.getBoolean("DamageMobs");
        inkType = InkBlockUtils.InkType.values.getOrDefault(new ResourceLocation(nbt.getString("InkType")), InkBlockUtils.InkType.NORMAL);
        sourceWeapon = ItemStack.of(nbt.getCompound("SourceWeapon"));

        if (nbt.hasUUID("Owner"))
            this.ownerUUID = nbt.getUUID("Owner");

        this.leftOwner = nbt.getBoolean("LeftOwner");

        ItemStack itemstack = ItemStack.of(nbt.getCompound("Item"));
        this.setItem(itemstack);
    }


    protected void onBlockHit(BlockRayTraceResult result) { }
    protected void onHitEntity(EntityRayTraceResult result) { }

    protected void onHit(RayTraceResult result)
    {
        RayTraceResult.Type rayType = result.getType();
        if (rayType == RayTraceResult.Type.ENTITY)
        {
            this.onHitEntity((EntityRayTraceResult) result);
        } else if (rayType == RayTraceResult.Type.BLOCK)
        {
            onBlockHit((BlockRayTraceResult) result);
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
        if (this.ownerUUID != null && this.level instanceof ServerWorld) {
            return ((ServerWorld)this.level).getEntity(this.ownerUUID);
        } else {
            return this.ownerNetworkId != 0 ? this.level.getEntity(this.ownerNetworkId) : null;
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket()
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
        Vector3d vector3d = (new Vector3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.random.nextGaussian() * (double) 0.0075F * (double) p_70186_8_, this.random.nextGaussian() * (double) 0.0075F * (double) p_70186_8_, this.random.nextGaussian() * (double) 0.0075F * (double) p_70186_8_).scale(p_70186_7_);
        this.setDeltaMovement(vector3d);
        float f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
        this.yRot = (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
        this.xRot = (float) (MathHelper.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI));
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }

    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        float f = -MathHelper.sin(p_234612_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_234612_2_ * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin((p_234612_2_ + p_234612_4_) * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(p_234612_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_234612_2_ * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
        Vector3d vector3d = p_234612_1_.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        this.setDeltaMovement(p_70016_1_, p_70016_3_, p_70016_5_);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            this.xRot = (float) (MathHelper.atan2(p_70016_3_, f) * (double) (180F / (float) Math.PI));
            this.yRot = (float) (MathHelper.atan2(p_70016_1_, p_70016_5_) * (double) (180F / (float) Math.PI));
            this.xRotO = this.xRot;
            this.yRotO = this.yRot;
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
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
        Vector3d vector3d = this.getDeltaMovement();
        float f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
        this.xRot = lerpRotation(this.xRotO, (float) (MathHelper.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI)));
        this.yRot = lerpRotation(this.yRotO, (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI)));
    }

    protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
        while(p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while(p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return MathHelper.lerp(0.2F, p_234614_0_, p_234614_1_);
    }
}
