package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.ITube;
import schmoller.tubes.logic.CompressorTubeLogic;
import schmoller.tubes.logic.TubeLogic;

public class CompressorTube extends TubeDefinition
{
	public static Icon compressorIcon;
	
	@Override
	public void registerIcons( IconRegister register )
	{
		compressorIcon = register.registerIcon("Tubes:tube-compressor-center");
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
		return new CompressorTubeLogic(tube);
	}

}
