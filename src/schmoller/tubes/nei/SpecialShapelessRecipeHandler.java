package schmoller.tubes.nei;

import java.util.ArrayList;
import java.util.List;

import schmoller.tubes.SpecialShapedRecipe.ISpecialItemCompare;
import schmoller.tubes.SpecialShapelessRecipe;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class SpecialShapelessRecipeHandler extends TemplateRecipeHandler
{
	public class CachedSpecialShapelessRecipe extends CachedRecipe
	{
		public ArrayList<PositionedStack> ingredients;
		public PositionedStack result;
		
		public CachedSpecialShapelessRecipe(SpecialShapelessRecipe recipe)
		{
			result = new PositionedStack(recipe.output, 119, 24);
			result.generatePermutations();
			
			ingredients = new ArrayList<PositionedStack>();
			
			for (int i = 0; i < recipe.recipeItems.size(); ++i)
			{
				Object obj = recipe.recipeItems.get(i);
				if (obj == null)
					continue;

				if(obj instanceof String)
					obj = OreDictionary.getOres((String)obj);
				else if(obj instanceof FluidStack)
				{
					ArrayList<ItemStack> items = new ArrayList<ItemStack>();
					for(FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData())
					{
						if(data.fluid.containsFluid((FluidStack)obj))
							items.add(data.filledContainer);
					}
					obj = items;
				}
				
				PositionedStack stack = new PositionedStack(obj, 25 + (i % 3) * 18, 6 + (i / 3) * 18);
				stack.setMaxSize(1);
				this.ingredients.add(stack);
			}
		}
		
		@Override
		public PositionedStack getResult()
		{
			return result;
		}
		
		@Override
		public List<PositionedStack> getIngredients()
		{
			return getCycledIngredients(cycleticks / 20, ingredients);
		}
		
	}
	@Override
	public String getRecipeName()
	{
		return "Tubes Shapeless Recipe";
	}

	@Override
	public String getGuiTexture()
	{
		return "textures/gui/container/crafting_table.png";
	}
	
	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiCrafting.class;
	}
	
	@Override
	public void loadCraftingRecipes( ItemStack result )
	{
		for(IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList())
		{
			if (!(NEIServerUtils.areStacksSameTypeCrafting(recipe.getRecipeOutput(), result)))
				continue;
			
			if(result.getItem() instanceof ISpecialItemCompare)
        	{
        		if(!((ISpecialItemCompare)result.getItem()).areItemsEqual(recipe.getRecipeOutput(), result))
        			continue;
        	}
			
			CachedSpecialShapelessRecipe cashed = null;
			
			if (recipe instanceof SpecialShapelessRecipe)
				cashed = new CachedSpecialShapelessRecipe((SpecialShapelessRecipe)recipe);
			else
				continue;
			
			arecipes.add(cashed);
		}
	}
	
	@Override
	public void loadUsageRecipes( ItemStack ingredient )
	{
		for(IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList())
		{
			if(recipe instanceof SpecialShapelessRecipe)
			{
				SpecialShapelessRecipe sRecipe = (SpecialShapelessRecipe)recipe;
				
				boolean found = false;
				for(Object obj : sRecipe.recipeItems)
				{
					if(obj instanceof ItemStack)
	                {
	                	ItemStack toMatch = (ItemStack)obj;
	                	
	                	if(!ingredient.isItemEqual(toMatch))
	                		continue;
	                	
	                	if(ingredient.getItemDamage() != toMatch.getItemDamage() && toMatch.getItemDamage() != OreDictionary.WILDCARD_VALUE)
	                		continue;
	                	
	                	if(toMatch.getItem() instanceof ISpecialItemCompare)
	                	{
	                		if(!((ISpecialItemCompare)toMatch.getItem()).areItemsEqual(ingredient, toMatch))
	                			continue;
	                	}
	                }
	                else if(obj instanceof String)
	                {
	                	List<ItemStack> toMatchAny = OreDictionary.getOres((String)obj);
	                	
	                	boolean matched = false;
	                	for(ItemStack toMatch : toMatchAny)
	                	{
	                		if(!ingredient.isItemEqual(toMatch))
	                			continue;
	                    	
	                    	if(ingredient.getItemDamage() != toMatch.getItemDamage() && toMatch.getItemDamage() != OreDictionary.WILDCARD_VALUE)
	                    		continue;
	                    	
	                    	if(toMatch.getItem() instanceof ISpecialItemCompare)
	                    	{
	                    		if(!((ISpecialItemCompare)toMatch.getItem()).areItemsEqual(ingredient, toMatch))
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
	                	
	                	if(!FluidContainerRegistry.containsFluid(ingredient, toMatch))
	                		continue;
	                }
	                else if(obj == null)
	                	continue;
					
					found = true;
					break;
				}
				
				if(found)
					arecipes.add(new CachedSpecialShapelessRecipe(sRecipe));
			}
		}
	}
	
	
}
