package schmoller.tubes.api.helpers;

import java.util.List;

import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import schmoller.tubes.ModTubes;
import schmoller.tubes.api.PayloadRegistry;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.TubeRegistry;
import schmoller.tubes.api.client.ITubeRender;
import schmoller.tubes.api.interfaces.ITube;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class RenderHelper
{
	public static void initialize()
	{
	}
	
	public static void renderTubeItems(ITube tube, int x, int y, int z, float partialTick)
	{
		EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		
		if(player.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) - 10 > ModTubes.payloadRenderDistance)
			return;
		
		for(TubeItem item : tube.getItems())
		{
			ForgeDirection dir = ForgeDirection.getOrientation(item.direction);
			float progress = (item.lastProgress + (item.progress - item.lastProgress) * partialTick);
			
			if(progress < 0.5 && item.progress >= 0.5)
				dir = ForgeDirection.getOrientation(item.lastDirection);

			progress -= 0.5f;
			
			double xx = x + 0.5 + progress * dir.offsetX;
			double yy = y + 0.5 + progress * dir.offsetY;
			double zz = z + 0.5 + progress * dir.offsetZ;
			
			if(player.getDistanceSq(xx, yy, zz) < ModTubes.payloadRenderDistance)
				PayloadRegistry.instance().getPayloadRender(item.item.getClass()).render(item.item, item.colour, xx, yy, zz, item.direction, progress + 0.5f);
		}
	}

	public static void renderDynamic( ITube tube, TubeDefinition definition, Vector3 pos, float frameTime )
	{
		ITubeRender render = TubeRegistry.instance().getRender(definition);
		GL11.glPushMatrix();
		//GL11.glTranslated(pos.x, pos.y, pos.z);
		GL11.glTranslated(pos.x - tube.x(), pos.y - tube.y(), pos.z - tube.z());
		
		render.renderDynamic(definition, tube, tube.world(), tube.x(), tube.y(), tube.z(), frameTime);
		
		GL11.glPopMatrix();
	}

	public static boolean renderStatic( ITube tube, TubeDefinition definition )
	{
		ITubeRender render = TubeRegistry.instance().getRender(definition);
		render.renderStatic(definition, tube, tube.world(), tube.x(), tube.y(), tube.z());
		return true;
	}
	
	public static void renderItem( ItemStack item, TubeDefinition definition )
	{
		ITubeRender render = TubeRegistry.instance().getRender(definition);
		render.renderItem(definition, item);
	}
	
	public static void renderIcon( IIcon icon, int x, int y, int width, int height)
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
	
	public static void renderPlainRect(int x1, int y1, int x2, int y2, int color)
    {
        int temp;

        if (x1 < x2)
        {
            temp = x1;
            x1 = x2;
            x2 = temp;
        }

        if (y1 < y2)
        {
            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        
        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(red, green, blue, alpha);
        tessellator.startDrawingQuads();
        tessellator.addVertex(x1, y2, 0.0D);
        tessellator.addVertex(x2, y2, 0.0D);
        tessellator.addVertex(x2, y1, 0.0D);
        tessellator.addVertex(x1, y1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
	
	public static void wrapTooltip(List<String> tooltip)
	{
		final int min = 18;
		final int max = 24;
		for(int i = 0; i < tooltip.size(); ++i)
		{
			String line = tooltip.get(i);
			if(line != null && line.length() > min)
			{
				int end = line.indexOf(' ', min);
				if(end == -1)
					end = line.length();
				
				if(end > max)
				{
					end = line.lastIndexOf(' ', min);
					if(end == -1)
						end = max;
				}
				else if(line.length() < max)
					continue;
				
				tooltip.set(i, line.substring(0, end).trim());
				String remain = line.substring(end).trim();
				if(!remain.isEmpty())
					tooltip.add(i+1, remain);
			}
		}
	}
}
