package net.splatcraft.forge.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.splatcraft.forge.blocks.InkedBlock;
import net.splatcraft.forge.blocks.InkwellBlock;
import net.splatcraft.forge.data.capabilities.playerinfo.PlayerInfoCapability;
import net.splatcraft.forge.entities.SquidBumperEntity;
import net.splatcraft.forge.registries.SplatcraftEntities;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftItems;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.tileentities.InkColorTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SquidBumperItem extends Item implements IColoredItem
{
    public SquidBumperItem()
    {
        super(new Properties().stacksTo(16).tab(SplatcraftItemGroups.GROUP_GENERAL));
        SplatcraftItems.inkColoredItems.add(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        if (ColorUtils.isColorLocked(stack))
        {
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof Player && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != 0xFFFFFF - ColorUtils.getPlayerColor((Player) entity)
                && PlayerInfoCapability.hasCapability((LivingEntity) entity))
        {
            ColorUtils.setInkColor(stack, 0xFFFFFF - ColorUtils.getPlayerColor((Player) entity));
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        BlockPos pos = entity.blockPosition().below();

        if (entity.level.getBlockState(pos).getBlock() instanceof InkwellBlock)
        {
            InkColorTileEntity te = (InkColorTileEntity) entity.level.getBlockEntity(pos);

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
            {
                ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
                ColorUtils.setColorLocked(entity.getItem(), true);
            }
        }
        else if(InkedBlock.causesClear(entity.level, pos, entity.level.getBlockState(pos)) && ColorUtils.isColorLocked(stack))
        {
            ColorUtils.setInkColor(stack, 0xFFFFFF);
            ColorUtils.setColorLocked(stack, false);
        }

        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        if (context.getClickedFace() == Direction.DOWN)
            return InteractionResult.FAIL;

        Level level = context.getLevel();
        BlockPos pos = new BlockPlaceContext(context).getClickedPos();
        ItemStack stack = context.getItemInHand();

        Vec3 vector3d = Vec3.atBottomCenterOf(pos);
        AABB axisalignedbb = SplatcraftEntities.SQUID_BUMPER.get().getDimensions().makeBoundingBox(vector3d.x(), vector3d.y(), vector3d.z());
        if (level.noCollision(null, axisalignedbb) && level.getEntities(null, axisalignedbb).isEmpty())
        {
            if (level instanceof ServerLevel)
            {
                SquidBumperEntity bumper = SplatcraftEntities.SQUID_BUMPER.get().create((ServerLevel) level, stack.getTag(), null, context.getPlayer(), pos, MobSpawnType.SPAWN_EGG, true, true);
                if(bumper != null)
                {
                    bumper.setColor(ColorUtils.getInkColor(stack));
                    float f = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    bumper.moveTo(bumper.getX(), bumper.getY(), bumper.getZ(), f, 0);
                    bumper.setYHeadRot(f);
                    bumper.yHeadRotO = f;

                    level.addFreshEntity(bumper);
                    level.playSound(null, bumper.getX(), bumper.getY(), bumper.getZ(), SplatcraftSounds.squidBumperPlace, SoundSource.BLOCKS, 0.75F, 0.8F);
                }
            }
            stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }


        return InteractionResult.FAIL;
    }
}
