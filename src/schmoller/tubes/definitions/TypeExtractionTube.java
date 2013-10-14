package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.ExtractionTube;

public class TypeExtractionTube extends TubeDefinition
{
	public static Icon icon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		icon = register.registerIcon("Tubes:tube-extraction");
	}
	
	@Override
	public Icon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}

	@Override
	public Icon getStraightIcon()
	{
		return TypeNormalTube.straightIcon;
	}

	@Override
	public BaseTube createTube()
	{
		return new ExtractionTube();
	}
}
