package schmoller.tubes.network.packets;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

import schmoller.tubes.network.ModBlockPacket;

public class ModPacketSetColor extends ModBlockPacket
{
	public int color;
	
	public ModPacketSetColor(int x, int y, int z, int color)
	{
		super(x, y, z);
		this.color = color;
	}
	
	public ModPacketSetColor() {}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
	{
		super.write(output);
		output.writeShort(color);
	}
	
	@Override
	public void read( MCDataInput input ) throws IOException
	{
		super.read(input);
		color = input.readShort();
		
		if(color < -1 || color > 15)
			throw new IOException("Invalid color " + color);
	}
}
