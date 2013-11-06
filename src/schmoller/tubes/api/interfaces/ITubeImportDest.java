package schmoller.tubes.api.interfaces;

/**
 * Tube connectables that implement this will have imported items routed to them.
 */
public interface ITubeImportDest
{
	/**
	 * True if items can be imported from the speficied side.
	 */
	public boolean canImportFromSide(int side);
}
