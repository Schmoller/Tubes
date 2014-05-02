package schmoller.tubes.network;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

public class ModBlockPacket extends ModPacket
{

	public int xCoord;
	public int yCoord;
	public int zCoord;
	
	public ModBlockPacket()
	{
		
	}
	
	public ModBlockPacket(int x, int y, int z)
	{
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
	{
		output.writeInt(xCoord);
		output.writeInt(yCoord);
		output.writeInt(zCoord);
	}

	@Override
	public void read( MCDataInput input ) throws IOException
	{
		xCoord = input.readInt();
		yCoord = input.readInt();
		zCoord = input.readInt();
	}

}
