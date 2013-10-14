package schmoller.tubes;

public interface ITubeOverflowDestination
{
	public boolean hasOverflow();
	public void addToOverflow(TubeItem item);
}
