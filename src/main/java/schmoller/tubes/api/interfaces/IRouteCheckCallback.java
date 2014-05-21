package schmoller.tubes.api.interfaces;

import schmoller.tubes.api.Position;

/**
 * NOTE: The behaviour this was intended for is now automatic. Use {@link IImportController} for control over what gets imported
 */
public interface IRouteCheckCallback
{
	public boolean isEndPointOk(Position position, int fromSide);
}
