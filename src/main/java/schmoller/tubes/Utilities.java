package schmoller.tubes;

import java.io.UnsupportedEncodingException;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;

public class Utilities
{
	/**
	 * Writes a string to the output. This method exists as a workaround to a 
	 * bug as different implementations of MCDataOutput use different ways of writing a string
	 */
	public static void writeString(MCDataOutput output, String string)
	{
		try
		{
			byte[] data = string.getBytes("UTF-8");
			output.writeVarShort(data.length);
			output.writeByteArray(data);
		}
		catch ( UnsupportedEncodingException e )
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a string from the input. This method exists as a workaround to a 
	 * bug as different implementations of MCDataOutput use different ways of writing a string
	 */
	public static String readString(MCDataInput input)
	{
		int len = input.readVarShort();
		byte[] data = input.readByteArray(len);
		
		try
		{
			return new String(data, "UTF-8");
		}
		catch ( UnsupportedEncodingException e )
		{
			e.printStackTrace();
			return null;
		}
	}
}
