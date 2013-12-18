package schmoller.tubes.types;

import java.util.List;

import schmoller.tubes.ModTubes;
import schmoller.tubes.api.ItemPayload;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.routing.OutputRouter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;

public class InjectionTube extends BaseTube implements IInventory, ITubeOverflowDestination
{
	private OverflowBuffer mOverflow;
	private ItemStack mItem;
	
	public InjectionTube()
	{
		super("injection");
		mOverflow = new OverflowBuffer();
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot( int i )
	{
		return mItem;
	}

	@Override
	public ItemStack decrStackSize( int i, int amount )
	{
		if(mItem == null)
			return null;
		
		if(mItem.stackSize <= amount)
		{
			ItemStack item = mItem;
			mItem = null;
			return item;
		}
		else
			return mItem.splitStack(amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing( int i )
	{
		return mItem;
	}

	@Override
	public void setInventorySlotContents( int i, ItemStack itemstack )
	{
		mItem = itemstack;
	}

	@Override
	public String getInvName()
	{
		return "container.inventory";
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
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
	public boolean isUseableByPlayer( EntityPlayer player )
	{
		return (player.getDistanceSq(x(), y(), z()) <= 25);
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot( int i, ItemStack itemstack )
	{
		return true;
	}

	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return (!(con instanceof InjectionTube));
	}
	
	@Override
	public boolean canConnectToInventories()
	{
		return false;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		return item.state == TubeItem.BLOCKED;
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		player.openGui(ModTubes.instance, ModTubes.GUI_INJECTION_TUBE, world(), x(), y(), z());
		return true;
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.state == TubeItem.BLOCKED)
		{
			if(!world().isRemote)
				mOverflow.addItem(item);
			
			return false;
		}
		
		return super.onItemJunction(item);
	}
	
	@Override
	public int getTickRate()
	{
		return 10;
	}
	
	@Override
	public void onTick()
	{
		if(world().isRemote)
			return;
		
		if(!mOverflow.isEmpty())
		{
			TubeItem item = mOverflow.peekNext();
			PathLocation loc = new OutputRouter(world(), new Position(x(),y(),z()), item).route();
			
			if(loc != null)
			{
				mOverflow.getNext();
				item.state = TubeItem.NORMAL;
				item.direction = item.lastDirection = 6;
				item.updated = false;
				item.setProgress(0.5f);
				addItem(item, false);
			}
		}
		else if(mItem != null)
		{
			if(getNumConnections() > 0)
			{
				addItem(new ItemPayload(mItem), -1);
				mItem = null;
			}
		}
	}
	
	@Override
	public boolean canAcceptOverflowFromSide( int side )
	{
		return true;
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		mOverflow.onDropItems(itemsToDrop);
		if(mItem != null)
			itemsToDrop.add(mItem);
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		if(mItem != null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			mItem.writeToNBT(tag);
			root.setTag("item", tag);
		}
		
		mOverflow.save(root);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		if(root.hasKey("item"))
			mItem = ItemStack.loadItemStackFromNBT(root.getCompoundTag("item"));
			
		mOverflow.load(root);
	}
}
