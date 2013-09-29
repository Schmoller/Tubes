package schmoller.tubes.render;

import codechicken.core.vec.Vector3;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.AdvRender;
import schmoller.tubes.TubeItem;
import schmoller.tubes.parts.BaseTubePart;

public class RenderTubePart
{
	private AdvRender mRender = new AdvRender();
	private EntityItem mDummy = new EntityItem(null);
	private RenderItem mRenderer = new RenderItem();
	
	private Icon mCenterIcon;
	private Icon mStraightIcon;
	
	private static RenderTubePart inst;
	
	public static RenderTubePart instance()
	{
		if(inst == null)
			inst = new RenderTubePart();
		
		return inst;
	}
	
	private RenderTubePart()
	{
		mRenderer.setRenderManager(RenderManager.instance);
		mDummy.hoverStart = 0;
	}
	
	public void setIcons(Icon center, Icon straight)
	{
		mCenterIcon = center;
		mStraightIcon = straight;
	}
	
	public void renderStatic(BaseTubePart part)
	{
		int connections = part.getConnections();
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(part.world(), part.x(), part.y(), part.z());
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(part.x(), part.y(), part.z());
		
		if(connections == 3 || connections == 12 || connections == 48)
			renderStraight(connections);
		else
		{
			renderCore(connections);
			renderConnections(connections);
		}
	}
	
	private void renderStraight(int connections)
	{
		mRender.setIcon(mStraightIcon);
		
		if(connections == 3)
		{
			mRender.drawBox(60, 0.25f, 0.0f, 0.25f, 0.75f, 1.0f, 0.75f);
		}
		else if(connections == 12)
		{
			mRender.setTextureRotation(0, 0, 1, 1, 1, 1);
			mRender.drawBox(51, 0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 1.0f);
		}
		else
		{
			mRender.setTextureRotation(1);
			mRender.drawBox(15, 0.0f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
		}
	}
	
	private void renderCore(int connections)
	{
		mRender.setIcon(mCenterIcon);
		mRender.drawBox((~connections) & 63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}
	
	private void renderConnections(int connections)
	{
		mRender.setIcon(mStraightIcon);
		
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				mRender.resetTextureRotation();
				switch(i)
				{
				case 0: // Down
					mRender.setupBox(0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
					break;
				case 1: // Up
					mRender.setupBox(0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
					break;
				case 2: // North
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.setupBox(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
					break;
				case 3: // South
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.setupBox(0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
					break;
				case 4: // West
					mRender.setTextureRotation(1);
					mRender.setupBox(0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
					break;
				case 5: // East
					mRender.setTextureRotation(1);
					mRender.setupBox(0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
					break;
				}
				
				mRender.drawFaces(63 - (1 << (i ^ 1))  - (1 << i));
				
				switch(i)
				{
				case 0: // Down
					mRender.setupBox(0.75f, 0.25f, 0.75f, 0.25f, 0.0f, 0.25f);
					break;
				case 1: // Up
					mRender.setupBox(0.75f, 1.0f, 0.75f, 0.25f, 0.75f, 0.25f);
					break;
				case 2: // North
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.setupBox(0.75f, 0.75f, 0.25f, 0.25f, 0.25f, 0.0f);
					break;
				case 3: // South
					mRender.setTextureRotation(0, 0, 0, 0, 1, 1);
					mRender.setupBox( 0.75f, 0.75f, 1.0f, 0.25f, 0.25f, 0.75f);
					break;
				case 4: // West
					mRender.setTextureRotation(1);
					mRender.setupBox(0.25f, 0.75f, 0.75f, 0.0f, 0.25f, 0.25f);
					break;
				case 5: // East
					mRender.setTextureRotation(1);
					mRender.setupBox(1.0f, 0.75f, 0.75f, 0.75f, 0.25f, 0.25f);
					break;
				}
				
				//mRender.drawFaces(63 - (1 << (i ^ 1))  - (1 << i));
			}
		}
	}
	
	public void renderDynamic(BaseTubePart part, Vector3 pos)
	{
		for(TubeItem item : part.getItems())
		{
			mDummy.setEntityItemStack(item.item);
			
			ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
			mRenderer.doRenderItem(mDummy, pos.x + 0.5 + (item.progress - 0.5) * dir.offsetX, pos.y + 0.4 + (item.progress - 0.5) * dir.offsetY, pos.z + 0.5 + (item.progress - 0.5) * dir.offsetZ, 0, 0);
		}
	}
}
