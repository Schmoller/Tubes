package schmoller.tubes.api.interfaces;

import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;

public interface IImportSource
{
	public boolean canPullItem(IFilter filter, int side, int count, SizeMode mode);
	public Payload pullItem(IFilter filter, int side, int count, SizeMode mode, boolean doExtract);
}
