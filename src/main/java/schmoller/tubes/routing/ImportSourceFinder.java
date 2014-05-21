package schmoller.tubes.routing;

import schmoller.tubes.AnyFilter;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.BaseRouter;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IImportSource;
import schmoller.tubes.api.interfaces.IImportController;
import schmoller.tubes.api.interfaces.IPayloadHandler;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class ImportSourceFinder extends BaseRouter
{
	private IFilter mItem;
	private int mStartDir;
	private SizeMode mMode;
	private int mImportColor;
	private Position mStartPosition;
	
	private IImportController mImporter;
	
	
	public ImportSourceFinder(IBlockAccess world, Position position, int startDirection, IFilter filterItem, SizeMode mode)
	{
		this(world, position, startDirection, filterItem, mode, -1);
	}
	
	public ImportSourceFinder(IBlockAccess world, Position position, int startDirection, IFilter filterItem, SizeMode mode, int importColor)
	{
		mItem = filterItem;
		if(mItem == null)
			mItem = new AnyFilter(0);
		mStartDir = startDirection;
		mMode = mode;
		mImportColor = importColor;
		mStartPosition = position;
		
		setup(world, position);
	}
	
	public ImportSourceFinder setImportControl(IImportController controller)
	{
		mImporter = controller;
		return this;
	}
	
	@Override
	protected void getNextLocations( PathLocation current )
	{
		int conns = TubeHelper.getConnectivity(getWorld(), current.position);
		
		for(int i = 0; i < 6; ++i)
		{
			if((conns & (1 << i)) != 0)
			{
				PathLocation loc = new PathLocation(current, i);
				
				TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
				ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
				
				if(con != null && !(con instanceof IImportSource))
				{
					if(!con.canPathThrough())
						continue;
					loc.dist += con.getRouteWeight() - 1;
				}
				
				addSearchPoint(loc);
			}
		}
	}
	
	@Override
	protected void getInitialLocations( Position position )
	{
		int conns = TubeHelper.getConnectivity(getWorld(), position);
		
		if((conns & (1 << mStartDir)) != 0)
		{
			PathLocation loc = new PathLocation(position, mStartDir);
			
			TileEntity ent = CommonHelper.getTileEntity(getWorld(), loc.position);
			ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
			
			if(con != null && !(con instanceof IImportSource))
			{
				if(!con.canPathThrough())
					return;
				
				loc.dist += con.getRouteWeight() - 1;
			}
			
			addSearchPoint(loc);
		}
	}

	@Override
	protected boolean isTerminator( Position current, int side )
	{
		TileEntity ent = CommonHelper.getTileEntity(getWorld(), current);
		ITubeConnectable con = TubeHelper.getTubeConnectable(ent);
		
		IImportSource source = CommonHelper.getInterface(getWorld(), current, IImportSource.class);
		
		if(source != null)
		{
			if(source.isImportSourceOk(mStartPosition))
				return source.canPullItem(mItem, side ^ 1, mItem.size(), mMode);
		}
		else if(con == null)
		{
			IPayloadHandler handler = InteractionHandler.getHandler((mItem == null ? null : mItem.getPayloadType()), getWorld(),current);
			if(handler != null)
			{
				Payload extracted = handler.extract(mItem, side ^ 1, mItem.size(), mMode, false);
				
				if(extracted != null)
					return true;
			}
		}
		return false;
	}
	
	@Override
	protected boolean isEndPointOk( Position current, int side )
	{
		if(!tryImport(current, side))
			return false;
		
		if(mImporter != null)
		{
			if(!mImporter.isImportSourceOk(current, side))
				return false;
		}
		
		return super.isEndPointOk(current, side);
	}

	private boolean tryImport(Position position, int fromSide)
	{
		IPayloadHandler handler = InteractionHandler.getHandler(mItem.getPayloadType(), getWorld(), position);
		IImportSource importSource = CommonHelper.getInterface(getWorld(), position, IImportSource.class);
		
		Payload extracted = null;
		if(importSource != null)
			extracted = importSource.pullItem(mItem, fromSide ^ 1, mItem.size(), mMode, false);
		else if(handler != null)
			extracted = handler.extract(mItem, fromSide ^ 1, mItem.size(), mMode, false);
		
		if(extracted == null)
			return false;
		
		if(mImporter != null)
		{
			if(!mImporter.isImportItemOk(extracted))
				return false;
		}
		
		// Run a test to make sure it can reach an import destination
		TubeItem tItem = new TubeItem(extracted);
		tItem.state = TubeItem.IMPORT;
		tItem.direction = fromSide ^ 1;
		tItem.colour = mImportColor;
		
		Position routePos = position.copy().offset(fromSide ^ 1, 1);
		if(routePos.equals(mStartPosition))
			return true;
		
		return (new InputRouter(getWorld(), routePos, tItem).route() != null);
	}
}
