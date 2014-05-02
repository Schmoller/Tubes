package schmoller.tubes.network.packets;

import java.io.IOException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

import schmoller.tubes.types.RoutingTube.RouteDirection;
import schmoller.tubes.network.ModBlockPacket;

public class ModPacketSetRoutingOptions extends ModBlockPacket
{
	public int colour;
	public RouteDirection direction;
	public boolean hasColour;
	public int column;
	
	public ModPacketSetRoutingOptions()
	{
		
	}
	
	public ModPacketSetRoutingOptions(int x, int y, int z, int column, int colour)
	{
		super(x, y, z);
		this.column = column;
		this.colour = colour;
		hasColour = true;
	}
	
	public ModPacketSetRoutingOptions(int x, int y, int z, int column, RouteDirection dir)
	{
		super(x, y, z);
		this.column = column;
		direction = dir;
		hasColour = false;
	}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
	{
		super.write(output);
		
		output.writeByte(column);
		output.writeBoolean(hasColour);
		
		if(hasColour)
			output.writeShort(colour);
		else
			output.writeByte(direction.ordinal());
	}
	
	@Override
	public void read( MCDataInput input ) throws IOException
	{
		super.read(input);
		
		column = input.readByte();
		hasColour = input.readBoolean();
		if(hasColour)
			colour = input.readShort();
		else
			direction = RouteDirection.from(input.readByte());
	}
}
