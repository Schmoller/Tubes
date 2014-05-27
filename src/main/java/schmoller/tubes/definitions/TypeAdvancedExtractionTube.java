package schmoller.tubes.definitions;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.AdvancedExtractionTube;

public class TypeAdvancedExtractionTube extends TubeDefinition
{
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/advExtractionTube.png");
	
	@Override
	public IIcon getCenterIcon()
	{
		return TypeNormalTube.centerIcon;
	}

	@Override
	public IIcon getStraightIcon()
	{
		return TypeNormalTube.straightIcon;
	}

	@Override
	public TMultiPart createTube()
	{
		return new AdvancedExtractionTube();
	}

}
