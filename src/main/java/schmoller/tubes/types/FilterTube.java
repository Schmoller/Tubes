package schmoller.tubes.types;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.Constants;
import schmoller.tubes.ItemFilter;
import schmoller.tubes.ModTubes;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPropertyHolder;
import schmoller.tubes.api.interfaces.ITubeConnectable;

public class FilterTube extends BaseTube implements IPropertyHolder
{
	public static final int PROP_MODE = 1;
	public static final int PROP_COMPARISON = 2;
	public static final int PROP_COLOR = 3;
	
	private IFilter[] mFilterStacks;
	private Mode mCurrentMode = Mode.Allow;
	private Comparison mCurrentComparison = Comparison.Any;
	private int mColor = -1;
	
	public FilterTube()
	{
		super("filter");
		mFilterStacks = new IFilter[16];
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_FILTER_TUBE, world(), x(), y(), z());
		
		return true;
	}
	
	public void setFilter(int index, IFilter filter)
	{
		mFilterStacks[index] = filter;
		tile().markDirty();
	}
	
	public IFilter getFilter(int index)
	{
		return mFilterStacks[index];
	}
	
	@Override
	public <T> T getProperty( int prop )
	{
		switch(prop)
		{
		case PROP_MODE:
			return (T)mCurrentMode;
		case PROP_COMPARISON:
			return (T)mCurrentComparison;
		case PROP_COLOR:
			return (T)Integer.valueOf(mColor);
		}
		
		return null;
	}
	
	@Override
	public <T> void setProperty( int prop, T value )
	{
		switch(prop)
		{
		case PROP_MODE:
			mCurrentMode = (Mode)value;
			break;
		case PROP_COMPARISON:
			mCurrentComparison = (Comparison)value;
			break;
		case PROP_COLOR:
			mColor = ((Number)value).intValue();
			break;
		}
		
		tile().markDirty();
	}
	
	private boolean doesMatchFilter(Payload item, int index)
	{
		if(mFilterStacks[index] == null || !mFilterStacks[index].matches(item, SizeMode.Max))
			return mCurrentMode == Mode.Deny;
		
		boolean matches = false;
		
		switch(mCurrentComparison)
		{
		case Any:
			matches = true;
			break;
		case Exact:
			matches = mFilterStacks[index].matches(item, SizeMode.Exact);
			break;
		case Greater:
			matches = !mFilterStacks[index].matches(item, SizeMode.LessEqual);
			break;
		case Less:
			matches = !mFilterStacks[index].matches(item, SizeMode.GreaterEqual);
			break;
		}
		
		if(mCurrentMode == Mode.Deny)
			matches = !matches;
		
		return matches;
	}
	
	private boolean doesMatchFilter(TubeItem item, int index)
	{
		if(mFilterStacks[index] == null || !mFilterStacks[index].matches(item, SizeMode.Max))
			return mCurrentMode == Mode.Deny;
		
		boolean matches = false;
		
		switch(mCurrentComparison)
		{
		case Any:
			matches = true;
			break;
		case Exact:
			matches = mFilterStacks[index].matches(item, SizeMode.Exact);
			break;
		case Greater:
			matches = !mFilterStacks[index].matches(item, SizeMode.LessEqual);
			break;
		case Less:
			matches = !mFilterStacks[index].matches(item, SizeMode.GreaterEqual);
			break;
		}
		
		if(mCurrentMode == Mode.Deny)
			matches = !matches;
		
		return matches;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
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
	public boolean canAddItem( Payload item, int direction )
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
	public void simulateEffects( TubeItem item )
	{
		item.colour = mColor;
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
				FilterRegistry.getInstance().writeFilter(mFilterStacks[i],tag);
				items.appendTag(tag);
			}
		}
		
		root.setTag("NewFilter", items);
		
		root.setInteger("mode", mCurrentMode.ordinal());
		root.setInteger("comp", mCurrentComparison.ordinal());
		
		root.setShort("Color", (short)mColor);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		if(root.hasKey("filter"))
		{
			NBTTagList items = root.getTagList("filter", Constants.NBT.TAG_COMPOUND);
			
			for(int i = 0; i < items.tagCount(); ++i)
			{
				NBTTagCompound tag = items.getCompoundTagAt(i);
				int slot = tag.getInteger("Slot");
				mFilterStacks[slot] = new ItemFilter(ItemStack.loadItemStackFromNBT(tag), false);
			}
		}
		else
		{
			NBTTagList filters = root.getTagList("NewFilter", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < filters.tagCount(); ++i)
			{
				NBTTagCompound tag = filters.getCompoundTagAt(i);
				int slot = tag.getInteger("Slot");
				mFilterStacks[slot] = FilterRegistry.getInstance().readFilter(tag);
			}
			
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
				FilterRegistry.getInstance().writeFilter(mFilterStacks[i],output);
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
				mFilterStacks[i] = FilterRegistry.getInstance().readFilter(input);
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
