package schmoller.tubes.api;

import org.apache.commons.lang3.Validate;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPayload extends Payload
{
	public ItemStack item;
	
	public ItemPayload() {}
	public ItemPayload(ItemStack item)
	{
		Validate.notNull(item);
		this.item = item;
	}
	
	@Override
	public Object get()
	{
		return item;
	}

	@Override
	public void read( NBTTagCompound tag )
	{
		item = ItemStack.loadItemStackFromNBT(tag);
	}

	@Override
	public void write( NBTTagCompound tag )
	{
		super.write(tag);
		item.writeToNBT(tag);
	}

	@Override
	public void read( MCDataInput input )
	{
		item = input.readItemStack();
	}

	@Override
	public void write( MCDataOutput output )
	{
		super.write(output);
		output.writeItemStack(item);
	}
	
	@Override
	public ItemPayload copy()
	{
		return new ItemPayload(item.copy());
	}
	
	@Override
	public boolean isPayloadEqual( Payload other )
	{
		if(!isPayloadTypeEqual(other))
			return false;
		
		return item.stackSize == ((ItemStack)other.get()).stackSize;
	}
	
	@Override
	public boolean isPayloadTypeEqual( Payload other )
	{
		if(!(other instanceof ItemPayload))
			return false;
		
		return item.isItemEqual((ItemStack)other.get()) && ItemStack.areItemStackTagsEqual(item, (ItemStack)other.get());
	}
	
	@Override
	public int size()
	{
		return item.stackSize;
	}
	
	@Override
	public void setSize( int size )
	{
		item.stackSize = size;
	}
	
	@Override
	public int maxSize()
	{
		return item.getMaxStackSize();
	}
	
	@Override
	public String toString()
	{
		return item.toString();
	}
}
