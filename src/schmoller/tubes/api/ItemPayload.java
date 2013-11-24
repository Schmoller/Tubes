package schmoller.tubes.api;

import schmoller.tubes.api.client.IPayloadRender;
import schmoller.tubes.render.ItemPayloadRender;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPayload extends Payload
{
	@SideOnly(Side.CLIENT)
	private static ItemPayloadRender mRender;
	
	public ItemStack item;
	
	public ItemPayload() {}
	public ItemPayload(ItemStack item)
	{
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
		tag.setInteger("Type", 0);
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
		output.writeByte(0);
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
	
	@Override
	@SideOnly( Side.CLIENT )
	public IPayloadRender getRenderer()
	{
		if(mRender == null)
			mRender = new ItemPayloadRender();
		
		return mRender;
	}
}
