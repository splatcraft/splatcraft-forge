package net.splatcraft.forge.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.splatcraft.forge.blocks.IColoredBlock;
import net.splatcraft.forge.data.capabilities.worldink.WorldInk;
import net.splatcraft.forge.data.capabilities.worldink.WorldInkCapability;
import net.splatcraft.forge.registries.SplatcraftItemGroups;
import net.splatcraft.forge.registries.SplatcraftSounds;
import net.splatcraft.forge.tileentities.InkedBlockTileEntity;
import net.splatcraft.forge.util.ColorUtils;
import net.splatcraft.forge.util.InkBlockUtils;

public class InkWaxerItem extends Item
{
    public InkWaxerItem()
    {
        super(new Properties().durability(256).tab(SplatcraftItemGroups.GROUP_GENERAL));
    }

    public void onBlockStartBreak(ItemStack itemstack, BlockPos pos, Level level)
    {
        if(InkBlockUtils.isInked(level, pos))
        {
            ColorUtils.addInkDestroyParticle(level, pos, InkBlockUtils.getInk(level, pos).color());

            SoundType soundType = SplatcraftSounds.SOUND_TYPE_INK;
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), soundType.getBreakSound(), SoundSource.PLAYERS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

            InkBlockUtils.clearInk(level, pos, true);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        WorldInk worldInk = WorldInkCapability.get(context.getLevel(), context.getClickedPos());

        if(worldInk.isInked(context.getClickedPos()))
        {
            WorldInk.Entry ink = worldInk.getInk(context.getClickedPos());
            worldInk.setPermanentInk(context.getClickedPos(), ink.color(), ink.type());

            context.getLevel().levelEvent(context.getPlayer(), 3003, context.getClickedPos(), 0);

            return InteractionResult.SUCCESS;
        }

        /*
        if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof InkedBlockTileEntity)
        {
            InkedBlockTileEntity te = (InkedBlockTileEntity) context.getLevel().getBlockEntity(context.getClickedPos());

            if(te.getPermanentColor() != te.getColor())
            {
                te.setPermanentColor(te.getColor());
                te.setPermanentInkType(InkBlockUtils.getInkType(context.getLevel().getBlockState(context.getClickedPos())));

                context.getLevel().globalLevelEvent(2005, context.getClickedPos(), 0);
                if(context.getPlayer() instanceof ServerPlayer && !context.getPlayer().isCreative())
                    context.getItemInHand().hurtAndBreak(1, context.getPlayer(), player -> player.broadcastBreakEvent(context.getHand()));
                return InteractionResult.SUCCESS;
            }
        }
        */

        return super.useOn(context);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level levelIn, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 0;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem().equals(Items.HONEYCOMB) || super.isValidRepairItem(toRepair, repair);
    }
}
