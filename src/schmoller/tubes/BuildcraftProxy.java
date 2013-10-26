package schmoller.tubes;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.fluids.FluidRegistry;
import buildcraft.api.recipes.RefineryRecipes;

public class BuildcraftProxy
{
	public BuildcraftProxy()
	{
		RefineryRecipes.addRecipe(FluidRegistry.getFluidStack("fuel", 1), FluidRegistry.getFluidStack("plastic", 2), 8, 1);
		FMLLog.info("Added Plastic refinery recipe");
	}
}
