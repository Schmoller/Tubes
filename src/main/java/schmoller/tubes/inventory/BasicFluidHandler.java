package schmoller.tubes.inventory;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import schmoller.tubes.AnyFilter;
import schmoller.tubes.FluidFilter;
import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPayloadHandler;

public class BasicFluidHandler implements IPayloadHandler<FluidPayload>
{
	private IFluidHandler mParent;
	public BasicFluidHandler(IFluidHandler parent)
	{
		mParent = parent;
	}
	
	@Override
	public FluidPayload insert( FluidPayload payload, int side, boolean doAdd )
	{
		int filled = mParent.fill(ForgeDirection.getOrientation(side), (FluidStack)payload.get(), doAdd);
		
		if(filled == payload.size())
			return null;
		
		FluidPayload remaining = payload.copy();
		remaining.setSize(remaining.size() - filled);
		
		return remaining;
	}

	@Override
	public FluidPayload extract( IFilter template, int side, boolean doExtract )
	{
		return extract(template, side, 1000, SizeMode.Max, doExtract);
	}

	@Override
	public FluidPayload extract( IFilter template, int side, int count, SizeMode mode, boolean doExtract )
	{
		if(template == null)
			throw new IllegalArgumentException("Cannot have a null template. Use AnyFilter \"any\"");
		if(template.getPayloadType() != null && !template.getPayloadType().equals(FluidPayload.class))
			throw new IllegalArgumentException("Invalid filter type " + template.getType());
		
		// We just use ourself to test whether the extract would have been successful before we start doing it so we dont need to track state
		if(doExtract)
		{
			if(extract(template, side, count, mode, false) == null)
				return null;
		}

		int amount = count;
		if(mode == SizeMode.Max || mode == SizeMode.GreaterEqual)
			amount = 1000;
		
		FluidStack fluid = null;
		if(template instanceof AnyFilter)
			fluid = mParent.drain(ForgeDirection.getOrientation(side), amount, doExtract);
		else
			fluid = mParent.drain(ForgeDirection.getOrientation(side), new FluidStack(((FluidFilter)template).getFluid(), amount), doExtract);
		
		if(fluid == null)
			return null;
		
		if(mode == SizeMode.Exact)
		{
			if(fluid.amount != count)
				return null;
		}
		else if(mode == SizeMode.GreaterEqual)
		{
			if(fluid.amount < count)
				return null;
		}
		else if(mode == SizeMode.LessEqual)
		{
			if(fluid.amount > count)
				return null;
		}
		
		return new FluidPayload(fluid);
	}
	
	@Override
	public boolean isSideAccessable( int side )
	{
		return true;
	}
	
	@Override
	public Collection<FluidPayload> listContents( IFilter filter, int side )
	{
		assert(filter != null);
		assert(side >= 0 && side < 6);
		
		FluidTankInfo[] tanks = mParent.getTankInfo(ForgeDirection.getOrientation(side));
		ArrayList<FluidPayload> payloads = new ArrayList<FluidPayload>();
		for(FluidTankInfo tank : tanks)
		{
			if(tank.fluid != null)
			{
				FluidPayload payload = new FluidPayload(tank.fluid);
				if(filter.matches(payload, SizeMode.Max))
					payloads.add(payload.copy());
			}
		}
		
		return payloads;
	}
	
	@Override
	public Collection<FluidPayload> listContents( int side )
	{
		return listContents(new AnyFilter(64), side);
	}
}
