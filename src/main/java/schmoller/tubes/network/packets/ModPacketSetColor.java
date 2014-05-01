package schmoller.tubes.network.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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
	public void write( DataOutput output ) throws IOException
	{
		super.write(output);
		output.writeShort(color);
	}
	
	@Override
	public void read( DataInput input ) throws IOException
	{
		super.read(input);
		color = input.readShort();
	}
}
