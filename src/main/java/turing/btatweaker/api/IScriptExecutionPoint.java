package turing.btatweaker.api;

public interface IScriptExecutionPoint {
    String getName();

    default boolean appliesToFolderName(String folderName) {
        return folderName.equals(getName());
    }

    default boolean isReloadable() {
        return false;
    }
}
