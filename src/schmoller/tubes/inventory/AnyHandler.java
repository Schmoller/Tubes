package schmoller.tubes.inventory;

import java.util.List;

import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IPayloadHandler;

public class AnyHandler implements IPayloadHandler<Payload>
{
	private List<IPayloadHandler> mHandlers;
	
	public AnyHandler(List<IPayloadHandler> handlers)
	{
		mHandlers = handlers;
	}
	
	
	@Override
	public Payload insert( Payload payload, int side, boolean doAdd )
	{
		throw new UnsupportedOperationException("Cannot insert into an any handler. Since the payload is known, you should pass the type to the handler.");
	}

	@Override
	public Payload extract( IFilter template, int side, boolean doExtract )
	{
		for(IPayloadHandler handler : mHandlers)
		{
			Payload payload = handler.extract(template, side, doExtract);
			if(payload != null)
				return payload;
		}
		return null;
	}

	@Override
	public Payload extract( IFilter template, int side, int count, SizeMode mode, boolean doExtract )
	{
		for(IPayloadHandler handler : mHandlers)
		{
			Payload payload = handler.extract(template, side, count, mode, doExtract);
			if(payload != null)
				return payload;
		}
		return null;
	}

}
