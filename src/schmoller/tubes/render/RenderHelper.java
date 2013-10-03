package schmoller.tubes.render;

import codechicken.core.vec.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import schmoller.tubes.ITube;
import schmoller.tubes.TubeItem;
import schmoller.tubes.TubeRegistry;
import schmoller.tubes.definitions.TubeDefinition;
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
			mRenderer.renderItemStack(item.item, 0.5 + (item.progress - 0.5) * dir.offsetX, 0.5 + (item.progress - 0.5) * dir.offsetY, 0.5 + (item.progress - 0.5) * dir.offsetZ);
		}
	}

	public static void renderDynamic( ITube tube, TubeDefinition definition, Vector3 pos )
	{
		ITubeRender render = TubeRegistry.instance().getRender(definition);
		GL11.glPushMatrix();
		GL11.glTranslated(pos.x, pos.y, pos.z);
		
		if(!render.renderDynamic(definition, tube, tube.world(), tube.x(), tube.y(), tube.z()))
			renderTubeItems(tube);
		
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
}
