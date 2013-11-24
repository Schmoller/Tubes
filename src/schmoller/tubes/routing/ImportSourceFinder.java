package schmoller.tubes.routing;

import schmoller.tubes.FluidFilter;
import schmoller.tubes.ItemFilter;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.Position;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.helpers.BaseRouter;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IInventoryHandler;
import schmoller.tubes.api.interfaces.ITubeConnectable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class ImportSourceFinder extends BaseRouter
{
	private IFilter mItem;
	private int mStartDir;
	private SizeMode mMode;
	
	public ImportSourceFinder(IBlockAccess world, Position position, int startDirection, IFilter filterItem, SizeMode mode)
	{
		mItem = filterItem;
		mStartDir = startDirection;
		mMode = mode;
		setup(world, position);
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
				
				if(con != null)
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
			
			if(con != null)
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
		
		if(con == null)
		{
			if(mItem == null || mItem instanceof ItemFilter)
			{
				ItemStack item = (mItem == null ? null : ((ItemFilter)mItem).getItem());
				IInventoryHandler handler = InteractionHandler.getInventoryHandler(getWorld(),current);
				if(handler != null)
				{
					ItemStack extracted;
					if(item == null)
						extracted = handler.extractItem(item, side ^ 1, false);
					else
						extracted = handler.extractItem(item, side ^ 1, item.stackSize, mMode, false);
					
					if(extracted != null)
						return true;
				}
			}
			
			if(mItem == null || mItem instanceof FluidFilter)
			{
				FluidStack fluid = (mItem == null ? null : ((FluidFilter)mItem).getFluid());
				IFluidHandler handler = InteractionHandler.getFluidHandler(getWorld(), current);
				if(handler != null)
				{
					FluidStack drained = null;
					if(fluid == null)
						drained = handler.drain(ForgeDirection.getOrientation(side), 1000, false);
					else
						drained = handler.drain(ForgeDirection.getOrientation(side), fluid, false);
					
					if(drained != null)
						return true;
				}
			}
		}
		return false;
	}

}
