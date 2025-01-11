package turing.btatweaker.lua;

import net.minecraft.core.util.collection.Pair;
import turing.btatweaker.api.IScriptExecutionPoint;
import turing.btatweaker.api.IScriptPreprocessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LuaScript {
    private final File file;
    private final String name;
    protected final List<Pair<IScriptPreprocessor<?>, String>> preprocessors = new ArrayList<>();
    public IScriptExecutionPoint executionPoint;

    public LuaScript(File file) {
        this.file = file;
        this.name = file.getName().replace(".luau", "").replace(".lua", "");
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public List<Pair<IScriptPreprocessor<?>, String>> getPreprocessors() {
        return preprocessors;
    }

    public boolean hasPreprocessor(IScriptPreprocessor<?> preprocessor) {
        return preprocessors.stream().anyMatch((pair) -> pair.getLeft().getName().equals(preprocessor.getName()));
    }

    @SuppressWarnings("unchecked")
    public <T> boolean handlePreprocessors() {
        boolean hasError = false;

        for (Pair<IScriptPreprocessor<?>, String> pair : preprocessors) {
            IScriptPreprocessor<T> preprocessor = (IScriptPreprocessor<T>) pair.getLeft();
            if (!preprocessor.handle(this, preprocessor.intrepretArgument(pair.getRight()))) {
                hasError = true;
            }
        }

        return !hasError;
    }
}
