package schmoller.tubes.inventory;

import java.util.ArrayList;
import java.util.Collection;
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

	@Override
	public boolean isSideAccessable( int side )
	{
		for(IPayloadHandler handler : mHandlers)
		{
			if(handler.isSideAccessable(side))
				return true;
		}
		return false;
	}
	
	@Override
	public Collection<Payload> listContents( IFilter filter, int side )
	{
		ArrayList<Payload> payloads = new ArrayList<Payload>();
		for(IPayloadHandler handler : mHandlers)
			payloads.addAll(handler.listContents(filter, side));
		
		return payloads;
	}
	
	@Override
	public Collection<Payload> listContents( int side )
	{
		ArrayList<Payload> payloads = new ArrayList<Payload>();
		for(IPayloadHandler handler : mHandlers)
			payloads.addAll(handler.listContents(side));
		
		return payloads;
	}
}
