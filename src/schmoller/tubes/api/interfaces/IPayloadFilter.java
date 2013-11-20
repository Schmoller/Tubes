package schmoller.tubes.api.interfaces;

import schmoller.tubes.api.Payload;

public interface IPayloadFilter extends Comparable<Payload>
{
	public boolean matches(Payload payload);
}
