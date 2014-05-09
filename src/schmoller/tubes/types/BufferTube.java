package schmoller.tubes.types;

import java.util.List;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.inventory.BasicInvHandler;
import schmoller.tubes.routing.OutputRouter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;

public class BufferTube extends DirectionalTube implements ISidedInventory, ITubeOverflowDestination
{
	private OverflowBuffer mOverflow = new OverflowBuffer();
	private ItemStack[] mSlots = new ItemStack[9];
	
	public BufferTube()
	{
		super("buffer");
	}
	
	@Override
	public int getSizeInventory()
	{
		return 9;
	}

	@Override
	public ItemStack getStackInSlot( int i )
	{
		return mSlots[i];
	}

	@Override
	public ItemStack decrStackSize( int i, int amount )
	{
		if(mSlots[i] == null)
			return null;
		
		if(mSlots[i].stackSize <= amount)
		{
			ItemStack item = mSlots[i];
			mSlots[i] = null;
			return item;
		}
		else
			return mSlots[i].splitStack(amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing( int i )
	{
		return mSlots[i];
	}

	@Override
	public void setInventorySlotContents( int i, ItemStack itemstack )
	{
		mSlots[i] = itemstack;
	}

	@Override
	public String getInvName()
	{
		return "Buffer Tube";
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return true;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void onInventoryChanged()
	{
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer entityplayer )
	{
		return entityplayer.getDistanceSq(x(), y(), z()) <= 25;
	}

	@Override
	public void openChest()	{}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot( int i, ItemStack itemstack )	{ return true; }

	@Override
	public boolean canExtractItem( int var1, ItemStack var2, int var3 )
	{
		return true;
	}
	
	@Override
	public boolean canInsertItem( int var1, ItemStack var2, int var3 )
	{
		return true;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide( int side )
	{
		return new int[] {0,1,2,3,4,5,6,7,8,9};
	}
	
	@Override
	protected int getConnectableSides()
	{
		return 63;
	}
	
	@Override
	public int getTickRate() { return mOverflow.isEmpty() ? 20 : 10; }
	
	@Override
	public void onTick()
	{
		if(world().isRemote)
			return;
		
		if(!mOverflow.isEmpty())
		{
			TubeItem item = mOverflow.peekNext();
			PathLocation loc = new OutputRouter(world(), new Position(x(),y(),z()), item, getFacing()).route();
			
			if(loc != null)
			{
				mOverflow.getNext();
				item.state = TubeItem.NORMAL;
				item.direction = item.lastDirection = getFacing();
				item.updated = true;
				item.setProgress(0.5f);
				addItem(item, true);
			}
			
			return;
		}
		
		for(int i = 0; i < mSlots.length; ++i)
		{
			if(mSlots[i] == null)
				continue;
			
			TubeItem item = new TubeItem(new ItemPayload(mSlots[i]));
			item.direction = item.lastDirection = getFacing();
			item.state = TubeItem.NORMAL;
			item.updated = true;
			item.setProgress(0.5f);
			
			addItem(item, true);

			mSlots[i] = null;
			break;
		}
	}

	@Override
	public boolean canPathThrough() { return false; }
	
	@Override
	public boolean canAcceptOverflowFromSide( int side )
	{
		return side == (getFacing() ^ 1);
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		if(item.direction == (getFacing() ^ 1))
			return (item.state == TubeItem.BLOCKED);
		
		return super.canItemEnter(item);
	}
	
	@Override
	public boolean canAddItem( Payload item, int direction )
	{
		if(direction == (getFacing() ^ 1))
			return false;
		
		if(!(item instanceof ItemPayload))
			return false;
		
		if(world().isRemote)
			return true;
		
		BasicInvHandler handler = new BasicInvHandler(this);
		
		ItemPayload result = handler.insert((ItemPayload)item, direction ^ 1, false);
		
		return result == null || result.size() != item.size(); 
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.direction == (getFacing() ^ 1))
		{
			if(!world().isRemote)
				mOverflow.addItem(item);
		}
		else
		{
			BasicInvHandler handler = new BasicInvHandler(this);
			ItemPayload left = handler.insert((ItemPayload)item.item, item.direction ^ 1, true);
			
			if(left != null)
			{
				item.item.setSize(left.size());
				item.direction = item.direction ^ 1;
				item.state = TubeItem.BLOCKED;
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(!super.activate(player, part, item))
		{
			player.openGui(ModTubes.instance, ModTubes.GUI_BUFFER_TUBE, world(), x(), y(), z());
			return true;
		}
		
		return true;
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		mOverflow.save(root);
		
		NBTTagList items = new NBTTagList();
		for(int i = 0; i < 9; ++i)
		{
			if(mSlots[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				mSlots[i].writeToNBT(tag);
				tag.setInteger("Slot", i);
				items.appendTag(tag);
			}
		}
		root.setTag("Slots", items);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		mOverflow.load(root);
		
		NBTTagList items = root.getTagList("Slots");
		for(int i = 0; i < items.tagCount(); ++i)
		{
			NBTTagCompound tag = (NBTTagCompound)items.tagAt(i);
			int slot = tag.getInteger("Slot");
			
			mSlots[slot] = ItemStack.loadItemStackFromNBT(tag);
		}
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		mOverflow.onDropItems(itemsToDrop);
		for(int i = 0; i < mSlots.length; ++i)
		{
			if(mSlots[i] != null)
				itemsToDrop.add(mSlots[i]);
		}
	}
}
