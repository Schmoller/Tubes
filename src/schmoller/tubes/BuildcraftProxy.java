package schmoller.tubes;

import net.minecraftforge.fluids.FluidRegistry;
import buildcraft.api.recipes.RefineryRecipes;

public class BuildcraftProxy
{
	public BuildcraftProxy()
	{
		RefineryRecipes.addRecipe(FluidRegistry.getFluidStack("fuel", 1), FluidRegistry.getFluidStack("plastic", 1), 8, 1);
	}
}
