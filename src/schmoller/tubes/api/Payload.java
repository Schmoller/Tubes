package schmoller.tubes.api;

import schmoller.tubes.api.PayloadRegistry.PayloadType;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.nbt.NBTTagCompound;

public abstract class Payload
{
	public abstract Object get();
	
	public abstract int size();
	public abstract void setSize(int size);
	
	public abstract int maxSize();
	
	public abstract void read(NBTTagCompound tag);
	public void write(NBTTagCompound tag)
	{
		tag.setString("Type", PayloadRegistry.instance().getPayload(getClass()).type);
	}
	
	public abstract void read(MCDataInput input);
	public void write(MCDataOutput output)
	{
		output.writeByte(PayloadRegistry.instance().getPayload(getClass()).index);
	}
	
	
	public static Payload load(NBTTagCompound tag)
	{
		String typeName = tag.getString("Type");
		
		Payload payload = null;
		PayloadType type = PayloadRegistry.instance().getPayload(typeName);
		if(type == null)
			throw new IllegalArgumentException("Unknown tube payload type " + typeName);
		
		payload = type.newInstance();
		payload.read(tag);
		
		return payload;
	}
	
	public static Payload load(MCDataInput input)
	{
		short typeId = input.readByte();
		
		Payload payload = null;
		PayloadType type = PayloadRegistry.instance().getPayload(typeId);
		if(type == null)
			throw new IllegalArgumentException("Unknown tube payload type " + typeId);
		
		payload = type.newInstance();
		payload.read(input);
		
		return payload;
	}
	
	public abstract Payload copy();
	
	public abstract boolean isPayloadTypeEqual(Payload other);
	public abstract boolean isPayloadEqual(Payload other);
}
