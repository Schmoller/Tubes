package schmoller.tubes;

public interface IRedstoneTube
{
	public void onLoadPower(int level);
	public void onPowerChange(int level);
	
	public int weakPower();
	
	public int strongPower();
}
