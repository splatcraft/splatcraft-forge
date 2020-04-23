package com.cibernet.splatcraft.entities.classes;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.utils.InkColors;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityNPCSquid extends EntityCreature
{
	private static final DataParameter<Float> COLOR = EntityDataManager.createKey(EntityNPCSquid.class, DataSerializers.FLOAT);
	
	public EntityNPCSquid(World worldIn)
	{
		super(worldIn);
		setSize(0.6f, 0.6f);
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataManager.register(COLOR, (float) SplatCraft.DEFAULT_INK);
	}
	
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
	}
	
	//Entity AI
	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.6D));
		this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
		BlockPos pos = new BlockPos(posX, posY-1, posZ);
		
		if(world.getBlockState(pos).getBlock().equals(SplatCraftBlocks.inkwell) && world.getTileEntity(pos) instanceof TileEntityColor)
		{
			TileEntityColor te = (TileEntityColor) world.getTileEntity(pos);
			if(te.getColor() != getColor())
				setColor(te.getColor());
		}
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		
		if(compound.hasKey("Color"))
			setColor(compound.getInteger("Color"));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		
		compound.setInteger("Color", getColor());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if(compound.hasKey("Color"))
			setColor(compound.getInteger("Color"));
		else setColor(InkColors.getRandomStarterColor().getColor());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("Color", getColor());
		return super.writeToNBT(compound);
	}
	
	public int getColor() {return dataManager.get(COLOR).intValue();}
	public void setColor(int color) {dataManager.set(COLOR, (float)color);}
}
