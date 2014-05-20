package schmoller.tubes.network.packets;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

import schmoller.tubes.types.ManagementTube.ManagementMode;
import schmoller.tubes.network.ModBlockPacket;

public class ModPacketSetManagementMode extends ModBlockPacket
{
	public ManagementMode mode;
	
	public ModPacketSetManagementMode(int x, int y, int z, ManagementMode mode)
	{
		super(x, y, z);
		this.mode = mode;
	}
	
	public ModPacketSetManagementMode()
	{
		
	}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
	{
		super.write(output);
		output.writeByte(mode.ordinal());
	}
	
	@Override
	public void read( MCDataInput input ) throws IOException
	{
		super.read(input);
		int val = input.readByte();
		if(val >= 0 && val < ManagementMode.values().length)
			mode = ManagementMode.values()[val];
		else
			mode = ManagementMode.Stock;
	}
}
