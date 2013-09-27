package schmoller.tubes;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileTubeRenderer extends TileEntitySpecialRenderer
{
	private EntityItem mDummy = new EntityItem(null);
	private RenderItem mRenderer = new RenderItem();
	
	public TileTubeRenderer()
	{
		mRenderer.setRenderManager(RenderManager.instance);
		mDummy.hoverStart = 0;
	}
	@Override
	public void renderTileEntityAt( TileEntity tile, double x, double y, double z, float partialTick )
	{
		TileTube tube = (TileTube)tile;
		
		for(TubeItem item : tube.getItems())
		{
			mDummy.setEntityItemStack(item.item);
			
			ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
			mRenderer.doRenderItem(mDummy, x + 0.5 + (item.progress - 0.5) * dir.offsetX, y + 0.4 + (item.progress - 0.5) * dir.offsetY, z + 0.5 + (item.progress - 0.5) * dir.offsetZ, 0, 0);
		}
	}

}
