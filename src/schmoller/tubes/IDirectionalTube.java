package schmoller.tubes;

public interface IDirectionalTube
{
	public int getFacing();
	
	public boolean canFaceDirection(int face);
	
	public void setFacing(int face);
}
