package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.docs.Description;
import turing.docs.ExecutionPoint;

@ExecutionPoint("AfterClientStart")
@Description({"Runs during AfterClientStart", "Only runs on the client."})
public class ExecutionPointAfterClientStart implements IScriptExecutionPoint {
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
