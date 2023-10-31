package net.splatcraft.forge.network.s2c;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.client.layer.PlayerInkColoredSkinLayer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

public class ReceivePlayerOverlayPacket extends PlayS2CPacket
{
	final UUID player;
	final byte[] imageBytes;

	public ReceivePlayerOverlayPacket(UUID player, byte[] imageBytes)
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

	public static ReceivePlayerOverlayPacket decode(FriendlyByteBuf buffer)
	{
		return new ReceivePlayerOverlayPacket(buffer.readUUID(), buffer.readByteArray());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void execute()
	{
		try {

			ResourceLocation location = new ResourceLocation(Splatcraft.MODID, PlayerInkColoredSkinLayer.PATH + player.toString());
			Minecraft.getInstance().getTextureManager().release(location);

			if(imageBytes.length > 0)
			{
				DynamicTexture texture = new DynamicTexture(NativeImage.read(new ByteArrayInputStream(imageBytes)));
				Minecraft.getInstance().getTextureManager().register(location, texture);
				PlayerInkColoredSkinLayer.TEXTURES.put(player, location);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
