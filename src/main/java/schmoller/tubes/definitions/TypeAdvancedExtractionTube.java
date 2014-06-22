package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.AdvancedExtractionTube;

public class TypeAdvancedExtractionTube extends TubeDefinition
{
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/advExtractionTube.png");
	public static ResourceLocation pump = new ResourceLocation("tubes", "textures/models/tube-advancedextraction-pump.png");
	public static IIcon main;
	public static IIcon backOpen;
	public static IIcon backClosed;
	
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

	@Override
	public void registerIcons( IIconRegister register )
	{
		main = register.registerIcon("tubes:tube-advancedextraction");
		backOpen = register.registerIcon("tubes:tube-advancedextraction-open");
		backClosed = register.registerIcon("tubes:tube-advancedextraction-closed");
	}
}
