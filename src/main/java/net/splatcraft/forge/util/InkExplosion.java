package net.splatcraft.forge.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class InkExplosion
{
    private final Random random = new Random();
    private final World level;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity exploder;
    private final float size;
    private final DamageSource damageSource;
    private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
    private final Map<PlayerEntity, Vector3d> playerKnockbackMap = Maps.newHashMap();
    private final Vector3d position;

    private final int color;
    private final InkBlockUtils.InkType inkType;
    private final boolean damageMobs;
    private final float minDamage;
    private final float maxDamage;
    private final float blockDamage;
    private final ItemStack weapon;

    @OnlyIn(Dist.CLIENT)
    public InkExplosion(World levelIn, @Nullable Entity exploderIn, double xIn, double yIn, double zIn, float blockDamage, float minDamage, float maxDamage, boolean damageMobs, float sizeIn, int color, InkBlockUtils.InkType inkType, ItemStack weapon)
    {
        this(levelIn, exploderIn, null, xIn, yIn, zIn, blockDamage, minDamage, maxDamage, damageMobs, sizeIn, color, inkType, weapon);
    }

    public InkExplosion(World level, @Nullable Entity source, @Nullable DamageSource damageSource, double x, double y, double z, float blockDamage, float minDamage, float maxDamage, boolean damageMobs, float size, int color, InkBlockUtils.InkType inkType, ItemStack weapon)
    {
        this.level = level;
        this.exploder = source;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.damageSource = damageSource;
        this.position = new Vector3d(this.x, this.y, this.z);


        this.color = color;
        this.inkType = inkType;
        this.damageMobs = damageMobs;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.blockDamage = blockDamage;
        this.weapon = weapon;
    }

    public static void createInkExplosion(World level, Entity source, DamageSource damageSource, BlockPos pos, float size, float blockDamage, float damage, boolean damageMobs, int color, InkBlockUtils.InkType type, ItemStack weapon)
    {
        createInkExplosion(level, source, damageSource, pos, size, blockDamage, damage, damage, damageMobs, color, type, weapon);
    }

    public static void createInkExplosion(World level, Entity source, DamageSource damageSource, BlockPos pos, float size, float blockDamage, float minDamage, float maxDamage, boolean damageMobs, int color, InkBlockUtils.InkType type, ItemStack weapon)
    {

        if (level.isClientSide)
            return;

        InkExplosion inksplosion = new InkExplosion(level, source, damageSource, pos.getX(), pos.getY(), pos.getZ(), blockDamage, minDamage, maxDamage, damageMobs, size, color, type, weapon);

        inksplosion.doExplosionA();
        inksplosion.doExplosionB(false);
    }

    public static float getBlockDensity(Vector3d p_222259_0_, Entity p_222259_1_)
    {
        AxisAlignedBB axisalignedbb = p_222259_1_.getBoundingBox();
        double d0 = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
        if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D))
        {
            int i = 0;
            int j = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0))
            {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1))
                {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2))
                    {
                        double d5 = MathHelper.lerp(f, axisalignedbb.minX, axisalignedbb.maxX);
                        double d6 = MathHelper.lerp(f1, axisalignedbb.minY, axisalignedbb.maxY);
                        double d7 = MathHelper.lerp(f2, axisalignedbb.minZ, axisalignedbb.maxZ);
                        Vector3d vector3d = new Vector3d(d5 + d3, d6, d7 + d4);
                        if (p_222259_1_.level.clip(new RayTraceContext(vector3d, p_222259_0_, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, p_222259_1_)).getType() == RayTraceResult.Type.MISS)
                        {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float) i / (float) j;
        } else
        {
            return 0.0F;
        }
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    public void doExplosionA()
    {
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                for (int l = 0; l < 16; ++l)
                {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15)
                    {
                        double d0 = (float) j / 15.0F * 2.0F - 1.0F;
                        double d1 = (float) k / 15.0F * 2.0F - 1.0F;
                        double d2 = (float) l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.size * (0.7F + this.level.getRandom().nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
                        {
                            BlockRayTraceResult raytrace = level.clip(new RayTraceContext(new Vector3d(x + 0.5f, y + 0.5f, z + 0.5f), new Vector3d(d4 + 0.5f, d6 + 0.5f, d8 + 0.5f), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            f -= 0.3f * 0.3F;

                            blockpos = raytrace.getBlockPos();
                            if(InkBlockUtils.canInkFromFace(level, blockpos, raytrace.getDirection()))
                                set.add(blockpos);


                            d4 += d0 * (double) 0.3F;
                            d6 += d1 * (double) 0.3F;
                            d8 += d2 * (double) 0.3F;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(set);
        float f2 = this.size * 1.2f;
        int k1 = MathHelper.floor(this.x - (double) f2 - 1.0D);
        int l1 = MathHelper.floor(this.x + (double) f2 + 1.0D);
        int i2 = MathHelper.floor(this.y - (double) f2 - 1.0D);
        int i1 = MathHelper.floor(this.y + (double) f2 + 1.0D);
        int j2 = MathHelper.floor(this.z - (double) f2 - 1.0D);
        int j1 = MathHelper.floor(this.z + (double) f2 + 1.0D);
        List<Entity> list = this.level.getEntities(this.exploder, new AxisAlignedBB(k1, i2, j2, l1, i1, j1));
        Vector3d vector3d = new Vector3d(this.x, this.y, this.z);

        for (Entity entity : list)
        {
            int targetColor = -2;
            if (entity instanceof LivingEntity)
                targetColor = ColorUtils.getEntityColor(entity);



            if (targetColor == -1 || (color != targetColor && targetColor > -1))
            {
                float pctg = Math.max(0, (float) (entity.distanceToSqr(x, y, z)/Math.pow(f2, 2)));

                InkDamageUtils.doSplatDamage(level, (LivingEntity) entity, MathHelper.lerp(pctg, maxDamage, minDamage), color, exploder, weapon, damageMobs, inkType);
            }

            DyeColor dyeColor = null;

            if (InkColor.getByHex(color) != null)
                dyeColor = InkColor.getByHex(color).getDyeColor();

            if (dyeColor != null && entity instanceof SheepEntity)
                ((SheepEntity) entity).setColor(dyeColor);

            /*
            if (!entity.isImmuneToExplosions())
            {
                double d12 = (double)(MathHelper.sqrt(entity.distanceToSqr(vector3d)) / f2);
                if (d12 <= 1.0D)
                {
                    double d5 = entity.getX() - this.x;
                    double d7 = (entity instanceof TNTEntity ? entity.getY() : entity.getEyeY()) - this.y;
                    double d9 = entity.getZ() - this.z;
                    double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != 0.0D) {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        double d14 = (double)getBlockDensity(vector3d, entity);
                        double d10 = (1.0D - d12) * d14;
                        entity.hurt(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f2 + 1.0D)));
                        double d11 = d10;
                        if (entity instanceof LivingEntity) {
                            d11 = ProtectionEnchantment.getBlastDamageReduction((LivingEntity)entity, d10);
                        }
                    }
                }
            }
            */
        }

    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    public void doExplosionB(boolean spawnParticles)
    {
        /*
        if (this.level.isClientSide) {
            this.level.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.level.getRandom().nextFloat() - this.level.getRandom().nextFloat()) * 0.2F) * 0.7F, false);
        }
        boolean flag = this.mode != Explosion.Mode.NONE;
        */
        if (spawnParticles)
        {
            if (!(this.size < 2.0F))
            {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            } else
            {
                this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            }
        }

        ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
        Collections.shuffle(this.affectedBlockPositions, this.level.random);

        for (BlockPos blockpos : this.affectedBlockPositions)
        {
            BlockState blockstate = this.level.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (!blockstate.isAir(this.level, blockpos))
            {
                if (exploder instanceof PlayerEntity)
                {
                    InkBlockUtils.playerInkBlock((PlayerEntity) exploder, level, blockpos, color, blockDamage, inkType);
                } else
                {
                    InkBlockUtils.inkBlock(level, blockpos, color, blockDamage, inkType);
                }
            }

        }

        for (Pair<ItemStack, BlockPos> pair : objectarraylist)
        {
            CommonUtils.blockDrop(this.level, pair.getSecond(), pair.getFirst());
        }

    }

    public DamageSource getDamageSource()
    {
        return this.damageSource;
    }

    public Map<PlayerEntity, Vector3d> getPlayerKnockbackMap()
    {
        return this.playerKnockbackMap;
    }

    public void clearAffectedBlockPositions()
    {
        this.affectedBlockPositions.clear();
    }

    public List<BlockPos> getAffectedBlockPositions()
    {
        return this.affectedBlockPositions;
    }

    public Vector3d getPosition()
    {
        return this.position;
    }

    @Nullable
    public Entity getExploder()
    {
        return this.exploder;
    }

}
