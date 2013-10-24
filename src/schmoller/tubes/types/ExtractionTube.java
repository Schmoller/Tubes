package schmoller.tubes.types;

import codechicken.multipart.IRedstonePart;
import codechicken.multipart.RedstoneInteractions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.ITubeConnectable;
import schmoller.tubes.ITubeOverflowDestination;
import schmoller.tubes.OverflowBuffer;
import schmoller.tubes.Position;
import schmoller.tubes.TubeItem;
import schmoller.tubes.inventory.InventoryHelper;
import schmoller.tubes.routing.BaseRouter.PathLocation;
import schmoller.tubes.routing.OutputRouter;

public class ExtractionTube extends DirectionalBasicTube implements IRedstonePart, ITubeOverflowDestination
{
	private boolean mIsPowered;
	private OverflowBuffer mOverflow;
	
	public static final int BLOCKED_TICKS = 10;
	
	public ExtractionTube()
	{
		super("extraction");
		mIsPowered = false;
		mOverflow = new OverflowBuffer();
	}
	
	@Override
	public boolean canConnectTo( ITubeConnectable con )
	{
		if(con instanceof ExtractionTube)
			return false;
		
		return super.canConnectTo(con);
	}

	@Override
	public int getTickRate()
	{
		return mOverflow.isEmpty() ? 20 : BLOCKED_TICKS;
	}
	
	@Override
	public void onTick()
	{
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
		
		ItemStack item = InventoryHelper.extractItem(world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ, dir.ordinal(), null);
		
		if(item != null)
			addItem(item, dir.ordinal() ^ 1);
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
	public void onWorldJoin()
	{
		mIsPowered = getPower() > 0;
	}
	
	@Override
	public void update()
	{
		mIsPowered = getPower() > 0;
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
}
