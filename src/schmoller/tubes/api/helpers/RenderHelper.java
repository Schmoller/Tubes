package schmoller.tubes.api.helpers;

import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.api.PayloadRegistry;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubeRegistry;
import schmoller.tubes.api.client.ITubeRender;
import schmoller.tubes.api.interfaces.ITube;

import org.lwjgl.opengl.GL11;

public class RenderHelper
{
	public static void initialize()
	{
	}
	
	public static void renderTubeItems(ITube tube, float partialTick)
	{
		for(TubeItem item : tube.getItems())
		{
			ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
			float progress = item.lastProgress + (item.progress - item.lastProgress) * partialTick;
			PayloadRegistry.instance().getPayloadRender(item.item.getClass()).render(item.item, item.colour, 0.5 + (progress - 0.5) * dir.offsetX, 0.5 + (progress - 0.5) * dir.offsetY, 0.5 + (progress - 0.5) * dir.offsetZ, item.direction, progress);
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
