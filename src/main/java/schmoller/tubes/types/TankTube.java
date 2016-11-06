package schmoller.tubes.types;

import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.routing.OutputRouter;

public class TankTube extends BaseTube implements IFluidHandler, ITubeOverflowDestination
{
	private FluidTank mTank = new FluidTank(1000);
	private OverflowBuffer mOverflow;
	
	public static final int CHANNEL_FLUID = 10;
	
	private long mLastUpdate = 0;
	private boolean mHasUpdated = true;
	
	public TankTube()
	{
		super("tank");
		mOverflow = new OverflowBuffer();
	}

	@Override
	public boolean canAcceptOverflowFromSide( int side )
	{
		return true;
	}
	
	public FluidStack getFluid()
	{
		return mTank.getFluid();
	}

	@Override
	public int fill( ForgeDirection from, FluidStack resource, boolean doFill )
	{
		int amount = mTank.fill(resource, doFill);
		if(amount > 0 && doFill)
			onFluidChange();
		
		return amount;
	}

	@Override
	public FluidStack drain( ForgeDirection from, FluidStack resource, boolean doDrain )
	{
		if(!resource.isFluidEqual(mTank.getFluid()))
			return null;
		
		FluidStack drained = mTank.drain(resource.amount, doDrain);
		
		if(drained != null && doDrain)
			onFluidChange();
		
		return drained;
	}

	@Override
	public FluidStack drain( ForgeDirection from, int maxDrain, boolean doDrain )
	{
		FluidStack drained = mTank.drain(maxDrain, doDrain);
		
		if(drained != null && doDrain)
			onFluidChange();
		
		return drained;
	}

	@Override
	public boolean canFill( ForgeDirection from, Fluid fluid )
	{
		return true;
	}

	@Override
	public boolean canDrain( ForgeDirection from, Fluid fluid )
	{
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo( ForgeDirection from )
	{
		return new FluidTankInfo[] {mTank.getInfo()};
	}

	@Override
	public boolean canConnectToInventories() 
	{
		return false;
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		return !(con instanceof TankTube);
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(item == null)
			return false;
		
		if(FluidContainerRegistry.isEmptyContainer(item))
		{
			if(player.worldObj.isRemote)
				return true;
			
			if(mTank.getFluidAmount() == 0)
				return true;
			
			ItemStack filled = FluidContainerRegistry.fillFluidContainer(mTank.getFluid(), item);
			if(filled != null)
			{
				FluidStack drained = FluidContainerRegistry.getFluidForFilledItem(filled);
				mTank.drain(drained.amount, true);
				onFluidChange();
				if (!player.capabilities.isCreativeMode)
				{
					if (player.inventory.mainInventory[player.inventory.currentItem].stackSize > 1)
					{
						player.inventory.setInventorySlotContents(player.inventory.getFirstEmptyStack(),filled);
						player.inventory.decrStackSize(player.inventory.currentItem,1);
					}
					else
						player.inventory.mainInventory[player.inventory.currentItem] = filled;
				}
				player.inventory.markDirty();
				player.inventoryContainer.detectAndSendChanges();
				return true;
			}
		}
		else if(FluidContainerRegistry.isFilledContainer(item))
		{
			if(player.worldObj.isRemote)
				return true;
			
			FluidStack container = FluidContainerRegistry.getFluidForFilledItem(item);
			
			if(mTank.fill(container, false) == container.amount)
			{
				if(!player.capabilities.isCreativeMode)
				{
					if(item.getItem().hasContainerItem(item))
						player.inventory.mainInventory[player.inventory.currentItem] = item.getItem().getContainerItem(item);
					else
						player.inventory.decrStackSize(player.inventory.currentItem, 1);
				}
				
				mTank.fill(container, true);
				onFluidChange();
			}

			return true;
		}
		
		return false;
	}
	
	private void updateIfNeeded()
	{
		if(!mHasUpdated && (System.currentTimeMillis() - mLastUpdate) > 200)
		{
			mLastUpdate = System.currentTimeMillis();
			mHasUpdated = true;
			NBTTagCompound tankNBT = new NBTTagCompound();
			mTank.writeToNBT(tankNBT);
			openChannel(CHANNEL_FLUID).writeNBTTagCompound(tankNBT); //.writeFluidStack(mTank.getFluid());
		}
	}
	
	private void onFluidChange()
	{
		mHasUpdated = false;
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
	public boolean canItemEnter( TubeItem item )
	{
		return item.state == TubeItem.BLOCKED;
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
		
		updateIfNeeded();
		
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
		else if(mTank.getFluidAmount() > 0)
		{
			if(getNumConnections() > 0)
			{
				addItem(new FluidPayload(mTank.getFluid()), -1);
				mTank.setFluid(null);
				onFluidChange();
			}
		}
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		if(mTank.getFluidAmount() > 0)
		{
			NBTTagCompound tag = new NBTTagCompound();
			mTank.getFluid().writeToNBT(tag);
			root.setTag("fluid", tag);
		}
		mOverflow.save(root);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		if(root.hasKey("fluid"))
			mTank.setFluid(FluidStack.loadFluidStackFromNBT(root.getCompoundTag("fluid")));
		else
			mTank.setFluid(null);

		mOverflow.load(root);
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		super.writeDesc(packet);
		NBTTagCompound tankNBT = new NBTTagCompound();
		mTank.writeToNBT(tankNBT);
		packet.writeNBTTagCompound(tankNBT);
	}
	
	@Override
	public void readDesc( MCDataInput packet )
	{
		super.readDesc(packet);
		mTank.readFromNBT(packet.readNBTTagCompound());
	}
	
	@Override
	protected void onRecieveDataClient( int channel, MCDataInput input )
	{
		if(channel == CHANNEL_FLUID)
			mTank.readFromNBT(input.readNBTTagCompound());
		else
			super.onRecieveDataClient(channel, input);
	}
	
	@Override
	public int getHollowSize( int side )
	{
		return 10;
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		mOverflow.onDropItems(itemsToDrop);
	}
}
