package turing.btatweaker.impl;

import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointProcessRecipes implements IScriptExecutionPoint {
    public ExecutionPointProcessRecipes() {
        BTATweaker.executionPoints.add(this);
    }

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
