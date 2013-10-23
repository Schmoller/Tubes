package schmoller.tubes;

import net.minecraftforge.common.ForgeDirection;

public class Position
{
	public int x;
	public int y;
	public int z;
	
	public Position() {}
	
	public Position(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Position offset(int dir, int amount)
	{
		ForgeDirection fDir = ForgeDirection.getOrientation(dir);
		x += fDir.offsetX * amount;
		y += fDir.offsetY * amount;
		z += fDir.offsetZ * amount;
		
		return this;
	}
	
	public Position copy()
	{
		return new Position(x, y, z);
	}
	
	public int hashCode()
    {
        return x * 8976890 + y * 981131 + z;
    }
	
	@Override
	public boolean equals( Object obj )
	{
		if(!(obj instanceof Position))
			return false;
		
		return x == ((Position)obj).x && y == ((Position)obj).y && z == ((Position)obj).z;
	}
	
	@Override
	public String toString()
	{
		return x + ", " + y + ", " + z;
	}
}
