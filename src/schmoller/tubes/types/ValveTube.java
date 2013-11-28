package schmoller.tubes.types;

import java.util.List;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubesAPI;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;

public class ValveTube extends DirectionalBasicTube implements ITubeOverflowDestination, IRedstonePart
{
	private boolean mIsOpen = false;
	private OverflowBuffer mOverflow = new OverflowBuffer();
	
	private static int CHANNEL_STATE = 2;
	
	public ValveTube()
	{
		super("valve");
	}
	
	@Override
	public int getHollowSize( int side )
	{
		if(side == getFacing())
			return 10;
		return super.getHollowSize(side);
	}
	
	@Override
	protected int getConnectableSides()
	{
		return 63;
	}
	
	@Override
	public boolean canItemEnter( TubeItem item )
	{
		if(!mOverflow.isEmpty() && item.state != TubeItem.BLOCKED && item.direction != (getFacing() ^ 1))
			return false;
		
		if(item.state == TubeItem.BLOCKED && item.direction == (getFacing() ^ 1))
			return super.canItemEnter(item);
		
		if(mIsOpen || item.direction != (getFacing() ^ 1))
			return super.canItemEnter(item);

		return false;
	}
	
	@Override
	public boolean canAddItem( Payload item, int direction )
	{
		if(!mOverflow.isEmpty())
			return false;
		
		if(mIsOpen || direction != (getFacing() ^ 1))
			return super.canAddItem(item, direction);
		
		return false;
	}

	@Override
	public int getTickRate()
	{
		return 10;
	}
	
	@Override
	public void onTick()
	{
		if(!mOverflow.isEmpty() && !world().isRemote)
		{
			TubeItem item = mOverflow.peekNext();
			
			PathLocation loc = null;
			if(mIsOpen)
				loc = TubesAPI.instance.getOutputRouter(world(), new Position(x(),y(),z()), item).route();
			else
				loc = TubesAPI.instance.getOutputRouter(world(), new Position(x(),y(),z()), item, getFacing()).route();
			
			if(loc != null)
			{
				mOverflow.getNext();
				item.state = TubeItem.NORMAL;
				item.direction = loc.initialDir;
				item.updated = true;
				item.setProgress(0.5f);
				addItem(item, true);
			}
		}
	}

	@Override
	public boolean canAcceptOverflowFromSide( int side )
	{
		return !mIsOpen && (side == (getFacing() ^ 1));
	}

	@Override
	public boolean addItem( TubeItem item, boolean syncToClient )
	{
		if(item.state == TubeItem.BLOCKED && item.direction == (getFacing() ^ 1))
		{
			if(!world().isRemote)
				mOverflow.addItem(item);
			
			return true;
		}
		
		return super.addItem(item, syncToClient);
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		
		mOverflow.onDropItems(itemsToDrop);
	}
	
	@Override
	public void load( NBTTagCompound root )
	{
		super.load(root);
		
		mOverflow.load(root);
	}
	
	@Override
	public void save( NBTTagCompound root )
	{
		super.save(root);
		
		mOverflow.save(root);
	}
	
	@Override
	public void writeDesc( MCDataOutput packet )
	{
		super.writeDesc(packet);
		
		packet.writeBoolean(mIsOpen);
	}
	
	@Override
	public void readDesc( MCDataInput packet )
	{
		super.readDesc(packet);
		
		mIsOpen = packet.readBoolean();
	}
	
	@Override
	protected void onRecieveDataClient( int channel, MCDataInput input )
	{
		if(channel == CHANNEL_STATE)
		{
			mIsOpen = input.readBoolean();
			markForRender();
		}
		else
			super.onRecieveDataClient(channel, input);
	}
	
	private int getPower()
	{
		int current = 0;
		for(int side = 0; side < 6; ++side)
			current = Math.max(current, RedstoneInteractions.getPowerTo(world(), x(), y(), z(), side, 0x1f));
		
		return current;
	}
	
	@Override
	public void onWorldJoin()
	{
		mIsOpen = getPower() > 0;
	}
	
	@Override
	public void update()
	{
		if(!world().isRemote)
		{
			boolean state = getPower() > 0;
			
			if(mIsOpen != state)
				openChannel(CHANNEL_STATE).writeBoolean(state);
			
			mIsOpen = state;
		}
		
		super.update();
	}

	@Override
	public boolean canConnectRedstone( int side )
	{
		return true;
	}

	@Override
	public int strongPowerLevel( int side )
	{
		return 0;
	}

	@Override
	public int weakPowerLevel( int side )
	{
		return 0;
	}

	public boolean isOpen()
	{
		return mIsOpen;
	}
}
