package schmoller.tubes;

import codechicken.core.lighting.LazyLightMatrix;
import codechicken.core.vec.Cuboid6;
import codechicken.core.vec.Vector3;
import codechicken.multipart.JCuboidPart;

public class BaseTube extends JCuboidPart
{
	@Override
	public String getType()
	{
		return "schmoller_tube";
	}

	@Override
	public Cuboid6 getBounds()
	{
		return new Cuboid6(0.2, 0.2, 0.2, 0.8, 0.8, 0.8);
	}

}
