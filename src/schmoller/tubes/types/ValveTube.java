package schmoller.tubes.types;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;

import schmoller.tubes.api.Payload;
import schmoller.tubes.api.TubeItem;

public class ValveTube extends DirectionalBasicTube implements IRedstonePart
{
	private boolean mIsOpen = false;
	
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
		if(mIsOpen || item.direction != (getFacing() ^ 1))
			return super.canItemEnter(item);

		return false;
	}
	
	@Override
	public boolean canAddItem( Payload item, int direction )
	{
		if(mIsOpen || direction != (getFacing() ^ 1))
			return super.canAddItem(item, direction);
		
		return false;
	}

	@Override
	public boolean addItem( TubeItem item, boolean syncToClient )
	{
		return super.addItem(item, syncToClient);
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
