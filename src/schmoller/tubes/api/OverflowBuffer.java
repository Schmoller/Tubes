package schmoller.tubes.api;

import java.util.LinkedList;
import java.util.List;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class OverflowBuffer
{
	private LinkedList<TubeItem> mBuffer;
	
	public OverflowBuffer()
	{
		mBuffer = new LinkedList<TubeItem>();
	}
	
	public boolean isEmpty()
	{
		return mBuffer.isEmpty();
	}
	
	public void addItem(TubeItem item)
	{
		mBuffer.add(item);
	}
	
	public TubeItem getNext()
	{
		return mBuffer.removeFirst();
	}
	
	public TubeItem peekNext()
	{
		return mBuffer.getFirst();
	}
	
	public void save(NBTTagCompound root)
	{
		NBTTagList list = new NBTTagList();
		for(TubeItem item : mBuffer)
		{
			NBTTagCompound tag = new NBTTagCompound();
			item.writeToNBT(tag);
			list.appendTag(tag);
		}
		
		root.setTag("Overflow", list);
	}
	
	public void load(NBTTagCompound root)
	{
		NBTTagList list = root.getTagList("Overflow");
		mBuffer.clear();
		
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)list.tagAt(i);
			mBuffer.add(TubeItem.readFromNBT(tag));
		}
	}

	public void onDropItems( List<ItemStack> itemsToDrop )
	{
		for(TubeItem item : mBuffer)
			itemsToDrop.add(item.item);
	}
}
