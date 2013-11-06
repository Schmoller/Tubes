package schmoller.tubes.api.interfaces;

import java.util.List;

import schmoller.tubes.api.TubeItem;


import net.minecraft.world.World;

/**
 * This interface should be used for tube connectables that are actually tubes. Use this when implementing new types of tubes.
 */
public interface ITube extends ITubeConnectable
{
	public int x();
	
	public int y();
	
	public int z();
	
	public World world();
	
	/**
	 * Returns a mask of actual connections. Each bit represents a connection to the side specified by: (1 << side). 
	 * So if (getConnections() & (1 << side)) is not 0 then there is a connection to that side 
	 */
	public int getConnections();

	/**
	 * Gets any items that are currently traveling in the tube
	 */
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
