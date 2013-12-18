package schmoller.tubes.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import schmoller.tubes.AdvRender;
import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.client.IPayloadRender;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.definitions.TypeNormalTube;

public class FluidPayloadRender implements IPayloadRender
{
	private AdvRender mRender = new AdvRender();
	
	private static ResourceLocation mBlobSprite = new ResourceLocation("tubes", "textures/models/liquidBlob.png");
	
	@Override
	public void render( Payload rawPayload, int color, double x, double y, double z, int direction, float progress )
	{
		// TODO: Still really unsure about this one
		FluidPayload payload = (FluidPayload)rawPayload;
		FluidStack fluid = payload.fluid;
		
		
		GL11.glDisable(GL11.GL_LIGHTING);
		
		Icon icon = fluid.getFluid().getIcon(fluid);
        
		mRender.resetLighting();
        mRender.setLocalLights(0.5f, 1, 0.8f, 0.8f, 0.6f, 0.6f);
        mRender.resetTransform();
         
        Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().getTextureManager().getResourceLocation(fluid.getFluid().getSpriteNumber()));
        mRender.setIcon(icon);
        mRender.setAbsoluteTextureCoords(false);
        
        Tessellator tes = Tessellator.instance;
        
        tes.startDrawingQuads();
        
        double cx = Math.floor(x / 0.1) * 0.1;
        double cy = Math.floor(y / 0.1) * 0.1;
        double cz = Math.floor(z / 0.1) * 0.1;
        
        mRender.pushTransform();
        
        mRender.translate((float)cx, (float)cy, (float)cz);
        mRender.drawBox(63, -0.05f, -0.05f, -0.05f, 0.05f, 0.05f, 0.05f);
        
        mRender.popTransform();
        
        int index = 0;
        for(int i = 0; i < payload.lastCount; ++i)
        {
        	if(payload.lastX[i] == cx && payload.lastY[i] == cy && payload.lastZ[i] == cz)
        		continue;
        	
        	float size = 1f - (index / (float)(FluidPayload.maxLastCount-1));
        	size *= 2;
        	mRender.pushTransform();
            
        	mRender.scale(size, size, size);
            mRender.translate((float)payload.lastX[i], (float)payload.lastY[i], (float)payload.lastZ[i]);
            mRender.drawBox(63, -0.05f, -0.05f, -0.05f, 0.05f, 0.05f, 0.05f);
            
            mRender.popTransform();
            
            ++index;
        }
        
        tes.draw();
        
        if(payload.lastCount == 0 || (payload.lastX[0] != cx || payload.lastY[0] != cy || payload.lastZ[0] != cz))
        {
        	double offsetX = 0, offsetY = 0, offsetZ = 0;
        	
//        	if(payload.lastProgress >= 0.5 && progress < 0.5)
//        	{
//        		offsetX -= ForgeDirection.getOrientation(direction).offsetX;
//        		offsetY -= ForgeDirection.getOrientation(direction).offsetY;
//        		offsetZ -= ForgeDirection.getOrientation(direction).offsetZ;
//        	}
        	
        	for(int i = FluidPayload.maxLastCount - 2; i >= 0; --i)
        	{
        		payload.lastX[i + 1] = payload.lastX[i] + offsetX;
        		payload.lastY[i + 1] = payload.lastY[i] + offsetY;
        		payload.lastZ[i + 1] = payload.lastZ[i] + offsetZ;
        	}
        	
        	++payload.lastCount;
        	if(payload.lastCount > FluidPayload.maxLastCount)
        		payload.lastCount = FluidPayload.maxLastCount;
        	
        	payload.lastX[0] = cx;
        	payload.lastY[0] = cy;
        	payload.lastZ[0] = cz;
        }
        
        if(payload.lastProgress < 0.5 && progress >= 0.5)
        	payload.lastDirection = direction;
        else if(payload.lastProgress >= 0.5 && progress < 0.5)
        	payload.lastDirection = direction;
        
        payload.lastProgress = progress;
		
        GL11.glEnable(GL11.GL_LIGHTING);
        
//        GL11.glPushMatrix();
//        GL11.glTranslated(x, y, z);
        
        
        
//        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
//        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
//        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
//        GL11.glScalef(-0.4f, -0.4f, 0.4f);
//        
//        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glEnable(GL11.GL_STENCIL_TEST);
//        GL11.glDisable(GL11.GL_BLEND);
//        
//        GL11.glColorMask(false, false, false, false);
//        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0x00);
//        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR);
//        GL11.glStencilMask(0xFF);
//        GL11.glDepthMask(false);
//        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
//        
//        Tessellator tes = Tessellator.instance;
//        
//        GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.9f);
//        GL11.glEnable(GL11.GL_ALPHA_TEST);
//        
//
//        Minecraft.getMinecraft().getTextureManager().bindTexture(mBlobSprite);
//        tes.startDrawingQuads();
//  
//        float offset = ((System.currentTimeMillis() / 100) % 7) * (1/7f);
//        
//        tes.addVertexWithUV(-0.5, 0.5, 0, 0, offset + 1/7f);
//        tes.addVertexWithUV(0.5, 0.5, 0, 1, offset + 1/7f);
//        tes.addVertexWithUV(0.5, -0.5, 0, 1, offset);
//        tes.addVertexWithUV(-0.5, -0.5, 0, 0, offset);
//        
//        tes.draw();
//        
//        GL11.glStencilMask(0x00);
//        GL11.glDepthMask(true);
//        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
//        GL11.glColorMask(true, true, true, true);
//        
//        Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().getTextureManager().getResourceLocation(fluid.getFluid().getSpriteNumber()));
//        
//        tes.startDrawingQuads();
//        
//        tes.addVertexWithUV(-0.5, 0.5, 0, icon.getMinU(), icon.getMaxV());
//        tes.addVertexWithUV(0.5, 0.5, 0, icon.getMaxU(), icon.getMaxV());
//        tes.addVertexWithUV(0.5, -0.5, 0, icon.getMaxU(), icon.getMinV());
//        tes.addVertexWithUV(-0.5, -0.5, 0, icon.getMinU(), icon.getMinV());
//        
//        tes.draw();
//        
//        GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glDisable(GL11.GL_STENCIL_TEST);
//        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        
//        GL11.glPopMatrix();
        
//       
//
//        
//        
//        tes.startDrawingQuads();
//        mRender.setIcon(icon);
//        mRender.drawBox(63, 0, 0, 0, 1, 0.8f, 1);
//        
//        tes.draw();
        
        if(color != -1)
		{
        	Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			
			mRender.enableNormals = false;
			mRender.resetLighting(15728880);
			mRender.resetColor();
			mRender.resetAO();
			mRender.setLocalLights(1f, 1f, 1f, 1f, 1f, 1f);
			mRender.resetTransform();

			mRender.setColorRGB(CommonHelper.getDyeColor(color));
			
			mRender.translate(-0.5f, -0.5f, -0.5f);
			
			mRender.scale(0.4f, 0.4f, 0.4f);
			mRender.translate((float)x, (float)y, (float)z);
			mRender.setIcon(TypeNormalTube.itemBorder);
			
			tes.startDrawingQuads();
			
			
			mRender.drawBox(63, 0f, 0f, 0f, 1f, 1f, 1f);
			tes.draw();
		}
	}

}
