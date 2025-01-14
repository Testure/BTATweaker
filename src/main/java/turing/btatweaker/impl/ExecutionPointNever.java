package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointNever implements IScriptExecutionPoint {
    @Override
    public String getName() {
        return "never";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        return folderName.equalsIgnoreCase(getName()) || folderName.equalsIgnoreCase("modules");
    }
}
