package schmoller.tubes.network.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import schmoller.tubes.PullMode;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.network.ModBlockPacket;

public class ModPacketSetRequestingModes extends ModBlockPacket
{
	public PullMode mode;
	public SizeMode sizeMode;
	
	public ModPacketSetRequestingModes(int x, int y, int z, PullMode mode)
	{
		super(x, y, z);
		this.mode = mode;
	}
	
	public ModPacketSetRequestingModes(int x, int y, int z, SizeMode mode)
	{
		super(x, y, z);
		sizeMode = mode;
	}
	
	public ModPacketSetRequestingModes()
	{
		
	}
	
	@Override
	public void write( DataOutput output ) throws IOException
	{
		super.write(output);
		if(mode != null)
		{
			output.writeBoolean(true);
			output.writeByte(mode.ordinal());
		}
		else
		{
			output.writeBoolean(false);
			output.writeByte(sizeMode.ordinal());
		}
		
	}
	
	@Override
	public void read( DataInput input ) throws IOException
	{
		super.read(input);
		
		if(input.readBoolean())
		{
			int val = input.readByte();
			if(val >= 0 && val < PullMode.values().length)
				mode = PullMode.values()[val];
			else
				mode = PullMode.RedstoneConstant;
		}
		else
		{
			int val = input.readByte();
			if(val >= 0 && val < SizeMode.values().length)
				sizeMode = SizeMode.values()[val];
			else
				sizeMode = SizeMode.Max;
		}
	}
}
