package schmoller.tubes.definitions;

import codechicken.lib.vec.Cuboid6;
import schmoller.tubes.parts.BaseTubePart;
import schmoller.tubes.types.BaseTube;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;


public abstract class TubeDefinition
{
	public void registerIcons(IconRegister register) {}
	
	public abstract Icon getCenterIcon();
	public abstract Icon getStraightIcon();

	public abstract BaseTube createTube();

	public Class<? extends BaseTubePart> getPartClass() { return BaseTubePart.class; }
	
	public Cuboid6 getSize()
	{
		return new Cuboid6(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
	}
}
