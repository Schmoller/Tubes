package schmoller.tubes.api.interfaces;

import schmoller.tubes.api.Position;

public interface IRouteCheckCallback
{
	public boolean isEndPointOk(Position position, int fromSide);
}
