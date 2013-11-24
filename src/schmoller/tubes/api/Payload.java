package schmoller.tubes.api;

import schmoller.tubes.api.client.IPayloadRender;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public abstract class Payload
{
	public abstract Object get();
	
	public abstract int size();
	public abstract void setSize(int size);
	
	public abstract int maxSize();
	
	public abstract void read(NBTTagCompound tag);
	public abstract void write(NBTTagCompound tag);
	
	public abstract void read(MCDataInput input);
	public abstract void write(MCDataOutput output);
	
	public static Payload load(NBTTagCompound tag)
	{
		int type = tag.getInteger("Type");
		
		Payload payload = null;
		switch(type)
		{
		case 0:
			payload = new ItemPayload();
			break;
		case 1:
			payload = new FluidPayload();
			break;
		default:
			throw new IllegalArgumentException("Unknown tube payload type " + type);
		}
		
		payload.read(tag);
		
		return payload;
	}
	
	public static Payload load(MCDataInput input)
	{
		int type = input.readByte();
		
		Payload payload = null;
		switch(type)
		{
		case 0:
			payload = new ItemPayload();
			break;
		case 1:
			payload = new FluidPayload();
			break;
		default:
			throw new IllegalArgumentException("Unknown tube payload type " + type);
		}
		
		payload.read(input);
		
		return payload;
	}
	
	public abstract Payload copy();
	
	public abstract boolean isPayloadTypeEqual(Payload other);
	public abstract boolean isPayloadEqual(Payload other);
	
	@SideOnly(Side.CLIENT)
	public abstract IPayloadRender getRenderer();
}
