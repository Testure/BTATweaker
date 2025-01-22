package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.docs.Description;
import turing.docs.Documented;
import turing.docs.ExecutionPoint;

@Documented
@ExecutionPoint("BeforeClientStart")
@Description({"Runs during BeforeClientStart", "Only runs on the client."})
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
