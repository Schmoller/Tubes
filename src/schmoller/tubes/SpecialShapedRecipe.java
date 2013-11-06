package schmoller.tubes;

import java.util.HashMap;
import java.util.List;

import schmoller.tubes.api.interfaces.ISpecialItemCompare;

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

public class SpecialShapedRecipe implements IRecipe
{
	public int width;
	public int height;
	
	public Object[] recipeItems;
	public ItemStack output;
	
	public boolean canMirror = true;
	
	
	public SpecialShapedRecipe(ItemStack output, Object... definition)
	{
		this.output = output.copy();
		
		String recipe = "";
        int index = 0;
        width = 0;
        height = 0;

        while (definition[index] instanceof String)
        {
            String line = (String)definition[index++];
            ++height;
            width = line.length();
            recipe += line;
        }

        HashMap<Character, Object> map = new HashMap<Character, Object>();

        for (; index < definition.length; index += 2)
        {
            Character character = (Character)definition[index];
            
            Object obj = definition[index+1];
            
            if(obj instanceof Item)
            	obj = new ItemStack((Item)obj);
            else if(obj instanceof Block)
            	obj = new ItemStack((Block)obj);
            else if(obj instanceof Fluid)
            	obj = new FluidStack(((Fluid)obj), FluidContainerRegistry.BUCKET_VOLUME);
            else if(!(obj instanceof String) && !(obj instanceof ItemStack) && !(obj instanceof FluidStack))
            	throw new IllegalArgumentException("Invalid definition: " + character + " = " + obj);
            	
            map.put(character, obj);
        }

        recipeItems = new Object[width * height];
        
        for (int i = 0; i < width * height; ++i)
        {
            char c = recipe.charAt(i);
            
            recipeItems[i] = map.get(c);
        }
	}
	
	public SpecialShapedRecipe setCanMirror(boolean canMirror)
	{
		this.canMirror = canMirror;
		return this;
	}
	
	@Override
	public boolean matches( InventoryCrafting inv, World world )
	{
		for (int i = 0; i <= 3 - width; ++i)
        {
            for (int j = 0; j <= 3 - height; ++j)
            {
                if (checkMatch(inv, i, j, false))
                    return true;

                if (canMirror && checkMatch(inv, i, j, true))
                    return true;
            }
        }
		
		return false;
	}
	
	private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror)
	{
		for (int x = 0; x < 3; ++x)
        {
            for(int y = 0; y < 3; ++y)
            {
                Object obj = null;
                
                if(x >= startX && x < startX + width && y >= startY && y < startY + height)
                {
	                if(mirror)
	                	obj = recipeItems[(width - (x - startX) - 1) + (y - startY) * width];
	                else
	                	obj = recipeItems[(x - startX) + (y - startY) * width];
                }
                
                ItemStack inSlot = inv.getStackInRowAndColumn(x, y);

                if(inSlot == null && obj != null)
                	return false;
                
                if(obj instanceof ItemStack)
                {
                	ItemStack toMatch = (ItemStack)obj;
                	
                	if(inSlot.itemID != toMatch.itemID)
                		return false;
                	
                	if(inSlot.getItemDamage() != toMatch.getItemDamage() && toMatch.getItemDamage() != OreDictionary.WILDCARD_VALUE)
                		return false;
                	
                	if(toMatch.getItem() instanceof ISpecialItemCompare)
                	{
                		if(!((ISpecialItemCompare)toMatch.getItem()).areItemsEqual(inSlot, toMatch))
                			return false;
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
                		return false;
                }
                else if(obj instanceof FluidStack)
                {
                	FluidStack toMatch = (FluidStack)obj;
                	
                	FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(inSlot);
                	if(contained == null || !contained.isFluidStackIdentical(toMatch))
                		return false;
                }
            }
        }
		
		return true;
	}

	@Override
	public ItemStack getCraftingResult( InventoryCrafting inventorycrafting )
	{
		return output.copy();
	}

	@Override
	public int getRecipeSize()
	{
		return width * height;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return output;
	}

}
