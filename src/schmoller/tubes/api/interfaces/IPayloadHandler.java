package schmoller.tubes.api.interfaces;

import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;

/**
 * Handlers allow you to specify how to interact with an object with Payloads
 */
public interface IPayloadHandler<T extends Payload>
{
	/**
	 * Attempts to insert the payload
	 * @param payload The payload to insert. This cannot be null. This must NOT be modified by implementing classes
	 * @param side The side to insert from
	 * @param doAdd When false, this action will only be simulated
	 * @return Anything left over, or null if everything was inserted
	 */
	public T insert(T payload, int side, boolean doAdd);
	
	/**
	 * Attempts to extract a payload
	 * @param template The filter to use. Cannot be null, use AnyFilter: "any" to select anything 
	 * @param side The side to grab from
	 * @param doExtract When false, this action will only be simulated
	 * @return The item that was extracted
	 */
	public T extract(IFilter template, int side, boolean doExtract);
	/**
	 * Attempts to extract a payload
	 * @param template The filter to use. Cannot be null, use AnyFilter: "any" to select anything
	 * @param side The side to grab from
	 * @param count The amount to pull. The exact meaning of this depends on mode
	 * @param mode Defines what count means. See {@link SizeMode}
	 * @param doExtract When false, this action will only be simulated
	 * @return The item that was extracted
	 */
	public T extract(IFilter template, int side, int count, SizeMode mode, boolean doExtract);
}
