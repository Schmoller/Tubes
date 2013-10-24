package schmoller.tubes.types;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.ModTubes;
import schmoller.tubes.TubeItem;

public class FilterTube extends BaseTube
{
	private ItemStack[] mFilterStacks;
	private Mode mCurrentMode = Mode.Allow;
	private Comparison mCurrentComparison = Comparison.Any;
	private int mColor = -1;
	
	public FilterTube()
	{
		super("filter");
		mFilterStacks = new ItemStack[16];
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_FILTER_TUBE, world(), x(), y(), z());
		
		return true;
	}
	
	public void setFilter(int index, ItemStack item)
	{
		mFilterStacks[index] = item;
	}
	
	public ItemStack getFilter(int index)
	{
		return mFilterStacks[index];
	}
	
	public void setMode(Mode mode)
	{
		mCurrentMode = mode;
	}
	
	public Mode getMode()
	{
		return mCurrentMode;
	}
	
	public void setComparison(Comparison comp)
	{
		mCurrentComparison = comp;
	}
	
	public Comparison getComparison()
	{
		return mCurrentComparison;
	}
	
	public void setColour(short colour)
	{
		mColor = colour;
	}
	
	public int getColour()
	{
		return mColor;
	}

	private boolean doesMatchFilter(ItemStack item, int index)
	{
		int matchAmount = 0;
		if(mFilterStacks[index] != null && (mFilterStacks[index].isItemEqual(item) && ItemStack.areItemStackTagsEqual(mFilterStacks[index], item)))
		{
			matchAmount = mFilterStacks[index].stackSize;
		}
		else
			return mCurrentMode == Mode.Deny;
		
		if(matchAmount == 0 && mCurrentMode == Mode.Allow)
			return false;
		
		boolean matches = false;
		
		switch(mCurrentComparison)
		{
		case Any:
			matches = true;
			break;
		case Exact:
			matches = (matchAmount == item.stackSize);
			break;
		case Greater:
			matches = ( item.stackSize > matchAmount);
			break;
		case Less:
			matches = (item.stackSize < matchAmount);
			break;
		}
		
		if(mCurrentMode == Mode.Deny)
			matches = !matches;
		
		return matches;
	}
	
	@Override
	public boolean canAddItem( ItemStack item, int direction )
	{
		boolean empty = true;

		for(int i = 0; i < mFilterStacks.length; ++i)
		{
			if(mFilterStacks[i] != null)
			{
				empty = false;
				
				if(mCurrentMode == Mode.Allow)
				{
					if(doesMatchFilter(item, i))
						return true;
				}
				else
				{
					if(!doesMatchFilter(item, i))
						return false;
				}
			}
		}
		
		if(empty)
			return true;
		
		return mCurrentMode == Mode.Deny;
	}
	
	@Override
	public boolean simulateEffects( TubeItem item )
	{
		item.colour = mColor;
		return true;
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		item.colour = mColor;
		
		return super.onItemJunction(item);
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return !(con instanceof FilterTube);
	}
	
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		NBTTagList items = new NBTTagList();
		for(int i = 0; i < mFilterStacks.length; ++i)
		{
			if(mFilterStacks[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				mFilterStacks[i].writeToNBT(tag);
				items.appendTag(tag);
			}
		}
		
		root.setTag("filter", items);
		
		root.setInteger("mode", mCurrentMode.ordinal());
		root.setInteger("comp", mCurrentComparison.ordinal());
		
		root.setShort("Color", (short)mColor);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		NBTTagList items = root.getTagList("filter");
		
		for(int i = 0; i < items.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)items.tagAt(i);
			int slot = tag.getInteger("Slot");
			mFilterStacks[slot] = ItemStack.loadItemStackFromNBT(tag);
		}
		
		mCurrentMode = Mode.values()[root.getInteger("mode")];
		mCurrentComparison = Comparison.values()[root.getInteger("comp")];
		
		mColor = root.getShort("Color");
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		super.writeDesc(output);
		
		output.writeByte(mCurrentMode.ordinal());
		output.writeByte(mCurrentComparison.ordinal());
		
		for(int i = 0; i < mFilterStacks.length; ++i)
		{
			if(mFilterStacks[i] != null)
			{
				output.writeBoolean(true);
				output.writeItemStack(mFilterStacks[i]);
			}
			else
				output.writeBoolean(false);
		}
		
		output.writeShort(mColor);
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		super.readDesc(input);
		
		mCurrentMode = Mode.values()[input.readByte()];
		mCurrentComparison = Comparison.values()[input.readByte()];
		
		for(int i = 0; i < mFilterStacks.length; ++i)
		{
			if(input.readBoolean())
				mFilterStacks[i] = input.readItemStack();
		}
		
		mColor = input.readShort();
	}

	public enum Mode
	{
		Allow,
		Deny
	}
	
	public enum Comparison
	{
		Any,
		Exact,
		Less,
		Greater
	}
}
