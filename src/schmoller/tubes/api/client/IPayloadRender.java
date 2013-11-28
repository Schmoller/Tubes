package schmoller.tubes.api.client;

import schmoller.tubes.api.Payload;

public interface IPayloadRender
{
	public void render(Payload payload, int color, double x, double y, double z, int direction, float progress);
}
