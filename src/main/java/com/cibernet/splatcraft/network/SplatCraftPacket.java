package com.cibernet.splatcraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;

public abstract class SplatCraftPacket
{
	protected ByteBuf data = Unpooled.buffer();
	
	
	public SplatCraftPacket() {
	}
	
	public static SplatCraftPacket makePacket(SplatCraftPacket.Type type, Object... dat) {
		return type.make().generatePacket(dat);
	}
	
	public static String readLine(ByteBuf data) {
		StringBuilder str = new StringBuilder();
		
		while(data.readableBytes() > 0) {
			char c = data.readChar();
			if (c == '\n') {
				break;
			}
			
			str.append(c);
		}
		
		return str.toString();
	}
	
	public static void writeString(ByteBuf data, String str) {
		for(int i = 0; i < str.length(); ++i) {
			data.writeChar(str.charAt(i));
		}
		
	}
	
	public abstract SplatCraftPacket generatePacket(Object... var1);
	
	public abstract SplatCraftPacket consumePacket(ByteBuf var1);
	
	public abstract void execute(EntityPlayer var1);
	
	public abstract EnumSet<Side> getSenderSide();
	
	public static enum Type
	{
		WEAPON_LEFT_CLICK(PacketWeaponLeftClick.class)
		;
		
		Class<? extends SplatCraftPacket> packetType;
		
		private Type(Class<? extends SplatCraftPacket> packetClass) {
			this.packetType = packetClass;
		}
		
		SplatCraftPacket make() {
			try {
				return (SplatCraftPacket)this.packetType.newInstance();
			} catch (Exception var2) {
				var2.printStackTrace();
				return null;
			}
		}
	}
}
