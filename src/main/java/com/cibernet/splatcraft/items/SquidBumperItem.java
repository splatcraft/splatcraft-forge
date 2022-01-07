package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.entities.SquidBumperEntity;
import com.cibernet.splatcraft.registries.SplatcraftEntities;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class SquidBumperItem extends Item implements IColoredItem
{
    public SquidBumperItem(String name)
    {
        super(new Properties().stacksTo(16).tab(SplatcraftItemGroups.GROUP_GENERAL));
        SplatcraftItems.inkColoredItems.add(this);
        setRegistryName(name);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        if (ColorUtils.isColorLocked(stack))
        {
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);

        if (entity instanceof PlayerEntity && !ColorUtils.isColorLocked(stack) && ColorUtils.getInkColor(stack) != 0xFFFFFF - ColorUtils.getPlayerColor((PlayerEntity) entity)
                && PlayerInfoCapability.hasCapability((LivingEntity) entity))
        {
            ColorUtils.setInkColor(stack, 0xFFFFFF - ColorUtils.getPlayerColor((PlayerEntity) entity));
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
        else if(InkedBlock.causesClear(entity.level.getBlockState(pos)) && ColorUtils.isColorLocked(stack))
        {
            ColorUtils.setInkColor(stack, 0xFFFFFF);
            ColorUtils.setColorLocked(stack, false);
        }

        return false;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        if (context.getClickedFace() == Direction.DOWN)
            return ActionResultType.FAIL;

        World level = context.getLevel();
        BlockPos pos = new BlockItemUseContext(context).getClickedPos();
        ItemStack stack = context.getItemInHand();

        Vector3d vector3d = Vector3d.atBottomCenterOf(pos);
        AxisAlignedBB axisalignedbb = SplatcraftEntities.SQUID_BUMPER.getDimensions().makeBoundingBox(vector3d.x(), vector3d.y(), vector3d.z());
        if (level.noCollision(null, axisalignedbb, (entity) -> true) && level.getEntities(null, axisalignedbb).isEmpty())
        {
            if (level instanceof ServerWorld)
            {
                SquidBumperEntity bumper = SplatcraftEntities.SQUID_BUMPER.create((ServerWorld) level, stack.getTag(), null, context.getPlayer(), pos, SpawnReason.SPAWN_EGG, true, true);
                if(bumper != null)
                {
                    bumper.setColor(ColorUtils.getInkColor(stack));
                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    bumper.moveTo(bumper.getX(), bumper.getY(), bumper.getZ(), f, 0);
                    bumper.setYHeadRot(f);
                    bumper.yHeadRotO = f;

                    level.addFreshEntity(bumper);
                    level.playSound(null, bumper.getX(), bumper.getY(), bumper.getZ(), SplatcraftSounds.squidBumperPlace, SoundCategory.BLOCKS, 0.75F, 0.8F);
                }
            }
            stack.shrink(1);
            return ActionResultType.sidedSuccess(level.isClientSide);
        }


        return ActionResultType.FAIL;
    }
}
