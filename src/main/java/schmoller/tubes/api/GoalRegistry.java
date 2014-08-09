package schmoller.tubes.api;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.collect.HashBiMap;

import schmoller.tubes.api.interfaces.IRoutingGoal;
import schmoller.tubes.network.packets.ModPacketGoalIds;

public class GoalRegistry
{
	private static final GoalRegistry mInstance = new GoalRegistry();
	
	public static GoalRegistry getInstance()
	{
		return mInstance;
	}
	
	private HashBiMap<String, IRoutingGoal> mGoals;
	private HashMap<IRoutingGoal, Integer> mGoalIds;
	private IRoutingGoal[] mBakedGoals;
	
	private GoalRegistry()
	{
		mGoals = HashBiMap.create();
	}
	
	public void initialize()
	{
		mGoalIds = new HashMap<IRoutingGoal, Integer>();
		mBakedGoals = new IRoutingGoal[mGoals.size()];
		int index = 0;
		for(IRoutingGoal goal : mGoals.values())
		{
			mGoalIds.put(goal, index);
			mBakedGoals[index] = goal;
			++index;
		}
	}
	
	public IRoutingGoal get(String name)
	{
		return mGoals.get(name.toLowerCase());
	}
	
	public IRoutingGoal get(int id)
	{
		return mBakedGoals[id];
	}
	
	public int getId(IRoutingGoal goal)
	{
		return mGoalIds.get(goal);
	}
	
	public String getName(IRoutingGoal goal)
	{
		return mGoals.inverse().get(goal);
	}
	
	public ModPacketGoalIds getPacket()
	{
		HashMap<String, Integer> ids = new HashMap<String, Integer>(mGoalIds.size());
		for(Entry<String, IRoutingGoal> goalEntry : mGoals.entrySet())
		{
			int id = mGoalIds.get(goalEntry.getValue());
			ids.put(goalEntry.getKey(), id);
		}
		
		return new ModPacketGoalIds(ids);
	}
	
	public void loadFrom(ModPacketGoalIds packet)
	{
		mGoalIds = new HashMap<IRoutingGoal, Integer>(packet.ids.size());
		mBakedGoals = new IRoutingGoal[packet.ids.size()];
		
		for(Entry<String, Integer> entry : packet.ids.entrySet())
		{
			IRoutingGoal goal = mGoals.get(entry.getKey());
			mGoalIds.put(goal, entry.getValue());
			mBakedGoals[entry.getValue()] = goal;
		}
	}
	
	/**
	 * Registers a routing goal for use in routing.
	 * @param name The name of the goal
	 * @param goal The IRoutingGoal to use
	 */
	public static void registerGoal(String name, IRoutingGoal goal)
	{
		mInstance.mGoals.put(name.toLowerCase(), goal);
	}
}
