package schmoller.tubes;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.FluidStack;
import schmoller.tubes.api.FluidPayload;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.RenderHelper;
import schmoller.tubes.api.interfaces.IFilter;

public class FluidFilter implements IFilter
{
	private FluidStack mTemplate;
	
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

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFilter( int x, int y )
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        Icon icon = mTemplate.getFluid().getIcon(mTemplate);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().getTextureManager().getResourceLocation(mTemplate.getFluid().getSpriteNumber()));
        
        RenderHelper.renderIcon(icon, x, y, 16, 16);
        
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
