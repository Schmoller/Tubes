package schmoller.tubes.api.interfaces;

import schmoller.tubes.api.OverflowBuffer;

/**
 * Tube connectables that implement this will be able to accept items with no destination. 
 * Should they receive any they should block preventing other items from being processed by them.
 */
public interface ITubeOverflowDestination
{
	/**
	 * True if the tube connectable can accept blocked items from the specified side
	 */
	public boolean canAcceptOverflowFromSide(int side);
	
	public OverflowBuffer getOverflowContents();
}
