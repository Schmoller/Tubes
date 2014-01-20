package schmoller.tubes.types;

import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.api.InventoryHandlerRegistry;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.IInventoryHandler;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.routing.OutputRouter;

public class ExtractionTube extends DirectionalBasicTube implements IRedstonePart, ITubeOverflowDestination
{
	private boolean mIsPowered;
	private OverflowBuffer mOverflow;
	
	public static final int BLOCKED_TICKS = 10;
	public static final int CHANNEL_POWERED = 2;
	
	public float animTime = 0;
	
	public ExtractionTube()
	{
		super("extraction");
		mIsPowered = false;
		mOverflow = new OverflowBuffer();
	}

	@Override
	public int getTickRate()
	{
		return mOverflow.isEmpty() ? 20 : BLOCKED_TICKS;
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
				item.direction = getFacing() ^ 1;
				item.updated = false;
				item.progress = 0;
				addItem(item, true);
			}
			
			return;
		}
		else if(mIsPowered)
			return;
		
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		
		IInventoryHandler handler = InventoryHandlerRegistry.getHandlerFor(world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		if(handler != null)
		{
			ItemStack extracted = handler.extractItem(null, dir.ordinal() ^ 1, true);
			if(extracted != null)
				addItem(extracted, dir.ordinal() ^ 1);
		}
	}
	
	@Override
	public boolean canAcceptOverflowFromSide( int side )
	{
		return (side != (getFacing() ^ 1));
	}
	
	private int getPower()
	{
		int current = 0;
		for(int side = 0; side < 6; ++side)
			current = Math.max(current, RedstoneInteractions.getPowerTo(world(), x(), y(), z(), side, 0x1f));
		
		return current;
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		mOverflow.onDropItems(itemsToDrop);
	}
	
	@Override
	public void onWorldJoin()
	{
		mIsPowered = getPower() > 0;
	}
	
	@Override
	public void update()
	{
		boolean powered = getPower() > 0;
		
		if(powered != mIsPowered && !world().isRemote)
			openChannel(CHANNEL_POWERED).writeBoolean(powered);
		
		mIsPowered = powered;
		
		if(world().isRemote)
		{
			if(!isPowered())
			{
				animTime += 0.05;
				if(animTime > 1)
					animTime -= 1;
			}
			else if(animTime > 0)
			{
				animTime += 0.05;
				if(animTime > 1)
					animTime = 0;
			}
		}
		
		super.update();
	}
	
	@Override
	public boolean canConnectRedstone( int side ) { return true; }

	@Override
	public int strongPowerLevel( int side ) { return 0; }

	@Override
	public int weakPowerLevel( int side ) { return 0; }

	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.state == TubeItem.BLOCKED)
		{
			item.direction = getFacing();
			item.updated = true;
			return true;
		}
		else
			return super.onItemJunction(item);
	}
	
	@Override
	protected boolean onItemLeave( TubeItem item )
	{
		if(item.state == TubeItem.BLOCKED && item.direction == getFacing())
		{
			if(!world().isRemote)
				mOverflow.addItem(item);
			
			return true;
		}

		return super.onItemLeave(item);
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		mOverflow.save(root);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		mOverflow.load(root);
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		super.writeDesc(packet);
		
		packet.writeBoolean(mIsPowered);
	}
	
	@Override
	public void readDesc( MCDataInput packet )
	{
		super.readDesc(packet);
		
		mIsPowered = packet.readBoolean();
	}
	
	@Override
	protected void onRecieveDataClient( int channel, MCDataInput input )
	{
		if(channel == CHANNEL_POWERED)
			mIsPowered = input.readBoolean();
		else
			super.onRecieveDataClient(channel, input);
	}
	
	public boolean isPowered()
	{
		return mIsPowered;
	}
}
