package schmoller.tubes.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

public class FluidPayload extends Payload
{
	public FluidStack fluid;
	
	// Rendering aids
	public int lastDirection;
	public float lastProgress;
	
	public static final int maxLastCount = 4;
	
	public int lastCount = 0;
	public double[] lastX = new double[maxLastCount];
	public double[] lastY = new double[maxLastCount];
	public double[] lastZ = new double[maxLastCount];
	
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
		super.write(tag);
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
		super.write(output);
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
}
