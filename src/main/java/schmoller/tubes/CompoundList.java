package schmoller.tubes;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class CompoundList<E> implements List<E>
{
	private List<E> mA, mB;
	public CompoundList(List<E> a, List<E> b)
	{
		mA = a;
		mB = b;
	}
	@Override
	public int size()
	{
		return mA.size() + mB.size();
	}
	@Override
	public boolean isEmpty()
	{
		return mA.isEmpty() && mB.isEmpty();
	}
	@Override
	public boolean contains( Object o )
	{
		return mA.contains(o) || mB.contains(o);
	}
	@Override
	public Iterator<E> iterator()
	{
		return listIterator();
	}
	@Override
	public Object[] toArray()
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public <T> T[] toArray( T[] a )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean add( E e )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean remove( Object o )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean containsAll( Collection<?> c )
	{
		for(Object o : c)
			if(!contains(o))
				return false;
		
		return true;
	}
	@Override
	public boolean addAll( Collection<? extends E> c )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll( int index, Collection<? extends E> c )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean removeAll( Collection<?> c )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean retainAll( Collection<?> c )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public E get( int index )
	{
		if(index < mA.size())
			return mA.get(index);
		return mB.get(index);
	}
	@Override
	public E set( int index, E element )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public void add( int index, E element )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public E remove( int index )
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public int indexOf( Object o )
	{
		int index = mA.indexOf(o);
		if(index != -1)
			return index;
		index = mB.indexOf(o);
		if(index != -1)
			return index + mA.size();
			
		return -1;
	}
	@Override
	public int lastIndexOf( Object o )
	{
		int index = mA.lastIndexOf(o);
		if(index != -1)
			return index;
		index = mB.lastIndexOf(o);
		if(index != -1)
			return index + mA.size();
			
		return -1;
	}
	@Override
	public ListIterator<E> listIterator()
	{
		return listIterator(0);
	}
	@Override
	public ListIterator<E> listIterator( int index )
	{
		return new CompoundIterator(index);
	}
	@Override
	public List<E> subList( int fromIndex, int toIndex )
	{
		throw new UnsupportedOperationException();
	}
	
	private class CompoundIterator implements ListIterator<E>
	{
		private ListIterator<E> mItA;
		private ListIterator<E> mItB;
		
		private int mIndex;
		
		public CompoundIterator(int index)
		{
			mIndex = index;
			if(mIndex < mA.size())
				mItA = mA.listIterator(index);
			else
				mItB = mB.listIterator(index - mA.size());
		}
		
		@Override
		public boolean hasNext()
		{
			return (mIndex < size());
		}

		@Override
		public E next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			E returned = null;
			int last = mIndex;
			
			if(mIndex < mA.size())
				returned = mItA.next();
			else
				returned = mItB.next();
			
			++mIndex;
			
			if(last < mA.size() && mIndex >= mA.size())
				mItB = mB.listIterator(mIndex - mA.size());

			return returned;
		}

		@Override
		public boolean hasPrevious()
		{
			return (mIndex > 0);
		}

		@Override
		public E previous()
		{
			if(!hasPrevious())
				throw new NoSuchElementException();
			
			E returned = null;
			int last = mIndex;
			
			if(mIndex < mA.size())
				returned = mItA.previous();
			else
				returned = mItB.previous();
			
			--mIndex;
			
			if(last >= mA.size() && mIndex < mA.size())
				mItA = mA.listIterator(mIndex);
			
			return returned;
		}

		@Override
		public int nextIndex()
		{
			return mIndex;
		}

		@Override
		public int previousIndex()
		{
			return mIndex - 1;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void set( E e )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void add( E e )
		{
			throw new UnsupportedOperationException();
		}
		
	}
}
