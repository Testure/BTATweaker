package turing.btatweaker.impl;

import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointAfterGameStart implements IScriptExecutionPoint {
    public ExecutionPointAfterGameStart() {
        BTATweaker.executionPoints.add(this);
    }

    @Override
    public String getName() {
        return "afterGameStart";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        return folderName.equals(getName()) || folderName.equals("afterStart");
    }

    @Override
    public boolean isReloadable() {
        return true;
    }
}
