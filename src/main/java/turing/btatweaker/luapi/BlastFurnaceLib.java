package turing.btatweaker.luapi;

import turing.btatweaker.BTATweaker;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderFurnace;

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
