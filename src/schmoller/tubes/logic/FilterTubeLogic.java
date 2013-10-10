package schmoller.tubes.logic;

import codechicken.core.data.MCDataInput;
import codechicken.core.data.MCDataOutput;
import schmoller.tubes.ITube;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.ModTubes;
import schmoller.tubes.TubeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class FilterTubeLogic extends TubeLogic
{
	private ItemStack[] mFilterStacks;
	private Mode mCurrentMode = Mode.Allow;
	private Comparison mCurrentComparison = Comparison.Any;
	
	public FilterTubeLogic(ITube tube)
	{
		super(tube);
		mFilterStacks = new ItemStack[16];
	}
	
	@Override
	public boolean onActivate( EntityPlayer player )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_FILTER_TUBE, mTube.world(), mTube.x(), mTube.y(), mTube.z());
		
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
	public boolean canItemEnter( TubeItem item, int side )
	{
		boolean empty = true;

		for(int i = 0; i < mFilterStacks.length; ++i)
		{
			if(mFilterStacks[i] != null)
			{
				empty = false;
				
				if(mCurrentMode == Mode.Allow)
				{
					if(doesMatchFilter(item.item, i))
						return true;
				}
				else
				{
					if(!doesMatchFilter(item.item, i))
						return false;
				}
			}
		}
		
		if(empty)
			return true;
		
		return mCurrentMode == Mode.Deny;
	}

	@Override
	public void onSave( NBTTagCompound root )
	{
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
	}
	
	@Override
	public void onLoad( NBTTagCompound root )
	{
		NBTTagList items = root.getTagList("filter");
		
		for(int i = 0; i < items.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)items.tagAt(i);
			int slot = tag.getInteger("Slot");
			mFilterStacks[slot] = ItemStack.loadItemStackFromNBT(tag);
		}
		
		mCurrentMode = Mode.values()[root.getInteger("mode")];
		mCurrentComparison = Comparison.values()[root.getInteger("comp")];
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
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
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		mCurrentMode = Mode.values()[input.readByte()];
		mCurrentComparison = Comparison.values()[input.readByte()];
		
		for(int i = 0; i < mFilterStacks.length; ++i)
		{
			if(input.readBoolean())
				mFilterStacks[i] = input.readItemStack();
		}
	}
	
	public ITube getTube()
	{
		return mTube;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		if(con instanceof ITube && ((ITube)con).getLogic() instanceof FilterTubeLogic)
			return false;
		
		return true;
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
