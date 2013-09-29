package schmoller.tubes.parts;

import schmoller.tubes.render.RenderTubePart;
import net.minecraft.util.Icon;

public class RestrictionTubePart extends BaseTubePart
{
	public static Icon center;
	public static Icon straight;
	
	@Override
	public int getRouteWeight()
	{
		return 5000;
	}
	
	@Override
	protected void setRenderIcons( RenderTubePart render )
	{
		render.setIcons(center, straight);
	}
}
