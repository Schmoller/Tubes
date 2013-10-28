package schmoller.tubes.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEITubesConfig implements IConfigureNEI
{
	@Override
	public String getName()
	{
		return "Tubes!";
	}
	
	@Override
	public String getVersion()
	{
		return "@{mod.version}";
	}
	
	@Override
	public void loadConfig()
	{
		API.registerUsageHandler(new SpecialShapedRecipeHandler());
		API.registerRecipeHandler(new SpecialShapedRecipeHandler());
		
		API.registerUsageHandler(new SpecialShapelessRecipeHandler());
		API.registerRecipeHandler(new SpecialShapelessRecipeHandler());
		
		API.registerNEIGuiHandler(new DragDropHandler());
	}
}
