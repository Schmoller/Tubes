package schmoller.tubes.network.packets;

import java.util.EnumSet;

import schmoller.tubes.network.ModBlockPacket;

public class ModPacketSetProperty extends ModBlockPacket
{
	public int prop;
	public int value;
	
	public ModPacketSetProperty(int x, int y, int z)
	{
		super(x, y, z);
	}
	
	public <T extends Enum> ModPacketSetProperty setProperty(int property, T value)
	{
		prop = property;
		this.value = value.ordinal();
		return this;
	}
	
	public <T extends Enum> T getValueEnum(Class<T> enumClass, T def)
	{
		EnumSet<? extends Enum> set = EnumSet.allOf(enumClass);
		
		for(Enum e : set)
		{
			if(e.ordinal() == value)
				return (T)e;
		}
		
		return def;
	}
}
