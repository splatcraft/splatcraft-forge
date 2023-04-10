package net.splatcraft.forge.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class InkExplosion {
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity exploder;
    private final float size;
    private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
    private final Vec3 position;

    private final int color;
    private final InkBlockUtils.InkType inkType;
    private final boolean damageMobs;
    private final float minDamage;
    private final float maxDamage;
    private final float blockDamage;
    private final ItemStack weapon;

    public InkExplosion(Level level, @Nullable Entity source, double x, double y, double z, float blockDamage, float minDamage, float maxDamage, boolean damageMobs, float size, int color, InkBlockUtils.InkType inkType, ItemStack weapon) {
        this.level = level;
        this.exploder = source;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.position = new Vec3(this.x, this.y, this.z);


        this.color = color;
        this.inkType = inkType;
        this.damageMobs = damageMobs;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.blockDamage = blockDamage;
        this.weapon = weapon;
    }

    public static void createInkExplosion(Level level, Entity source, BlockPos pos, float size, float blockDamage, float damage, boolean damageMobs, int color, InkBlockUtils.InkType type, ItemStack weapon) {
        createInkExplosion(level, source, pos, size, blockDamage, blockDamage, damage, damageMobs, color, type, weapon);
    }

    public static void createInkExplosion(Level level, Entity source, BlockPos pos, float size, float blockDamage, float minDamage, float maxDamage, boolean damageMobs, int color, InkBlockUtils.InkType type, ItemStack weapon) {

        if (level.isClientSide)
            return;

        InkExplosion inksplosion = new InkExplosion(level, source, pos.getX(), pos.getY(), pos.getZ(), blockDamage, minDamage, maxDamage, damageMobs, size, color, type, weapon);

        inksplosion.doExplosionA();
        inksplosion.doExplosionB(false);
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    public void doExplosionA()
    {
        Set<BlockPos> set = Sets.newHashSet();

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

                        for (; f > 0.0F; f -= 0.22500001F) {
                            BlockHitResult raytrace = level.clip(new ClipContext(new Vec3(x + 0.5f, y + 0.5f, z + 0.5f), new Vec3(d4 + 0.5f, d6 + 0.5f, d8 + 0.5f), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
                            BlockPos blockpos;
                            f -= 0.3f * 0.3F;

                            blockpos = raytrace.getBlockPos();
                            if (InkBlockUtils.canInkFromFace(level, blockpos, raytrace.getDirection()))
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
        int k1 = Mth.floor(this.x - (double) f2 - 1.0D);
        int l1 = Mth.floor(this.x + (double) f2 + 1.0D);
        int i2 = Mth.floor(this.y - (double) f2 - 1.0D);
        int i1 = Mth.floor(this.y + (double) f2 + 1.0D);
        int j2 = Mth.floor(this.z - (double) f2 - 1.0D);
        int j1 = Mth.floor(this.z + (double) f2 + 1.0D);
        List<Entity> list = this.level.getEntities(this.exploder, new AABB(k1, i2, j2, l1, i1, j1));

        Vec3 explosionPos = new Vec3(x + 0.5f, y + 0.5f, z + 0.5f);
        for (Entity entity : list)
        {
            int targetColor = -2;
            if (entity instanceof LivingEntity)
                targetColor = ColorUtils.getEntityColor(entity);

            if (targetColor == -1 || (color != targetColor && targetColor > -1))
            {
                double f2Sq = f2 * f2;
                float pctg = Math.max(0, (float) ((f2Sq - entity.distanceToSqr(x + 0.5f, y + 0.5f, z + 0.5f)) / f2Sq));

                InkDamageUtils.doSplatDamage(level, (LivingEntity) entity, Mth.lerp(pctg, minDamage, maxDamage) * Explosion.getSeenPercent(explosionPos, entity), color, exploder, weapon, damageMobs);
            }

            DyeColor dyeColor = null;

            if (InkColor.getByHex(color) != null)
                dyeColor = InkColor.getByHex(color).getDyeColor();

            if (dyeColor != null && entity instanceof Sheep)
                ((Sheep) entity).setColor(dyeColor);
        }

    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    public void doExplosionB(boolean spawnParticles)
    {
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

        Collections.shuffle(this.affectedBlockPositions, this.level.random);

        for (BlockPos blockpos : this.affectedBlockPositions)
        {
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (!blockstate.isAir())
            {
                if (exploder instanceof Player)
                {
                    InkBlockUtils.playerInkBlock((Player) exploder, level, blockpos, color, blockDamage, inkType);
                } else
                {
                    InkBlockUtils.inkBlock(level, blockpos, color, blockDamage, inkType);
                }
            }

        }
    }

    public Vec3 getPosition()
    {
        return this.position;
    }
}
