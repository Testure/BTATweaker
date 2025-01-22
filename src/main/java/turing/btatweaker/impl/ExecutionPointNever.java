package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.docs.Description;
import turing.docs.Documented;
import turing.docs.ExecutionPoint;

@Documented
@ExecutionPoint("Never")
@Description("An execution point that never runs.")
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
