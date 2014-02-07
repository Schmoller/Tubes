package schmoller.tubes.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import schmoller.tubes.AdvRender;
import schmoller.tubes.ModTubes;
import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.client.IPayloadRender;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.definitions.TypeNormalTube;

public class FluidPayloadRender implements IPayloadRender
{
	private AdvRender mRender = new AdvRender();
	
	@Override
	public void render( Payload rawPayload, int color, double x, double y, double z, int direction, float progress )
	{
		FluidPayload payload = (FluidPayload)rawPayload;
		FluidStack fluid = payload.fluid;
		
		// Invalidate bad data
		int tickNo = ModTubes.instance.getCurrentTick();
		if(payload.tickNo < tickNo - 1)
		{
			payload.coordX = (float)x;
			payload.coordY = (float)y;
			payload.coordZ = (float)z;
			payload.lastDirection = direction;
			payload.lastProgress = progress;
		}
		
		payload.tickNo = tickNo;
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		Icon icon = fluid.getFluid().getIcon(fluid);
        
		mRender.enableLighting = false;
        mRender.enableNormals = true;
        mRender.resetTransform();
        mRender.resetColor();
         
        Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().getTextureManager().getResourceLocation(fluid.getFluid().getSpriteNumber()));
        mRender.setIcon(icon);
        mRender.setAbsoluteTextureCoords(false);
        
        float scale = (fluid.amount / 1000f) * 0.7f + 0.3f;
        float baseSize = 0.05f * scale;
        float spacing = 0.1f * scale;
        
        Tessellator tes = Tessellator.instance;
        
        tes.startDrawingQuads();
        
        mRender.pushTransform();
        
        mRender.translate((float)x, (float)y, (float)z);
        
        mRender.drawBox(63, -baseSize, -baseSize, -baseSize, baseSize, baseSize, baseSize);
        
        
        mRender.popTransform();
        
        float xx = (float)x;
        float yy = (float)y;
        float zz = (float)z;
        
        int index = 0;
        int segmentCount = 4;
        for(int i = 0; i < segmentCount; ++i)
        {
        	float size = 1f - (index / (float)(segmentCount-1));
        	size *= 2;
        	mRender.pushTransform();
        	mRender.scale(size, size, size);
        	
        	ForgeDirection dir = ForgeDirection.getOrientation(direction ^ 1);
            xx += spacing * dir.offsetX;
            yy += spacing * dir.offsetY;
            zz += spacing * dir.offsetZ;
            
            boolean backSide = false;
            switch(direction)
			{
			case 0:
				backSide = (yy > payload.coordY);
				break;
			case 1:
				backSide = (yy < payload.coordY);
				break;
			case 2:
				backSide = (zz > payload.coordZ);
				break;
			case 3:
				backSide = (zz < payload.coordZ);
				break;
			case 4:
				backSide = (xx > payload.coordX);
				break;
			case 5:
				backSide = (xx < payload.coordX);
				break;
			}
        	
        	if(backSide && payload.lastDirection != direction)
        	{
        		float difference = 0;
        		float xxx = xx;
        		float yyy = yy;
        		float zzz = zz;
        		
    			switch(direction)
    			{
    			case 0:
    			case 1:
    				difference = Math.abs(yy - payload.coordY);
    				yyy = payload.coordY;
    				break;
    			case 2:
    			case 3:
    				difference = Math.abs(zz - payload.coordZ);
    				zzz = payload.coordZ;
    				break;
    			case 4:
    			case 5:
    				difference = Math.abs(xx - payload.coordX);
    				xxx = payload.coordX;
    				break;
    			}
        		
    			switch(payload.lastDirection)
    			{
    			case 0:
    				mRender.translate(xxx, yyy + difference, zzz);
    				break;
    			case 1:
    				mRender.translate(xxx, yyy - difference, zzz);
    				break;
    			case 2:
    				mRender.translate(xxx, yyy, zzz + difference);
    				break;
    			case 3:
    				mRender.translate(xxx, yyy, zzz - difference);
    				break;
    			case 4:
    				mRender.translate(xxx + difference, yyy, zzz);
    				break;
    			case 5:
    				mRender.translate(xxx - difference, yyy, zzz);
    				break;
    			}
        	}
        	else
        	{
	            mRender.translate(xx, yy, zz);
        	}
            
        	mRender.drawBox(63, -baseSize, -baseSize, -baseSize, baseSize, baseSize, baseSize);
            
            mRender.popTransform();
            
            ++index;
        }
        
        tes.draw();
        
        if(payload.lastProgress >= 0.5 && progress < 0.5)
        {
        	payload.lastDirection = direction;
        	payload.coordX = (float)x;
        	payload.coordY = (float)y;
        	payload.coordZ = (float)z;
        }
        
        payload.lastProgress = progress;
		
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
			
	        ForgeDirection dir = ForgeDirection.getOrientation(direction ^ 1);
            xx = (float)x + spacing * dir.offsetX;
            yy = (float)y + spacing * dir.offsetY;
            zz = (float)z + spacing * dir.offsetZ;
            
            boolean backSide = false;
            switch(direction)
			{
			case 0:
				backSide = (yy > payload.coordY);
				break;
			case 1:
				backSide = (yy < payload.coordY);
				break;
			case 2:
				backSide = (zz > payload.coordZ);
				break;
			case 3:
				backSide = (zz < payload.coordZ);
				break;
			case 4:
				backSide = (xx > payload.coordX);
				break;
			case 5:
				backSide = (xx < payload.coordX);
				break;
			}
            
            if(backSide && payload.lastDirection != direction)
        	{
        		float difference = 0;
        		float xxx = xx;
        		float yyy = yy;
        		float zzz = zz;
        		
    			switch(direction)
    			{
    			case 0:
    			case 1:
    				difference = Math.abs(yy - payload.coordY);
    				yyy = payload.coordY;
    				break;
    			case 2:
    			case 3:
    				difference = Math.abs(zz - payload.coordZ);
    				zzz = payload.coordZ;
    				break;
    			case 4:
    			case 5:
    				difference = Math.abs(xx - payload.coordX);
    				xxx = payload.coordX;
    				break;
    			}
        		
    			switch(payload.lastDirection)
    			{
    			case 0:
    				mRender.translate(xxx, yyy + difference, zzz);
    				break;
    			case 1:
    				mRender.translate(xxx, yyy - difference, zzz);
    				break;
    			case 2:
    				mRender.translate(xxx, yyy, zzz + difference);
    				break;
    			case 3:
    				mRender.translate(xxx, yyy, zzz - difference);
    				break;
    			case 4:
    				mRender.translate(xxx + difference, yyy, zzz);
    				break;
    			case 5:
    				mRender.translate(xxx - difference, yyy, zzz);
    				break;
    			}
        	}
        	else
	            mRender.translate(xx, yy, zz);
			
			mRender.setIcon(TypeNormalTube.itemBorder);
			
			tes.startDrawingQuads();
			mRender.drawBox(63, 0.3f, 0.3f, 0.3f, 0.7f, 0.7f, 0.7f);
			tes.draw();
		}
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_LIGHTING);
	}

}
