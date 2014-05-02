package schmoller.tubes.api;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.TMultiPart;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public abstract class TubeDefinition
{
	public void registerIcons(IIconRegister register) {}
	
	public abstract IIcon getCenterIcon();
	public abstract IIcon getStraightIcon();

	public abstract TMultiPart createTube();

	public Cuboid6 getSize()
	{
		return new Cuboid6(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
	}
}
