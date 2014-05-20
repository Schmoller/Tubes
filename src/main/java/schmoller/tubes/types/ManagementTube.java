package schmoller.tubes.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;

import schmoller.tubes.AnyFilter;
import schmoller.tubes.ModTubes;
import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.helpers.BaseRouter.PathLocation;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.IRouteCheckCallback;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import schmoller.tubes.api.interfaces.ITubeImportDest;
import schmoller.tubes.routing.ImportSourceFinder;
import schmoller.tubes.routing.InputRouter;

public class ManagementTube extends DirectionalTube implements ITubeImportDest, IRouteCheckCallback
{
	public enum ManagementMode
	{
		Stock,
		Fill,
		PassiveStock,
		PassiveFill;
	}
	
	private IFilter[][] mFilters;
	private ManagementMode mMode;
	private int mColor;
	private int mPriority;
	
	private IFilter mActiveFilter;
	public ManagementTube()
	{
		super("management");
		mFilters = new IFilter[6][8];
		mMode = ManagementMode.Stock;
		mColor = -1;
		mPriority = 0;
	}
	
	@Override
	protected int getConnectableSides()
	{
		return 1 << (getFacing() ^ 1); 
	}
	
	@Override
	public boolean canConnectToInventories()
	{
		return false;
	}
	
	public IFilter getFilter(int x, int y)
	{
		return mFilters[y][x];
	}
	
	public void setFilter(int x, int y, IFilter filter)
	{
		mFilters[y][x] = filter;
	}
	
	private IPayloadHandler<? extends Payload> getHandler(Class<? extends Payload> payloadClass)
	{
		Position pos = new Position(x(), y(), z()).offset(getFacing(), 1);
		return InteractionHandler.getHandler(payloadClass, world(), pos);
	}
	
	private boolean filtersMatch(Payload payload)
	{
		for(int x = 0; x < 8; ++x)
		{
			for(int y = 0; y < 6; ++y)
			{
				IFilter filter = mFilters[y][x];
				if(filter != null)
				{
					if(filter.matches(payload, SizeMode.Max))
						return true;
				}
			}
		}
		
		return false;
	}
	
	private List<Payload> combine(Collection<? extends Payload> payloads)
	{
		ArrayList<Payload> newPayloads = new ArrayList<Payload>();
		for(Payload payload : payloads)
		{
			boolean merged = false;
			for(Payload existing : newPayloads)
			{
				if(payload.isPayloadTypeEqual(existing))
				{
					existing.setSize(existing.size() + payload.size());
					merged = true;
					break;
				}
			}
			
			if(!merged)
				newPayloads.add(payload.copy());
		}
		
		return newPayloads;
	}
	
	private int getMaxLevel(Payload payload)
	{
		int count = 0;
		for(int x = 0; x < 8; ++x)
		{
			for(int y = 0; y < 6; ++y)
			{
				IFilter filter = mFilters[y][x];
				if(filter != null)
				{
					if(filter.matches(payload, SizeMode.Max))
						count += filter.size();
				}
			}
		}
		return count;
	}
	
	private int getCurrentLevel(Payload payload)
	{
		IPayloadHandler<? extends Payload> handler = getHandler(null);
		List<Payload> contents = combine(handler.listContents(getFacing() ^ 1));
		
		for(Payload item : contents)
		{
			if(item.isPayloadTypeEqual(payload))
				return item.size();
		}
		
		return 0;
	}
	
	private boolean extract(Payload payload)
	{
		int size = Math.min(payload.size(), payload.maxSize());
		
		IFilter filter = FilterRegistry.getInstance().createFilter(payload);
		if(filter == null)
			return false;
		
		IPayloadHandler<? extends Payload> handler = getHandler(payload.getClass());
		
		Payload extracted = handler.extract(filter, getFacing() ^ 1, size, SizeMode.Exact, true);
		
		if(extracted != null)
		{
			addItem(extracted, getFacing() ^ 1);
			return true;
		}
		
		return false;
	}
	
	private boolean expelExcess(List<Payload> contents)
	{
		for(Payload payload : contents)
		{
			// Remove any that dont match any filter
			if(!filtersMatch(payload))
			{
				if(extract(payload))
					return true;
			}
			// Remove excess for ones that do match as long as there is a size requirement
			else if(mMode == ManagementMode.Stock || mMode == ManagementMode.PassiveStock)
			{
				int max = getMaxLevel(payload);
				if(payload.size() > max)
				{
					Payload excess = payload.copy();
					excess.setSize(payload.size() - max);
					if(extract(excess))
						return true;
				}
			}
		}
		return false;
	}
	
	private boolean request(IFilter filter)
	{
		mActiveFilter = filter;
		PathLocation source = new ImportSourceFinder(world(), new Position(x(), y(), z()), getFacing() ^ 1, filter, SizeMode.LessEqual).setRouteCheckCallback(this).route();
		
		if(source != null)
		{
			IPayloadHandler handler = InteractionHandler.getHandler((filter == null ? null : filter.getPayloadType()), world(), source.position);
			if(handler != null)
			{
				Payload extracted;
				if(filter == null)
					extracted = handler.extract(new AnyFilter(0), source.dir ^ 1, true);
				else
					extracted = handler.extract(filter, source.dir ^ 1, (filter.size() > filter.getMax() ? filter.getMax() : filter.size()), SizeMode.LessEqual, true);
				
				if(extracted != null)
				{
					TubeItem tItem = new TubeItem(extracted);
					tItem.state = TubeItem.IMPORT;
					tItem.direction = source.dir ^ 1;
					
					PathLocation tubeLoc = new PathLocation(source, source.dir ^ 1);
					TileEntity tile = CommonHelper.getTileEntity(world(), tubeLoc.position);
					ITubeConnectable con = TubeHelper.getTubeConnectable(tile);
					if(con != null)
						con.addItem(tItem, true);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean requestMore(List<Payload> contents)
	{
		// If the filter does not match any, then it will be requested 
		for(int x = 0; x < 8; ++x)
		{
			for(int y = 0; y < 6; ++y)
			{
				IFilter filter = mFilters[y][x];
				if(filter != null)
				{
					if(getHandler(filter.getPayloadType()) == null) // If there is no handler for it, there is no point going any further
						continue;
					
					boolean matched = false;
					for(Payload payload : contents)
					{
						if(filter.matches(payload, SizeMode.Max))
						{
							if(mMode == ManagementMode.Stock)
							{
								int max = getMaxLevel(payload);
								if(payload.size() < max)
								{
									int toRequest = max - payload.size();
									Payload req = payload.copy();
									req.setSize(Math.min(toRequest, req.maxSize()));
									
									// Ensure we could actually add it
									IPayloadHandler<Payload> handler = (IPayloadHandler<Payload>)getHandler(req.getClass());
									Payload leftover = handler.insert(req, getFacing() ^ 1, false);
									if(leftover != null)
									{
										if(leftover.isPayloadEqual(req)) // Nothing could be inserted
										{
											matched = true;
											break;
										}
										req.setSize(req.size() - leftover.size()); // dont request more than we could actually insert
									}
									
									if(request(FilterRegistry.getInstance().createFilter(req)))
										return true;
								}
							}
							else if(mMode == ManagementMode.Fill)
							{
								Payload req = payload.copy();
								req.setSize(req.maxSize());
								
								// Ensure we could actually add it
								IPayloadHandler<Payload> handler = (IPayloadHandler<Payload>)getHandler(req.getClass());
								Payload leftover = handler.insert(req, getFacing() ^ 1, false);
								if(leftover != null)
								{
									if(leftover.isPayloadEqual(req)) // Nothing could be inserted
									{
										matched = true;
										break;
									}
									req.setSize(req.size() - leftover.size()); // dont request more than we could actually insert
								}
								
								if(request(FilterRegistry.getInstance().createFilter(req)))
									return true;
							}
							
							matched = true;
							break;
						}
					}
					
					if(!matched)
					{
						if(request(filter))
							return true;
					}
					
				}
			}
		}
		
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
		if(world().isRemote)
			return;
		
		IPayloadHandler<? extends Payload> handler = getHandler(null);
		List<Payload> contents = combine(handler.listContents(getFacing() ^ 1));
		
		if(!expelExcess(contents))
		{
			if(mMode == ManagementMode.Stock || mMode == ManagementMode.Fill)
			{
				requestMore(contents);
			}
		}
	}
	
	@Override
	public boolean canPathThrough()
	{
		return false;
	}
	
	@Override
	public boolean canAddItem( Payload item, int direction )
	{
		if(world().isRemote)
			return true;
		
		if(!filtersMatch(item))
			return false;
		
		IPayloadHandler<Payload> specHandler = (IPayloadHandler<Payload>)getHandler(item.getClass());
		if(specHandler == null)
			return false;
		
		Payload leftover = specHandler.insert(item, getFacing() ^ 1, false);
		if(leftover != null)
		{
			if(leftover.isPayloadEqual(item))
				return false;
		}
		
		if(mMode == ManagementMode.Fill || mMode == ManagementMode.PassiveFill)
			return true;
		
		List<Payload> contents = combine(specHandler.listContents(getFacing() ^ 1));
		
		for(Payload payload : contents)
		{
			if(payload.isPayloadTypeEqual(item))
			{
				int max = getMaxLevel(item) - getCurrentLevel(item);
				
				return (max > 0);
			}
		}
		
		return true;
	}
	
	@Override
	protected boolean onItemJunction( TubeItem item )
	{
		if(item.direction != getFacing())
			return super.onItemJunction(item);
		
		if(!world().isRemote)
		{
			IPayloadHandler<Payload> handler = (IPayloadHandler<Payload>) getHandler(item.item.getClass());
			if(handler != null)
			{
				Payload remaining = null;
				if(mMode == ManagementMode.Fill || mMode == ManagementMode.PassiveFill)
					remaining = handler.insert(item.item, getFacing(), true);
				else
				{
					int max = getMaxLevel(item.item) - getCurrentLevel(item.item);
					if(max > 0)
					{
						int toAdd = Math.min(max, item.item.size());
						Payload add = item.item.copy();
						add.setSize(toAdd);
						
						remaining = handler.insert(add, getFacing(), true);
						
						if(toAdd != item.item.size())
						{
							if(remaining == null)
							{
								remaining = item.item.copy();
								remaining.setSize(item.item.size() - toAdd);
							}
							else
								remaining.setSize(remaining.size() + (item.item.size() - toAdd));
						}
						
					}
					else
					{
						item.direction = item.direction ^ 1;
						item.state = TubeItem.BLOCKED;
						
						addToClient(item);
						return true;
					}
				}
				
				if(remaining != null)
				{
					item.item.setSize(remaining.size());
					item.direction = item.direction ^ 1;
					item.state = TubeItem.BLOCKED;
					
					addToClient(item);
					return true;
				}
			}
			else
			{
				item.direction = item.direction ^ 1;
				item.state = TubeItem.BLOCKED;
				
				addToClient(item);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean activate( EntityPlayer player, MovingObjectPosition part, ItemStack item )
	{
		if(!super.activate(player, part, item))
			player.openGui(ModTubes.instance, ModTubes.GUI_MANAGEMENT_TUBE, world(), x(), y(), z());
		
		return true;
	}
	
	@Override
	public boolean canImportFromSide( int side )
	{
		return side == getFacing();
	}
	
	@Override
	public boolean isEndPointOk( Position position, int fromSide )
	{
		IPayloadHandler handler = InteractionHandler.getHandler((mActiveFilter == null ? null : mActiveFilter.getPayloadType()), world(), position);
		if(handler != null)
		{
			Payload extracted;
			if(mActiveFilter == null)
				extracted = handler.extract(new AnyFilter(0), fromSide ^ 1, false);
			else
				extracted = handler.extract(mActiveFilter, fromSide ^ 1, mActiveFilter.size(), SizeMode.LessEqual, false);
			
			if(extracted != null)
			{
				// Check that it can fit
				IPayloadHandler<Payload> myHandler = (IPayloadHandler<Payload>)getHandler(extracted.getClass());
				if(myHandler == null)
					return false;
				
				Payload leftover = myHandler.insert(extracted, getFacing() ^ 1, false);
				if(leftover != null)
				{
					if(leftover.isPayloadEqual(extracted))
						return false;
				}
				
				TubeItem tItem = new TubeItem(extracted);
				tItem.state = TubeItem.IMPORT;
				tItem.direction = fromSide ^ 1;
				
				Position routePos = position.copy().offset(fromSide ^ 1, 1);
				if(routePos.equals(new Position(x(),y(),z())))
					return true;
				
				return (new InputRouter(world(), routePos, tItem).route() != null);
			}
		}
		
		return false;
	}
	
	// Keep 6x8 filters
	// Have a priority
	// Have a color
	// Have a mode
	// - Stock (keep specified in the chest)
	// - Fill (keep specified type in chest, no limit)
	// - Passive Stock (accept specified items into the chest, no request)
	// - Passive Fill (accept specified items into the chest, no request, no limit)
	
	
}
