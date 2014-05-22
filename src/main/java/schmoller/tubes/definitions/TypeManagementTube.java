package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import codechicken.multipart.TMultiPart;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.types.ManagementTube;

public class TypeManagementTube extends TubeDefinition
{
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/managementTube.png");
	
	public static IIcon main;
	public static IIcon invEnd;
	public static IIcon back;
	
	@Override
	public IIcon getCenterIcon()
	{
		return main;
	}

	@Override
	public IIcon getStraightIcon()
	{
		return TypeNormalTube.straightIcon;
	}

	@Override
	public TMultiPart createTube()
	{
		return new ManagementTube();
	}
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		main = register.registerIcon("tubes:tube-management");
		invEnd = register.registerIcon("tubes:tube-management-inv");
		back = register.registerIcon("tubes:tube-management-back");
	}

}
