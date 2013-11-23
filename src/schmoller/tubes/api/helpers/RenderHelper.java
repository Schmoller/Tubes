package schmoller.tubes.api.helpers;

import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubeRegistry;
import schmoller.tubes.api.client.CustomRenderItem;
import schmoller.tubes.api.client.ITubeRender;
import schmoller.tubes.api.interfaces.ITube;

import org.lwjgl.opengl.GL11;

public class RenderHelper
{
	private static CustomRenderItem mRenderer = new CustomRenderItem();
	
	public static void initialize()
	{
	}
	
	public static void renderTubeItems(ITube tube)
	{
		for(TubeItem item : tube.getItems())
		{
			ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
			mRenderer.renderTubeItem(item, 0.5 + (item.progress - 0.5) * dir.offsetX, 0.5 + (item.progress - 0.5) * dir.offsetY, 0.5 + (item.progress - 0.5) * dir.offsetZ);
		}
	}

	public static void renderDynamic( ITube tube, TubeDefinition definition, Vector3 pos, float frameTime )
	{
		ITubeRender render = TubeRegistry.instance().getRender(definition);
		GL11.glPushMatrix();
		GL11.glTranslated(pos.x, pos.y, pos.z);
		
		render.renderDynamic(definition, tube, tube.world(), tube.x(), tube.y(), tube.z(), frameTime);
		
		GL11.glPopMatrix();
	}

	public static void renderStatic( ITube tube, TubeDefinition definition )
	{
		ITubeRender render = TubeRegistry.instance().getRender(definition);
		render.renderStatic(definition, tube, tube.world(), tube.x(), tube.y(), tube.z());
	}
	
	public static void renderItem( ItemStack item, TubeDefinition definition )
	{
		ITubeRender render = TubeRegistry.instance().getRender(definition);
		render.renderItem(definition, item);
	}
	
	public static void renderIcon( Icon icon, int x, int y, int width, int height)
	{
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 100, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y + height, 100, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x + width, y, 100, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y, 100, icon.getMinU(), icon.getMinV());
        tessellator.draw();
	}
	
	public static void renderRect( int x, int y, int width, int height, float u, float v, float tW, float tH)
	{
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 100, u, v + tH);
        tessellator.addVertexWithUV(x + width, y + height, 100, u + tW, v + tH);
        tessellator.addVertexWithUV(x + width, y, 100, u + tW, v);
        tessellator.addVertexWithUV(x, y, 100, u, v);
        tessellator.draw();
	}
}
