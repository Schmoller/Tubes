package schmoller.tubes.api.interfaces;

import schmoller.tubes.api.Payload;
import schmoller.tubes.api.Position;

public interface IImportController
{
	public boolean isImportItemOk(Payload item);
	public boolean isImportSourceOk(Position position, int fromSide);
}
