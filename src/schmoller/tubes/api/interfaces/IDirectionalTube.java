package schmoller.tubes.api.interfaces;

/**
 * Tubes that implement this have an important direction.
 */
public interface IDirectionalTube
{
	/**
	 * Gets the primary direction of the tube
	 */
	public int getFacing();

	/**
	 * Checks whether the tube can face that side
	 */
	public boolean canFaceDirection(int face);

	/**
	 * Sets the facing of the tube.
	 * When a player places a tube, this will called to face the side they clicked on.
	 */
	public void setFacing(int face);
}
