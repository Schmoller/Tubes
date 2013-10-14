package schmoller.tubes;

import java.util.LinkedList;

public class OverflowBuffer
{
	private LinkedList<TubeItem> mBuffer;
	
	public OverflowBuffer()
	{
		mBuffer = new LinkedList<TubeItem>();
	}
	
	public boolean isEmpty()
	{
		return mBuffer.isEmpty();
	}
	
	public void addItem(TubeItem item)
	{
		mBuffer.add(item);
	}
	
	
	
}
