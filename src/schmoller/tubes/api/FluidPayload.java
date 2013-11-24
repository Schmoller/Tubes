package schmoller.tubes.api;

import schmoller.tubes.api.client.IPayloadRender;
import schmoller.tubes.render.FluidPayloadRender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FluidPayload extends Payload
{
	@SideOnly(Side.CLIENT)
	private static FluidPayloadRender mRender;
	
	public FluidStack fluid;
	
	public FluidPayload() {}
	public FluidPayload(FluidStack fluid)
	{
		this.fluid = fluid;
	}
	
	@Override
	public Object get()
	{
		return fluid;
	}

	@Override
	public void read( NBTTagCompound tag )
	{
		fluid = FluidStack.loadFluidStackFromNBT(tag);
	}

	@Override
	public void write( NBTTagCompound tag )
	{
		tag.setInteger("Type", 1);
		fluid.writeToNBT(tag);
	}

	@Override
	public void read( MCDataInput input )
	{
		fluid = input.readFluidStack();
	}

	@Override
	public void write( MCDataOutput output )
	{
		output.writeByte(1);
		output.writeFluidStack(fluid);
	}

	@Override
	public FluidPayload copy()
	{
		return new FluidPayload(fluid.copy());
	}
	
	@Override
	public boolean isPayloadEqual( Payload other )
	{
		if(!isPayloadTypeEqual(other))
			return false;
		
		return fluid.amount == ((FluidStack)other.get()).amount;
	}
	
	@Override
	public boolean isPayloadTypeEqual( Payload other )
	{
		if(!(other instanceof FluidPayload))
			return false;
		
		return fluid.isFluidEqual((FluidStack)other.get());
	}
	
	@Override
	public int size()
	{
		return fluid.amount;
	}
	
	@Override
	public void setSize( int size )
	{
		fluid.amount = size;
	}
	
	@Override
	public int maxSize()
	{
		return 1000;
	}
	
	@Override
	public String toString()
	{
		return fluid.toString();
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public IPayloadRender getRenderer()
	{
		if(mRender == null)
			mRender = new FluidPayloadRender();
		
		return mRender;
	}
}
