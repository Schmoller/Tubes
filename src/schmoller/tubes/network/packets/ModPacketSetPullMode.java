package schmoller.tubes.network.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import schmoller.tubes.PullMode;
import schmoller.tubes.network.ModBlockPacket;

public class ModPacketSetPullMode extends ModBlockPacket
{
	public PullMode mode;
	
	public ModPacketSetPullMode(int x, int y, int z, PullMode mode)
	{
		super(x, y, z);
		this.mode = mode;
	}
	
	public ModPacketSetPullMode()
	{
		
	}
	
	@Override
	public void write( DataOutput output ) throws IOException
	{
		super.write(output);
		output.writeByte(mode.ordinal());
	}
	
	@Override
	public void read( DataInput input ) throws IOException
	{
		super.read(input);
		int val = input.readByte();
		if(val >= 0 && val < PullMode.values().length)
			mode = PullMode.values()[val];
		else
			mode = PullMode.RedstoneConstant;
	}
}
