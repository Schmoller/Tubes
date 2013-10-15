package schmoller.tubes;

public interface ITubeOverflowDestination
{
	public boolean canAcceptOverflowFromSide(int side);
}
