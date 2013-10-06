package schmoller.tubes.render;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class CustomRenderItem
{
	private RenderBlocks itemRenderBlocks = new RenderBlocks();
	private EntityItem mDummy = new EntityItem(null);
	
	private RenderEngine mRender;
	
	public void renderItemStack(ItemStack item, double x, double y, double z)
	{
		if(mRender == null)
			mRender = FMLClientHandler.instance().getClient().renderGlobal.renderEngine;
		
		mDummy.setEntityItemStack(item);
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		Block block = null;
        if (item.itemID < Block.blocksList.length)
            block = Block.blocksList[item.itemID];
		
        int count = getMiniBlockCount(item);

        GL11.glTranslatef(0, 0, (0.04f * (count - 1)) / -2f);
        for(int i = 0; i < count; ++i)
        {
        	GL11.glPushMatrix();
        	if(i != 0)
        		GL11.glTranslatef(0, 0, 0.04f * i);
        	renderSingleItem(item, block);
        	GL11.glPopMatrix();
        }
        
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
	
	private void renderSingleItem(ItemStack item, Block block)
	{
		if(renderCustomItem(item)) {}
		else if (item.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(Block.blocksList[item.itemID].getRenderType()))
		{
			GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);

			mRender.bindTexture("/terrain.png");
			int renderType = block.getRenderType();
			float scale = (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2 ? 0.5F : 0.25F);
            
            GL11.glScalef(scale, scale, scale);

            itemRenderBlocks.renderBlockAsItem(block, item.getItemDamage(), 1.0f);
		}
		else
		{
            if (item.getItem().requiresMultipleRenderPasses())
            {
                GL11.glScalef(0.5F, 0.5F, 0.5F);

                mRender.bindTexture("/gui/items.png");

                for (int pass = 0; pass < item.getItem().getRenderPasses(item.getItemDamage()); ++pass)
                {
                    Icon icon = item.getItem().getIcon(item, pass);

                    int color = Item.itemsList[item.itemID].getColorFromItemStack(item, pass);
                    float blue = (float)(color >> 16 & 255) / 255.0F;
                    float green = (float)(color >> 8 & 255) / 255.0F;
                    float red = (float)(color & 255) / 255.0F;
                    GL11.glColor4f(red, green, blue, 1.0F);
                    renderDroppedItem(item, icon, red, green, blue);
                }
            }
            else
            {
                GL11.glScalef(0.5F, 0.5F, 0.5F);

                Icon icon = item.getIconIndex();

                if (item.getItemSpriteNumber() == 0)
                	mRender.bindTexture("/terrain.png");
                else
                	mRender.bindTexture("/gui/items.png");

                int color = Item.itemsList[item.itemID].getColorFromItemStack(item, 0);
                float blue = (float)(color >> 16 & 255) / 255.0F;
                float green = (float)(color >> 8 & 255) / 255.0F;
                float red = (float)(color & 255) / 255.0F;
                renderDroppedItem(item, icon, red, green, blue);
            }
		}
	}
	private void renderDroppedItem(ItemStack item, Icon icon, float red, float green, float blue)
	{
		if (icon == null)
            icon = mRender.getMissingIcon(item.getItemSpriteNumber());
        
		float minU = icon.getMinU();
		float minV = icon.getMinV();
		float maxU = icon.getMaxU();
		float maxV = icon.getMaxV();
		
		if (RenderManager.instance.options.fancyGraphics)
        {
			GL11.glTranslated(-0.5, -0.5, 0.0);
			
            if (item.getItemSpriteNumber() == 0)
                mRender.bindTexture("/terrain.png");
            else
            	mRender.bindTexture("/gui/items.png");

            GL11.glColor4f(red, green, blue, 1.0F);
            ItemRenderer.renderItemIn2D(Tessellator.instance, maxU, minV, minU, maxV, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625f);

            if (item != null && item.hasEffect())
            {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                mRender.bindTexture("%blur%/misc/glint.png");
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f13 = 0.76F;
                GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f14 = 0.125F;
                GL11.glScalef(f14, f14, f14);
                float f15 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f15, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(Tessellator.instance, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, 0.0625f);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f14, f14, f14);
                f15 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f15, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(Tessellator.instance, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, 0.0625f);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }
        }
        else
        {
            GL11.glColor4f(red, green, blue, 1.0F);
            Tessellator.instance.startDrawingQuads();
            Tessellator.instance.setNormal(0.0F, 1.0F, 0.0F);
            Tessellator.instance.addVertexWithUV(-0.5, -0.5, 0.0D, minU, maxV);
            Tessellator.instance.addVertexWithUV(0.5, -0.5, 0.0D, maxU, maxV);
            Tessellator.instance.addVertexWithUV(0.5, 0.5, 0.0D, maxU, minV);
            Tessellator.instance.addVertexWithUV(-0.5, 0.5, 0.0D, minU, minV);
            Tessellator.instance.draw();
        }
	}
	
	private boolean renderCustomItem(ItemStack item)
	{
		IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ENTITY);
        if (customRenderer == null)
            return false;

        boolean is3D = customRenderer.shouldUseRenderHelper(ENTITY, item, BLOCK_3D);

        mRender.bindTexture(item.getItemSpriteNumber() == 0 ? "/terrain.png" : "/gui/items.png");
        Block block = (item.itemID < Block.blocksList.length ? Block.blocksList[item.itemID] : null);
        if (is3D || (block != null && RenderBlocks.renderItemIn3d(block.getRenderType())))
        {
            int renderType = (block != null ? block.getRenderType() : 1);
            float scale = (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2 ? 0.5F : 0.25F);

            GL11.glScalef(scale, scale, scale);
            
            customRenderer.renderItem(ENTITY, item, itemRenderBlocks, mDummy);
        }
        else
        {
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            customRenderer.renderItem(ENTITY, item, itemRenderBlocks, mDummy);
        }
        return true;
	}
	
	public byte getMiniBlockCount(ItemStack stack)
    {
        byte ret = 1;
        if (stack.stackSize > 1 ) ret = 2;
        if (stack.stackSize > 5 ) ret = 3;
        if (stack.stackSize > 20) ret = 4;
        if (stack.stackSize > 40) ret = 5;
        return ret;
    }

    /**
     * Allows for a subclass to override how many rendered items appear in a
     * "mini item 3d stack"
     * @param stack
     * @return
     */
    public byte getMiniItemCount(ItemStack stack)
    {
        byte ret = 1;
        if (stack.stackSize > 1) ret = 2;
        if (stack.stackSize > 15) ret = 3;
        if (stack.stackSize > 31) ret = 4;
        return ret;
    }
}
