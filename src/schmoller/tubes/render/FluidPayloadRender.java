package schmoller.tubes.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.FluidStack;
import schmoller.tubes.AdvRender;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.client.IPayloadRender;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.definitions.TypeNormalTube;

public class FluidPayloadRender implements IPayloadRender
{
	private AdvRender mRender = new AdvRender();
	
	@Override
	public void render( Payload payload, int color, double x, double y, double z, int direction, float progress )
	{
		FluidStack fluid = (FluidStack)payload.get();
		
		Icon icon = fluid.getFluid().getIcon(fluid);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().getTextureManager().getResourceLocation(fluid.getFluid().getSpriteNumber()));
        
        mRender.resetLighting();
        mRender.setLocalLights(0.5f, 1, 0.8f, 0.8f, 0.6f, 0.6f);
        mRender.resetTransform();
        mRender.translate(-0.5f, -0.4f, -0.5f);
        mRender.scale(0.3f, 0.3f, 0.3f);
        mRender.translate((float)x, (float)y, (float)z);

        Tessellator tes = Tessellator.instance;
        
        tes.startDrawingQuads();
        mRender.setIcon(icon);
        mRender.drawBox(63, 0, 0, 0, 1, 0.8f, 1);
        
        tes.draw();
        
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
