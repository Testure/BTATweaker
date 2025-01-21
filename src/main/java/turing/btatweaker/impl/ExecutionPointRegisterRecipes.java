package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.docs.Description;
import turing.docs.ExecutionPoint;

@ExecutionPoint("OnRegisterRecipes")
@Description("Runs during OnRecipesReady")
public class ExecutionPointRegisterRecipes implements IScriptExecutionPoint {
    @Override
    public String getName() {
        return "onRegisterRecipes";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        if (folderName.contains("Recipes")) {
            return folderName.startsWith("add") || folderName.startsWith("new");
        }
        return false;
    }
}
