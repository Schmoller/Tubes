package schmoller.tubes;

import net.minecraftforge.fluids.FluidRegistry;
import buildcraft.api.recipes.BuildcraftRecipeRegistry;

public class BuildcraftProxy
{
	public BuildcraftProxy()
	{
		BuildcraftRecipeRegistry.refinery.addRecipe("liquidPlastic", FluidRegistry.getFluidStack("fuel", 1), FluidRegistry.getFluidStack("plastic", 1), 8, 1);
	}
}
