package schmoller.tubes;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.FluidStack;
import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.interfaces.IFilter;

public class FluidFilter implements IFilter
{
	private FluidStack mTemplate;
	@SideOnly(Side.CLIENT)
	private AdvRender mRender;
	
	public FluidFilter(FluidStack template)
	{
		assert(template != null);
		
		mTemplate = template;
	}
	
	@Override
	public String getType()
	{
		return "fluid";
	}
	
	public FluidStack getFluid()
	{
		return mTemplate;
	}
	
	@Override
	public boolean matches( Payload payload, SizeMode mode )
	{
		if(!(payload instanceof FluidPayload) || !mTemplate.isFluidEqual((FluidStack)payload.get()))
			return false;
		
		FluidStack other = (FluidStack)payload.get();
		
		switch(mode)
		{
		case Exact:
			return other.amount == mTemplate.amount;
		case GreaterEqual:
			return other.amount >= mTemplate.amount;
		case LessEqual:
			return other.amount <= mTemplate.amount;
		case Max:
			return true;
		}
		
		return false;
	}

	@Override
	public boolean matches( TubeItem item, SizeMode mode )
	{
		return matches(item.item, mode);
	}

	@Override
	public void increase( boolean useMax, boolean shift )
	{
		mTemplate.amount += (shift ? 250 : 125);
		if(useMax && mTemplate.amount > getMax())
			mTemplate.amount = getMax();
	}

	@Override
	public void decrease( boolean shift )
	{
		mTemplate.amount -= (shift ? 250 : 125);
		if(mTemplate.amount < 0)
			mTemplate.amount = 0;
	}

	@Override
	public int getMax()
	{
		if(mTemplate.getFluid().isGaseous(mTemplate))
			return 8000;
		return 1000;
	}

	@Override
	public int size()
	{
		return mTemplate.amount;
	}
	
	@SideOnly(Side.CLIENT)
	private void renderBlock(Icon icon, int x, int y, int sprite)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().getTextureManager().getResourceLocation(sprite));
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(x - 2), (float)(y + 3), -3.0F + 200);
        GL11.glScalef(10.0F, 10.0F, 10.0F);
        GL11.glTranslatef(1.0F, 0.5F, 1.0F);
        GL11.glScalef(1.0F, 1.0F, -1.0F);
        GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);

        mRender.resetLighting();
        mRender.setLocalLights(1, 1, 1, 1, 1, 1);
        mRender.enableNormals = true;

        Tessellator tes = Tessellator.instance;
        
        tes.startDrawingQuads();
        mRender.setIcon(icon);
        mRender.drawBox(63, 0, 0, 0, 1, 0.8f, 1);
        
        tes.draw();

        GL11.glPopMatrix();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFilter( int x, int y )
	{
		if(mRender == null)
			mRender = new AdvRender();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        Icon icon = mTemplate.getFluid().getIcon(mTemplate);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().getTextureManager().getResourceLocation(mTemplate.getFluid().getSpriteNumber()));
        
        renderBlock(icon, x, y, mTemplate.getFluid().getSpriteNumber());
        
        String text = String.valueOf(mTemplate.amount);
        int fwidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        
        GL11.glPushMatrix();
        
        GL11.glTranslated(x + 17 - fwidth, y + 9, 0);
        if(fwidth > 16)
        {
        	GL11.glTranslatef(fwidth / 2 - 1, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f - 1, 0);
        	GL11.glScalef(0.5f, 0.5f, 0.5f);
        }
        
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, 0, 0, 16777215);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        GL11.glPopMatrix();
	}

	@Override
	public void write( NBTTagCompound tag )
	{
		mTemplate.writeToNBT(tag);
	}
	
	@Override
	public void write( MCDataOutput output )
	{
		output.writeFluidStack(mTemplate);
	}

	public static FluidFilter from(NBTTagCompound tag)
	{
		return new FluidFilter(FluidStack.loadFluidStackFromNBT(tag));
	}
	
	public static FluidFilter from(MCDataInput input)
	{
		return new FluidFilter(input.readFluidStack());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip( List<String> current )
	{
		return Arrays.asList("\u00a7" + Integer.toHexString(mTemplate.getFluid().getRarity(mTemplate).rarityColor) + mTemplate.getFluid().getLocalizedName());
	}
}
