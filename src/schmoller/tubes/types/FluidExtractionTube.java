package schmoller.tubes.types;

import schmoller.tubes.AnyFilter;
import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.InteractionHandler;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.interfaces.IPayloadHandler;

public class FluidExtractionTube extends ExtractionTube
{
	public FluidExtractionTube()
	{
		super("fluidExtraction");
	}
	
	@Override
	protected Payload doExtract( int x, int y, int z, int side )
	{
		IPayloadHandler handler = InteractionHandler.getHandler(FluidPayload.class, world(), x, y, z);
		if(handler != null)
			return handler.extract(new AnyFilter(0), side, true);

		return null;
	}
}
