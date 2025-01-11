package turing.btatweaker.impl;

import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointRegisterRecipes implements IScriptExecutionPoint {
    public ExecutionPointRegisterRecipes() {
        BTATweaker.executionPoints.add(this);
    }

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
