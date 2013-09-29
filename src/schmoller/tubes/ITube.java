package schmoller.tubes;

import net.minecraft.world.World;

public interface ITube extends ITubeConnectable
{
	public boolean isBlocked();
	
	public int x();
	
	public int y();
	
	public int z();
	
	public World world();
	
	public int getConnections();
}
