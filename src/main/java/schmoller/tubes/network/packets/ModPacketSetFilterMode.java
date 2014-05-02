package schmoller.tubes.network.packets;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

import schmoller.tubes.types.FilterTube.Comparison;
import schmoller.tubes.types.FilterTube.Mode;
import schmoller.tubes.network.ModBlockPacket;

public class ModPacketSetFilterMode extends ModBlockPacket
{
	public Mode mode;
	public Comparison comparison;
	
	public ModPacketSetFilterMode(int x, int y, int z, Mode mode)
	{
		super(x, y, z);
		this.mode = mode;
	}
	
	public ModPacketSetFilterMode(int x, int y, int z, Comparison comp)
	{
		super(x, y, z);
		this.comparison = comp;
	}
	
	public ModPacketSetFilterMode()
	{
		
	}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
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
			output.writeByte(comparison.ordinal());
		}
	}
	
	@Override
	public void read( MCDataInput input ) throws IOException
	{
		super.read(input);
		if(input.readBoolean())
		{
			int val = input.readByte();
			if(val >= 0 && val < Mode.values().length)
				mode = Mode.values()[val];
			else
				mode = Mode.Allow;
		}
		else
		{
			int val = input.readByte();
			if(val >= 0 && val < Comparison.values().length)
				comparison = Comparison.values()[val];
			else
				comparison = Comparison.Any;
		}
	}
}
