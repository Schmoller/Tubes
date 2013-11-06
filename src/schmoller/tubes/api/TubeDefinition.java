package schmoller.tubes.api;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;


public abstract class TubeDefinition
{
	public void registerIcons(IconRegister register) {}
	
	public abstract Icon getCenterIcon();
	public abstract Icon getStraightIcon();

	public abstract TMultiPart createTube();

	public Cuboid6 getSize()
	{
		return new Cuboid6(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
	}
}
