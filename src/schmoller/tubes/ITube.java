package schmoller.tubes;

import java.util.List;

import net.minecraft.world.World;

public interface ITube extends ITubeConnectable
{
	public int x();
	
	public int y();
	
	public int z();
	
	public World world();
	
	public int getConnections();

	public List<TubeItem> getItems();

	/**
	 * Gets whether this tube can connect to inventories
	 * Basic Tube connectivity checks are already done by this point
	 */
	public boolean canConnectToInventories();
	/**
	 * Gets whether this tube can connect to the specified object
	 * Basic Tube connectivity checks are already done by this point
	 */
	public boolean canConnectTo(ITubeConnectable con);
}
