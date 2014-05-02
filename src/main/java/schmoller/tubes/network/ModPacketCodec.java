package schmoller.tubes.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;

public class ModPacketCodec extends FMLIndexedMessageToMessageCodec<ModPacket>
{
	@Override
	public void decodeInto( ChannelHandlerContext context, ByteBuf buffer, ModPacket dest )
	{
		try
		{
			dest.read(new MCDInputBridge(buffer));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void encodeInto( ChannelHandlerContext context, ModPacket packet, ByteBuf buffer ) throws Exception
	{
		packet.write(new MCDOutputBridge(buffer));
	}

}
