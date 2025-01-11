package turing.btatweaker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turing.btatweaker.api.IScriptExecutionPoint;
import turing.btatweaker.api.IScriptPreprocessor;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.api.ModLibrary;
import turing.btatweaker.impl.ExecutionPointPreprocessor;
import turing.btatweaker.lua.LibGatherer;
import turing.btatweaker.lua.ScriptManager;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.ArrayList;
import java.util.List;

public class BTATweaker implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint, IScriptPropertyHolder {
    public static final String MOD_ID = "btatweaker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RecipeNamespace namespace = new RecipeNamespace();
    public static final List<ModLibrary> modLibs = new ArrayList<>();
    public static final List<IScriptExecutionPoint> executionPoints = new ArrayList<>();
    public static final List<IScriptPreprocessor<?>> preprocessors = new ArrayList<>();
    public static final List<IScriptableEvent> events = new ArrayList<>();
    public static final ScriptManager manager = new ScriptManager();

    @Override
    public void onInitialize() {
        LibGatherer gatherer = new LibGatherer();
        FabricLoader.getInstance().getEntrypoints("btatweakerPlugin", BTATweakerEntrypoint.class).forEach((plugin) -> {
            plugin.addLibs(gatherer);
            plugin.init(this);
        });
        ScriptManager.initGlobals(gatherer);
        for (IScriptExecutionPoint executionPoint : executionPoints) {
            if (preprocessors.stream().noneMatch((p) -> p.getName().equals(executionPoint.getName()))) {
                preprocessors.add(new ExecutionPointPreprocessor(executionPoint));
            }
        }
        manager.loadScripts();
        manager.preprocessScripts();
    }

    public void reloadScripts() {
        manager.loadScripts();
        manager.preprocessScripts();
        for (IScriptExecutionPoint executionPoint : executionPoints) {
            if (executionPoint.isReloadable()) {
                manager.executeScripts(executionPoint);
            }
        }
    }

    @Override
    public void addModLibrary(ModLibrary library) {
        modLibs.add(library);
    }

    @Override
    public void addExecutionPoint(IScriptExecutionPoint executionPoint) {
        executionPoints.add(executionPoint);
    }

    @Override
    public void addPreprocessor(IScriptPreprocessor<?> preprocessor) {
        preprocessors.add(preprocessor);
    }

    @Override
    public void addScriptEvent(IScriptableEvent event) {
        events.add(event);
    }

    @Override
    public void beforeGameStart() {
        manager.executeScripts(ScriptManager.BEFORE_GAME_START);
    }

    @Override
    public void afterGameStart() {
        manager.executeScripts(ScriptManager.AFTER_GAME_START);
    }

    @Override
    public void onRecipesReady() {
        manager.executeScripts(ScriptManager.REGISTER_RECIPES);
    }

    @Override
    public void initNamespaces() {
        Registries.RECIPES.register(MOD_ID, namespace);
    }
}
