package schmoller.tubes.render;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import schmoller.tubes.AdvRender;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.client.IPayloadRender;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.definitions.TypeNormalTube;

public class ItemPayloadRender implements IPayloadRender
{
	public static ResourceLocation itemGlint = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
	private RenderBlocks itemRenderBlocks = new RenderBlocks();
	private AdvRender mAdv = new AdvRender();
	private EntityItem mDummy = new EntityItem(null);
	
	private TextureManager mRender;
	private Random mRand = new Random();
	
	@Override
	public void render( Payload payload, int color, double x, double y, double z, int direction, float progress )
	{
		renderItemStack((ItemStack)payload.get(), x, y, z);
		
		if(color != -1)
		{
			mRender.bindTexture(TextureMap.locationBlocksTexture);
			
			mAdv.enableNormals = false;
			mAdv.resetLighting(15728880);
			mAdv.resetColor();
			mAdv.resetAO();
			mAdv.setLocalLights(1f, 1f, 1f, 1f, 1f, 1f);
			mAdv.resetTransform();

			mAdv.setColorRGB(CommonHelper.getDyeColor(color));
			
			mAdv.translate(-0.5f, -0.5f, -0.5f);
			
			mAdv.scale(0.4f, 0.4f, 0.4f);
			mAdv.translate((float)x, (float)y, (float)z);
			mAdv.setIcon(TypeNormalTube.itemBorder);
			
			Tessellator tes = Tessellator.instance;
			tes.startDrawingQuads();
			
			
			mAdv.drawBox(63, 0f, 0f, 0f, 1f, 1f, 1f);
			tes.draw();
		}
	}

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
        
        if(item.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(Block.blocksList[item.itemID].getRenderType()))
        {
        	mRand.setSeed(1234);
        	for (int i = 0; i < count; ++i)
            {
                GL11.glPushMatrix();

                GL11.glScalef(0.8f, 0.8f, 0.8f);
                if (i > 0)
                {
                    float xx = (mRand.nextFloat() * 2.0F - 1.0F) * 0.1F;
                    float yy = (mRand.nextFloat() * 2.0F - 1.0F) * 0.1F;
                    float zz = (mRand.nextFloat() * 2.0F - 1.0F) * 0.1F;
                    GL11.glTranslatef(xx, yy, zz);
                }

                renderSingleItem(item, block);
                GL11.glPopMatrix();
            }
        }
        else
        {
	        GL11.glTranslatef(0, 0, (0.04f * (count - 1)) / -2f);
	        for(int i = 0; i < count; ++i)
	        {
	        	GL11.glPushMatrix();
	        	if(i != 0)
	        	{
	        		GL11.glTranslatef(0, 0, 0.04f * i);
	        	}
	        	renderSingleItem(item, block);
	        	GL11.glPopMatrix();
	        }
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

			mRender.bindTexture(TextureMap.locationBlocksTexture);
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

                mRender.bindTexture(TextureMap.locationItemsTexture);

                for (int pass = 0; pass < item.getItem().getRenderPasses(item.getItemDamage()); ++pass)
                {
                	GL11.glPushMatrix();
                    Icon icon = item.getItem().getIcon(item, pass);

                    int color = Item.itemsList[item.itemID].getColorFromItemStack(item, pass);
                    float red = (float)(color >> 16 & 255) / 255.0F;
                    float green = (float)(color >> 8 & 255) / 255.0F;
                    float blue = (float)(color & 255) / 255.0F;
                    GL11.glColor4f(red, green, blue, 1.0F);
                    renderDroppedItem(item, icon, red, green, blue);
                    GL11.glPopMatrix();
                }
            }
            else
            {
                GL11.glScalef(0.5F, 0.5F, 0.5F);

                Icon icon = item.getIconIndex();

                if (item.getItemSpriteNumber() == 0)
                	mRender.bindTexture(TextureMap.locationBlocksTexture);
                else
                	mRender.bindTexture(TextureMap.locationItemsTexture);

                int color = Item.itemsList[item.itemID].getColorFromItemStack(item, 0);
                float red = (float)(color >> 16 & 255) / 255.0F;
                float green = (float)(color >> 8 & 255) / 255.0F;
                float blue = (float)(color & 255) / 255.0F;
                renderDroppedItem(item, icon, red, green, blue);
            }
		}
	}
	private void renderDroppedItem(ItemStack item, Icon icon, float red, float green, float blue)
	{
		if (icon == null)
		{
			ResourceLocation resourcelocation = mRender.getResourceLocation(item.getItemSpriteNumber());
            icon = ((TextureMap)mRender.getTexture(resourcelocation)).getAtlasSprite("missingno");
		}
        
		float minU = icon.getMinU();
		float minV = icon.getMinV();
		float maxU = icon.getMaxU();
		float maxV = icon.getMaxV();
		
		if (RenderManager.instance.options.fancyGraphics)
        {
			GL11.glTranslated(-0.5, -0.5, 0.0);
			
			mRender.bindTexture(mRender.getResourceLocation(item.getItemSpriteNumber()));

            GL11.glColor4f(red, green, blue, 1.0F);
            ItemRenderer.renderItemIn2D(Tessellator.instance, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), 0.0625f);

            if (item != null && item.hasEffect(0))
            {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                mRender.bindTexture(itemGlint);
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

        mRender.bindTexture(mRender.getResourceLocation(item.getItemSpriteNumber()));
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
	
	private byte getMiniBlockCount(ItemStack stack)
    {
        byte ret = 1;
        if (stack.stackSize > 1 ) ret = 2;
        if (stack.stackSize > 5 ) ret = 3;
        if (stack.stackSize > 20) ret = 4;
        if (stack.stackSize > 40) ret = 5;
        return ret;
    }
}
