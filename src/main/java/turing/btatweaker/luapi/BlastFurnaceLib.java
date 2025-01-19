package turing.btatweaker.luapi;

import turing.btatweaker.BTATweaker;
import turing.docs.Function;
import turing.docs.LuaClass;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderFurnace;

@LuaClass(value = "BlastFurnaceLibrary", extend = "FurnaceLibrary", folder = "Recipes", constructor = @Function(""))
public class BlastFurnaceLib extends FurnaceLib {
    @Override
    protected RecipeBuilderFurnace getBuilder() {
        return RecipeBuilder.BlastFurnace(BTATweaker.MOD_ID);
    }

    @Override
    protected void removeRecipe(String namespace, String id) {
        RecipeBuilder.ModifyBlastFurnace(namespace).removeRecipe(id);
    }
}
