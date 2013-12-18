package schmoller.tubes.api.interfaces;

/**
 * A provider can specify an inventory for any object. 
 * This can be used to remap slots, make an inventory smart,
 * give an inventory to something that didn't have one, etc.
 */
public interface IInterfaceProvider<T>
{
	/**
	 * Returns an interface for the specified object, or null
	 */
	public T provide(Object object);
}
