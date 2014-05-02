package schmoller.tubes;

import net.minecraftforge.fluids.FluidRegistry;
import buildcraft.core.recipes.RefineryRecipeManager;

public class BuildcraftProxy
{
	public BuildcraftProxy()
	{
		RefineryRecipeManager.INSTANCE.addRecipe(FluidRegistry.getFluidStack("fuel", 1), FluidRegistry.getFluidStack("plastic", 1), 8, 1);
	}
}
