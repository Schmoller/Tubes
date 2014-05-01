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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.RenderHelper;
import schmoller.tubes.api.interfaces.IFilter;

public class ColorFilter implements IFilter
{
	public static ResourceLocation baseTexture = new ResourceLocation("tubes", "textures/gui/iconColorPallet.png");
	public static ResourceLocation centerTexture = new ResourceLocation("tubes", "textures/gui/iconColorPalletCenter.png");
	
	private int mColor;
	
	public ColorFilter(int color)
	{
		mColor = color;
	}
	
	@Override
	public String getType()
	{
		return "color";
	}
	
	@Override
	public Class<? extends Payload> getPayloadType()
	{
		return null;
	}
	
	@Override
	public boolean matches( Payload payload, SizeMode mode )
	{
		return true;
	}

	@Override
	public boolean matches( TubeItem item, SizeMode mode )
	{
		return mColor == item.colour;
	}

	@Override
	public void increase( boolean useMax, boolean shift ) {}

	@Override
	public void decrease( boolean shift ) {}

	@Override
	public int getMax()
	{
		return 1;
	}

	@Override
	public int size()
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFilter( int x, int y )
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(baseTexture);
		RenderHelper.renderRect(x, y, 16, 16, 0, 0, 1, 1);
		
		if(mColor != -1)
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(centerTexture);
			Tessellator.instance.setColorOpaque_I(CommonHelper.getDyeColor(mColor));
			int color = CommonHelper.getDyeColor(mColor);
			
			GL11.glColor3f(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f);
			RenderHelper.renderRect(x, y, 16, 16, 0, 0, 1, 1);
			GL11.glColor3f(1, 1, 1);
		}
	}

	@Override
	public void write( NBTTagCompound tag )
	{
		tag.setInteger("Color", mColor);
	}
	
	@Override
	public void write( MCDataOutput output )
	{
		output.writeShort(mColor);
	}

	public static ColorFilter from(NBTTagCompound tag)
	{
		return new ColorFilter(tag.getInteger("Color"));
	}
	
	public static ColorFilter from(MCDataInput input)
	{
		return new ColorFilter(input.readShort());
	}
	
	@Override
	public IFilter copy()
	{
		return new ColorFilter(mColor);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip( List<String> current )
	{
		return Arrays.asList((mColor == -1 ? StatCollector.translateToLocal("gui.colors.any") : CommonHelper.getDyeName(mColor)));
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if(!(obj instanceof ColorFilter))
			return false;
		
		return mColor == ((ColorFilter)obj).mColor;
	}
}
