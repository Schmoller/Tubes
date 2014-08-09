package schmoller.tubes.network.packets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import schmoller.tubes.network.ModPacket;

public class ModPacketGoalIds extends ModPacket
{
	public Map<String, Integer> ids;
	
	public ModPacketGoalIds(Map<String, Integer> ids)
	{
		this.ids = ids;
	}
	
	public ModPacketGoalIds() {}
	
	@Override
	public void write( MCDataOutput output ) throws IOException
	{
		output.writeByte(ids.size());
		for(Entry<String, Integer> entry : ids.entrySet())
		{
			output.writeString(entry.getKey());
			output.writeShort(entry.getValue());
		}
	}

	@Override
	public void read( MCDataInput input ) throws IOException
	{
		int size = input.readUByte();
		ids = new HashMap<String, Integer>(size);
		
		for(int i = 0; i < size; ++i)
			ids.put(input.readString(), input.readUShort());
	}

}
