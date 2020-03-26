package com.cibernet.splatcraft.tileentities;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntitySunkenCrate extends TileEntityColor
{

    private final int maxHealth = 20;
    private int health = maxHealth;

    public static final ResourceLocation STORAGE_SUNKEN_CRATE = new ResourceLocation(SplatCraft.MODID, "storage/sunken_crate");

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("health"))
            health = compound.getInteger("health");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("health", health);
        return super.writeToNBT(compound);
    }

    public int getState() {return 4 - Math.round(health*4/maxHealth);}

    public void addHealth() {health++;}

    public void ink(int color, int damage)
    {
        if(world.isRemote)
            return;
        setColor(color);
        health -= damage;
        if(health <= 0)
        {
            world.destroyBlock(pos, false);

            LootContext.Builder contextBuilder = new LootContext.Builder((WorldServer)world);
            List<ItemStack> loot = world.getLootTableManager().getLootTableFromLocation(STORAGE_SUNKEN_CRATE).generateLootForPools(world.rand, contextBuilder.build());

            for(ItemStack stack : loot)
                SplatCraftUtils.dropItem(world, pos, stack, false);
        }
    }

    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 2, this.getUpdateTag());
    }

    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.handleUpdateTag(pkt.getNbtCompound());
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 2);
        }

    }
}
