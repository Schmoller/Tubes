package schmoller.tubes.network.packets;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

import schmoller.tubes.network.ModBlockPacket;

public class ModPacketSetPriority extends ModBlockPacket
{
	public int priority;
	
	public ModPacketSetPriority(int x, int y, int z, int priority)
	{
		super(x, y, z);
		this.priority = priority;
	}
	
	public ModPacketSetPriority() {}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
	{
		super.write(output);
		output.writeByte(priority);
	}
	
	@Override
	public void read( MCDataInput input ) throws IOException
	{
		super.read(input);
		priority = input.readByte();
		
		if(priority < 0 || priority >= 10)
			throw new IOException("Invalid priority " + priority);
	}
}
