package schmoller.tubes;

import java.util.ArrayList;
import java.util.List;

import schmoller.tubes.SpecialShapedRecipe.ISpecialItemCompare;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class SpecialShapelessRecipe implements IRecipe
{
	public ArrayList<Object> recipeItems;
	public ItemStack output;
	

	public SpecialShapelessRecipe(ItemStack output, Object... definition)
	{
		this.output = output.copy();
		
        int index = 0;

        recipeItems = new ArrayList<Object>();
        for (; index < definition.length; index++)
        {
            Object obj = definition[index];
            
            if(obj instanceof Item)
            	obj = new ItemStack((Item)obj);
            else if(obj instanceof Block)
            	obj = new ItemStack((Block)obj);
            else if(obj instanceof Fluid)
            	obj = new FluidStack(((Fluid)obj), FluidContainerRegistry.BUCKET_VOLUME);
            else if(!(obj instanceof String) && !(obj instanceof ItemStack) && !(obj instanceof FluidStack))
            	throw new IllegalArgumentException("Invalid item: " + obj);
            	
            recipeItems.add(obj);
        }
	}
	
	@Override
	public boolean matches( InventoryCrafting inv, World world )
	{
		ArrayList<Object> items = new ArrayList<Object>(recipeItems);
		
		for (int x = 0; x < 3; ++x)
        {
            for (int y = 0; y < 3; ++y)
            {
            	ItemStack inSlot = inv.getStackInRowAndColumn(x, y);

                if(inSlot == null)
                	continue;

                boolean found = false;
                for(Object obj : items)
                {
                	if(obj instanceof ItemStack)
                    {
                    	ItemStack toMatch = (ItemStack)obj;
                    	
                    	if(inSlot.itemID != toMatch.itemID)
                    		continue;
                    	
                    	if(inSlot.getItemDamage() != toMatch.getItemDamage() && toMatch.getItemDamage() != OreDictionary.WILDCARD_VALUE)
                    		continue;
                    	
                    	if(toMatch.getItem() instanceof ISpecialItemCompare)
                    	{
                    		if(!((ISpecialItemCompare)toMatch.getItem()).areItemsEqual(inSlot, toMatch))
                    			continue;
                    	}
                    	
                    }
                    else if(obj instanceof String)
                    {
                    	List<ItemStack> toMatchAny = OreDictionary.getOres((String)obj);
                    	
                    	boolean matched = false;
                    	for(ItemStack toMatch : toMatchAny)
                    	{
                    		if(inSlot.itemID != toMatch.itemID)
                    			continue;
                        	
                        	if(inSlot.getItemDamage() != toMatch.getItemDamage() && toMatch.getItemDamage() != OreDictionary.WILDCARD_VALUE)
                        		continue;
                        	
                        	if(toMatch.getItem() instanceof ISpecialItemCompare)
                        	{
                        		if(!((ISpecialItemCompare)toMatch.getItem()).areItemsEqual(inSlot, toMatch))
                        			continue;
                        	}
                        	
                        	matched = true;
                        	break;
                    	}
                    	
                    	if(!matched)
                    		continue;
                    }
                    else if(obj instanceof FluidStack)
                    {
                    	FluidStack toMatch = (FluidStack)obj;
                    	
                    	FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(inSlot);
                    	if(contained == null || !contained.isFluidStackIdentical(toMatch))
                    		return false;
                    }
                	
                	found = true;
                	items.remove(obj);
                	break;
                }
                
                if(!found)
                	return false;
                
            }
        }
		
		return items.isEmpty();
	}
	
	@Override
	public ItemStack getCraftingResult( InventoryCrafting inventorycrafting )
	{
		return output.copy();
	}

	@Override
	public int getRecipeSize()
	{
		return recipeItems.size();
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return output;
	}
}
