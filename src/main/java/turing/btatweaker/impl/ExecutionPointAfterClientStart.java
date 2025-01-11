package turing.btatweaker.impl;

import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointAfterClientStart implements IScriptExecutionPoint {
    public ExecutionPointAfterClientStart() {
        BTATweaker.executionPoints.add(this);
    }

    @Override
    public String getName() {
        return "afterClientStart";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        return folderName.equals(getName()) || folderName.equals("afterClient");
    }

    @Override
    public boolean isReloadable() {
        return true;
    }
}
