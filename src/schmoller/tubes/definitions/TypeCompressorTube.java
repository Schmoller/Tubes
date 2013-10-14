package schmoller.tubes.definitions;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import schmoller.tubes.types.BaseTube;
import schmoller.tubes.types.CompressorTube;

public class TypeCompressorTube extends TubeDefinition
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
		return new CompressorTube();
	}

}
