package schmoller.tubes.types;

import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import schmoller.tubes.AnyFilter;
import schmoller.tubes.ItemFilter;
import schmoller.tubes.ModTubes;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.api.helpers.InventoryHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.ITubeConnectable;

public class CompressorTube extends BaseTube implements IInventory
{
	private TubeItem mCurrent;
	private IFilter mTarget;
	
	public CompressorTube()
	{
		super("compressor");
		
		mTarget = new AnyFilter(64, 64);
		mCurrent = null;
	}
	
	@Override
	public boolean canAddItem( Payload item, int direction )
	{
		if(mCurrent != null)
			return mCurrent.item.isPayloadTypeEqual(item);
		
		return true;
	}

	@Override
	public boolean hasCustomRouting()
	{
		return true;
	}
	
	@Override
	public int onDetermineDestination( TubeItem item )
	{
		if(mCurrent != null)
		{
			if(!item.item.isPayloadTypeEqual(item.item))
				return item.direction ^ 1;
			
			int amt = Math.min(item.item.size(), Math.min(mTarget.size(), mCurrent.item.maxSize()) - mCurrent.item.size());
			mCurrent.item.setSize(mCurrent.item.size() + amt);
			item.item.setSize(item.item.size() - amt);
			
			if(mCurrent.item.size() == mTarget.size() || mCurrent.item.size() == mCurrent.item.maxSize())
			{
				mCurrent.updated = true;
				addItem(mCurrent, true);
				mCurrent = null;
				
				if(item.item.size() > 0)
					mCurrent = item.clone();
			}
			
			return -2;
		}
		else if(mTarget.matches(item.item, SizeMode.LessEqual))
		{
			if(item.item.size() < mTarget.size() && item.item.maxSize() != item.item.size())
			{
				mCurrent = item.clone();
				return -2;
			}
		}
		
		int conns = getConnections();
		int count = 0;
		int dir = 0;
		
		conns -= (conns & (1 << (item.direction ^ 1)));
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				++count;
				dir = i;
			}
		}
		
		if(count > 1)
			dir = TubeHelper.findNextDirection(world(), x(), y(), z(), item);
		
		return dir;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return !(con instanceof CompressorTube);
	}
	
	public IFilter getTargetType()
	{
		return mTarget;
	}
	
	public void setTargetType(IFilter item)
	{
		if(item == null)
			mTarget = new AnyFilter(64, 64);
		else
			mTarget = item;

		if(mCurrent != null)
		{
			if( !mTarget.matches(mCurrent, SizeMode.LessEqual) || mCurrent.item.size() >= mTarget.size() || mCurrent.item.size() >= mCurrent.item.maxSize())
			{
				addItem(mCurrent, true);
				mCurrent = null;
			}
		}
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot( int i )
	{
		if(mCurrent != null && mCurrent.item instanceof ItemPayload)
			return (ItemStack)mCurrent.item.get();
		return null;
	}

	@Override
	public ItemStack decrStackSize( int slot, int count )
	{
		if(mCurrent == null || !(mCurrent.item instanceof ItemPayload))
			return null;
		
        ItemStack itemstack;

        if (mCurrent.item.size() <= count)
        {
            itemstack = (ItemStack)mCurrent.item.get();
            mCurrent = null;
        }
        else
        {
            itemstack = ((ItemStack)mCurrent.item.get()).splitStack(count);

            if (mCurrent.item.size() == 0)
            	mCurrent = null;
        }
        
        markDirty();
        return itemstack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing( int i )
	{
		if(mCurrent == null || !(mCurrent.item instanceof ItemPayload))
			return null;
		
		ItemStack item = (ItemStack)mCurrent.item.get();
		mCurrent = null;
		
		return item;
	}

	@Override
	public void setInventorySlotContents( int i, ItemStack item )
	{
		if(item == null)
		{
			mCurrent = null;
			return;
		}
		
		if(mCurrent == null)
		{
			mCurrent = new TubeItem(new ItemPayload(item));
			mCurrent.direction = 6;
			mCurrent.setProgress(0.5f);
		}
		else
			mCurrent.item = new ItemPayload(item);
		
		if(mCurrent.item.size() >= mTarget.size() || mCurrent.item.size() >= mCurrent.item.maxSize())
		{
			addItem(mCurrent, true);
			mCurrent = null;
		}
	}

	@Override
	public String getInventoryName()
	{
		return "container.inventory";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return mTarget.size();
	}

	@Override
	public void markDirty()
	{
		
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer player )
	{
		return player.getDistanceSq(x(), y(), z()) <= 25;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot( int i, ItemStack item )
	{
		if(mTarget.getType().equals("any"))
			return true;
		if(mTarget.getType().equals("item"))
			return (InventoryHelper.areItemsEqual(((ItemFilter)mTarget).getItem(), item));
		
		return false;
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		if(mCurrent != null && mCurrent.item instanceof ItemPayload)
			itemsToDrop.add((ItemStack)mCurrent.item.get());
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_COMPRESSOR_TUBE, world(), x(), y(), z());
		
		return true;
	}
	
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		NBTTagCompound target = (NBTTagCompound)root.getTag("Target");
		mTarget = FilterRegistry.getInstance().readFilter(target);
		
		if(mTarget == null)
		{
			ItemStack oldTarget = ItemStack.loadItemStackFromNBT(target);
			if(oldTarget == null)
				mTarget = new AnyFilter(64, 64);
			else
				mTarget = new ItemFilter(oldTarget, false);
		}
		
		if(root.hasKey("Current"))
		{
			NBTTagCompound current = (NBTTagCompound)root.getTag("Current");
			mCurrent = TubeItem.readFromNBT(current);
		}
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		NBTTagCompound target = new NBTTagCompound();
		FilterRegistry.getInstance().writeFilter(mTarget, target);
		root.setTag("Target", target);
		
		if(mCurrent != null)
		{
			NBTTagCompound current = new NBTTagCompound();
			mCurrent.writeToNBT(current);
			root.setTag("Current", current);
		}
	}
	
	@Override
	public void readDesc( MCDataInput input )
	{
		super.readDesc(input);
		
		if(input.readBoolean())
			mCurrent = TubeItem.read(input);
		else
			mCurrent = null;
	}
	
	@Override
	public void writeDesc( MCDataOutput output )
	{
		super.writeDesc(output);
		
		if(mCurrent != null)
		{
			output.writeBoolean(true);
			mCurrent.write(output);
		}
		else
			output.writeBoolean(false);
	}
}
