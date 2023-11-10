package net.splatcraft.forge.network.c2s;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.handlers.SplatcraftCommonHandler;
import net.splatcraft.forge.network.SplatcraftPacketHandler;
import net.splatcraft.forge.network.s2c.ReceivePlayerOverlayPacket;

public class SendPlayerOverlayPacket extends PlayC2SPacket
{
	final UUID player;
	final byte[] imageBytes;

	@OnlyIn(Dist.CLIENT)
	public SendPlayerOverlayPacket(UUID player, File file) throws IOException
	{
		this.player = player;
		imageBytes = NativeImage.read(new FileInputStream(file)).asByteArray();
	}

	public SendPlayerOverlayPacket(UUID player, byte[] imageBytes)
	{
		this.imageBytes = imageBytes;
		this.player = player;
	}

	@Override
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeUUID(player);
		buffer.writeByteArray(imageBytes);
	}

	public static SendPlayerOverlayPacket decode(FriendlyByteBuf buffer)
	{
		return new SendPlayerOverlayPacket(buffer.readUUID(), buffer.readByteArray());
	}

	@Override
	public void execute(Player player)
	{
		if(imageBytes.length > 0)
			SplatcraftCommonHandler.COLOR_SKIN_OVERLAY_SERVER_CACHE.put(this.player, imageBytes);
		else SplatcraftCommonHandler.COLOR_SKIN_OVERLAY_SERVER_CACHE.remove(this.player);
		SplatcraftPacketHandler.sendToAll(new ReceivePlayerOverlayPacket(this.player, imageBytes));
	}
}
