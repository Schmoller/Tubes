package schmoller.tubes.nei;

import net.minecraft.util.StatCollector;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;

public class NEITubesConfig implements IConfigureNEI
{
	@Override
	public String getName()
	{
		return StatCollector.translateToLocal("itemGroup.tubes");
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
		
		GuiContainerManager.addTooltipHandler(new ExtContainerTooltipHandler());
	}
}
