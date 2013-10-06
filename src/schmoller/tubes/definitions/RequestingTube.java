package schmoller.tubes.definitions;

import codechicken.core.vec.Cuboid6;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.ITube;
import schmoller.tubes.logic.RequestingTubeLogic;
import schmoller.tubes.logic.TubeLogic;
import schmoller.tubes.parts.BaseTubePart;
import schmoller.tubes.parts.DirectionalRedstoneTubePart;

public class RequestingTube extends TubeDefinition
{
	public static Icon icon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		icon = register.registerIcon("Tubes:tube-requesting");
	}
	
	@Override
	public Icon getCenterIcon()
	{
		return NormalTube.centerIcon;
	}

	@Override
	public Icon getStraightIcon()
	{
		return NormalTube.straightIcon;
	}

	@Override
	public TubeLogic getTubeLogic( ITube tube )
	{
		return new RequestingTubeLogic(tube);
	}

	@Override
	public Class<? extends BaseTubePart> getPartClass()
	{
		return DirectionalRedstoneTubePart.class;
	}
	
	@Override
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
	}
}
