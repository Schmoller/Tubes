package schmoller.tubes.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import schmoller.tubes.CompoundFilter;
import schmoller.tubes.ModTubes;
import schmoller.tubes.RedstoneMode;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.OverflowBuffer;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.IPropertyHolder;
import schmoller.tubes.api.interfaces.ITubeOverflowDestination;
import schmoller.tubes.routing.OutputRouter;

public class AdvancedExtractionTube extends DirectionalTube implements ITubeOverflowDestination, IPropertyHolder
{
	public enum PullMode
	{
		NormalAllow,
		NormalDeny,
		Random,
		Sequence,
		Overflow
	}
	
	public static final int PROP_PULLMODE = 1;
	public static final int PROP_REDSTONEMODE = 2;
	public static final int PROP_SIZEMODE = 3;
	public static final int PROP_COLOR = 4;
	
	private static Random mRand = new Random();
	private OverflowBuffer mOverflow;
	private PullMode mMode = PullMode.NormalAllow;
	private RedstoneMode mRSMode = RedstoneMode.High;
	private SizeMode mSize = SizeMode.Max;
	private IFilter[] mFilters;
	private int mColor = -1;
	
	private int mNext;
	
	public AdvancedExtractionTube()
	{
		super("advancedExtraction");
		mFilters = new IFilter[24];
		mOverflow = new OverflowBuffer();
	}
	
	@Override
	public <T> T getProperty( int prop )
	{
		switch(prop)
		{
		case PROP_PULLMODE:
			return (T)mMode;
		case PROP_REDSTONEMODE:
			return (T)mRSMode;
		case PROP_SIZEMODE:
			return (T)mSize;
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
		case PROP_PULLMODE:
			mMode = (PullMode)value;
			break;
		case PROP_REDSTONEMODE:
			mRSMode = (RedstoneMode)value;
			break;
		case PROP_SIZEMODE:
			mSize = (SizeMode)value;
			break;
		case PROP_COLOR:
			mColor = ((Number)value).intValue();
			break;
		}
	}
	
	@Override
	protected int getConnectableSides()
	{
		return ~(1 << getFacing());
	}
	
	@Override
	public boolean canAcceptOverflowFromSide( int side )
	{
		return (side != (getFacing() ^ 1));
	}
	
	@Override
	public OverflowBuffer getOverflowContents()
	{
		return mOverflow;
	}
	
	@Override
	protected void onDropItems( List<ItemStack> itemsToDrop )
	{
		super.onDropItems(itemsToDrop);
		mOverflow.onDropItems(itemsToDrop);
	}
		
	public PullMode getPullMode()
	{
		return mMode;
	}
	
	public void setPullMode(PullMode mode)
	{
		mMode = mode;
	}
	
	public RedstoneMode getRSMode()
	{
		return mRSMode;
	}
	
	public void setRSMode(RedstoneMode mode)
	{
		mRSMode = mode;
	}
	
	public void setColor(int color)
	{
		mColor = color;
	}
	
	@Override
	public int getColor()
	{
		return mColor;
	}
	
	public IFilter getFilter(int index)
	{
		return mFilters[index];
	}
	
	public void setFilter(int index, IFilter filter)
	{
		mFilters[index] = filter;
	}
	
	private ArrayList<IFilter> getAllFilters()
	{
		ArrayList<IFilter> filters = new ArrayList<IFilter>();
		for(int i = 0; i < mFilters.length; ++i)
		{
			IFilter filter = mFilters[i];
			if(filter != null)
				filters.add(filter);
		}
		
		return filters;
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
				item.direction = item.lastDirection = getFacing() ^ 1;
				item.updated = false;
				item.setProgress(0);
				addItem(item, true);
			}
			
			return;
		}
		
		// TODO: Redstone check
		
		IFilter filter = null;
		int next = mNext;
		switch(mMode)
		{
		case NormalAllow:
			filter = new CompoundFilter(getAllFilters(), false);
			break;
		case NormalDeny:
			filter = new CompoundFilter(getAllFilters(), true);
			break;
		case Overflow:
			// TODO: Not done
			break;
		case Random:
		{
			ArrayList<IFilter> filters = getAllFilters();
			if(!filters.isEmpty())
				filter = filters.get(mRand.nextInt(filters.size()));
			break;
		}
		case Sequence:
		{
			int start = next;
			do
			{
				filter = mFilters[next++];
				if(next > mFilters.length)
					next = 0;
			}
			while(filter != null && next != start);
			break;
		}
		}
		
		if(filter == null)
			return;
		
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
		
		IPayloadHandler handler = InteractionHandler.getHandler(filter.getPayloadType(), world(), x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
		if(handler == null)
			return;
		
		Payload extracted = handler.extract(filter, getFacing() ^ 1, filter.size(), mSize, false);
		
		if(extracted != null)
		{
			TubeItem item = new TubeItem(extracted);
			item.colour = mColor;
			item.direction = dir.ordinal() ^ 1;
			
			if(new OutputRouter(world(), new Position(x(), y(), z()), item).route() != null)
			{
				handler.extract(filter, getFacing() ^ 1, filter.size(), mSize, true);
				addItem(item, true);
				
				if(mMode == PullMode.Sequence)
					mNext = next;
			}
		}
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.state == TubeItem.BLOCKED)
		{
			item.lastDirection = item.direction; 
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
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(!super.activate(player, part, item))
			player.openGui(ModTubes.instance, ModTubes.GUI_ADV_EXTRACTION, world(), x(), y(), z());
		
		return true;
	}
}
