package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.docs.Description;
import turing.docs.ExecutionPoint;

@ExecutionPoint("OnProcessRecipes")
@Description("Runs after OnRecipesReady")
public class ExecutionPointProcessRecipes implements IScriptExecutionPoint {
    @Override
    public String getName() {
        return "onProcessRecipes";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        if (folderName.contains("Recipes")) {
            if (folderName.startsWith("remove") || folderName.startsWith("handle") || folderName.startsWith("change") || folderName.startsWith("modify")) {
                return true;
            }
        }
        return folderName.equals(getName()) || folderName.equals("processRecipes");
    }
}
