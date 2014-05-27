package schmoller.tubes.api.interfaces;

public interface IPropertyHolder
{
	public <T> T getProperty(int prop);
	public <T> void setProperty(int prop, T value);
}
