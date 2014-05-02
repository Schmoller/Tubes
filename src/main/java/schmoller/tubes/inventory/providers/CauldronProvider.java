package schmoller.tubes.inventory.providers;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import schmoller.tubes.api.BlockInstance;
import schmoller.tubes.api.interfaces.IInterfaceProvider;

public class CauldronProvider implements IInterfaceProvider<IFluidHandler>
{
	@Override
	public IFluidHandler provide( Object object )
	{
		return new CauldronHandler((BlockInstance)object);
	}

	private static class CauldronHandler implements IFluidHandler
	{
		private BlockInstance mBlock;
		
		public CauldronHandler(BlockInstance block)
		{
			mBlock = block;
		}
		
		private FluidStack getContained()
		{
			int meta = mBlock.world.getBlockMetadata(mBlock.x, mBlock.y, mBlock.z);
			if(meta == 0)
				return null;
			
			return new FluidStack(FluidRegistry.WATER, (int)(1000 * (1/3D) * meta));
		}
		
		@Override
		public int fill( ForgeDirection from, FluidStack resource, boolean doFill )
		{
			if(resource.getFluid() != FluidRegistry.WATER)
				return 0;
			
			int points = resource.amount / 333;
			int meta = mBlock.world.getBlockMetadata(mBlock.x, mBlock.y, mBlock.z);
			
			if(meta < 3)
			{
				int filled = Math.min((3 - meta), points);
				
				if(doFill)
				{
					((World)mBlock.world).setBlockMetadataWithNotify(mBlock.x, mBlock.y, mBlock.z, meta + filled, 2);
					((World)mBlock.world).notifyBlockOfNeighborChange(mBlock.x, mBlock.y, mBlock.z, mBlock.world.getBlock(mBlock.x, mBlock.y, mBlock.z));
				}
					
				return Math.min((int)(filled * (1000/3D)), resource.amount);
			}
			
			return 0;
		}

		@Override
		public FluidStack drain( ForgeDirection from, FluidStack resource, boolean doDrain )
		{
			if(resource.getFluid() != FluidRegistry.WATER)
				return null;
			
			return drain(from, resource.amount, doDrain);
		}

		@Override
		public FluidStack drain( ForgeDirection from, int maxDrain, boolean doDrain )
		{
			int meta = mBlock.world.getBlockMetadata(mBlock.x, mBlock.y, mBlock.z);
			
			if(meta == 0)
				return null;
			
			int points = maxDrain / 333;
			
			int drained = Math.min(meta, points);
			
			if(doDrain)
			{
				((World)mBlock.world).setBlockMetadataWithNotify(mBlock.x, mBlock.y, mBlock.z, meta - drained, 2);
				((World)mBlock.world).notifyBlockOfNeighborChange(mBlock.x, mBlock.y, mBlock.z, mBlock.world.getBlock(mBlock.x, mBlock.y, mBlock.z));
			}
			
			return new FluidStack(FluidRegistry.WATER, drained * 333);
		}

		@Override
		public boolean canFill( ForgeDirection from, Fluid fluid )
		{
			return (FluidRegistry.WATER == fluid);
		}

		@Override
		public boolean canDrain( ForgeDirection from, Fluid fluid )
		{
			return (FluidRegistry.WATER == fluid);
		}

		@Override
		public FluidTankInfo[] getTankInfo( ForgeDirection from )
		{
			return new FluidTankInfo[] { new FluidTankInfo(getContained(), 1000) };
		}
		
	}
}
