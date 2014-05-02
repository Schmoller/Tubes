package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.BaseTube;
import schmoller.tubes.types.RoutingTube;

public class TypeRoutingTube extends TubeDefinition
{
	public static IIcon center;
	public static IIcon colours;
	
	public static ResourceLocation gui = new ResourceLocation("tubes", "textures/gui/routingTube.png");
	
	public static int[] sideColours = new int[] {15, 0, 11, 14, 4, 9};
	public static EnumChatFormatting[] sideColoursText = new EnumChatFormatting[] {EnumChatFormatting.DARK_GRAY, EnumChatFormatting.WHITE, EnumChatFormatting.BLUE, EnumChatFormatting.RED, EnumChatFormatting.YELLOW, EnumChatFormatting.DARK_AQUA};
	
	@Override
	public void registerIcons( IIconRegister register )
	{
		center = register.registerIcon("Tubes:tube-routing-center");
		colours = register.registerIcon("Tubes:tube-routing-colours");
	}
	
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
	public BaseTube createTube()
	{
		return new RoutingTube();
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
