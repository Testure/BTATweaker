package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointBeforeClientStart implements IScriptExecutionPoint {
    @Override
    public String getName() {
        return "beforeClientStart";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        return folderName.equals(getName()) || folderName.equals("beforeClient");
    }

    @Override
    public boolean isReloadable() {
        return true;
    }
}
