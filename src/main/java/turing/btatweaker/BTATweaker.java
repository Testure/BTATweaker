package turing.btatweaker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turing.btatweaker.api.IScriptExecutionPoint;
import turing.btatweaker.api.IScriptPreprocessor;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.api.ModLibrary;
import turing.btatweaker.impl.*;
import turing.btatweaker.lua.ScriptGlobals;
import turing.btatweaker.lua.ScriptManager;
import turing.btatweaker.luapi.*;
import turing.docs.Docs;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.util.ArrayList;
import java.util.List;

public class BTATweaker implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint, ClientStartEntrypoint, IBTATweaker, BTATweakerEntrypoint {
    public static final String MOD_ID = "btatweaker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static TomlConfigHandler configHandler;
    public static final List<ModLibrary> modLibs = new ArrayList<>();
    public static final List<IScriptExecutionPoint> executionPoints = new ArrayList<>();
    public static final List<IScriptPreprocessor<?>> preprocessors = new ArrayList<>();
    public static final List<IScriptableEvent> events = new ArrayList<>();
    public static final ScriptManager manager = new ScriptManager();

    public static final ExecutionPointRegisterRecipes REGISTER_RECIPES = new ExecutionPointRegisterRecipes();
    public static final ExecutionPointProcessRecipes PROCESS_RECIPES = new ExecutionPointProcessRecipes();
    public static final ExecutionPointBeforeGameStart BEFORE_GAME_START = new ExecutionPointBeforeGameStart();
    public static final ExecutionPointAfterGameStart AFTER_GAME_START = new ExecutionPointAfterGameStart();
    public static final ExecutionPointBeforeClientStart BEFORE_CLIENT_START = new ExecutionPointBeforeClientStart();
    public static final ExecutionPointAfterClientStart AFTER_CLIENT_START = new ExecutionPointAfterClientStart();

    public static boolean SHOULD_GEN_DOCS = false;

    @Override
    public void onInitialize() {
        Toml config = new Toml();
        config.addEntry("genDocs", "set to true to generate docs", false);
        configHandler = new TomlConfigHandler(MOD_ID, config, true);

        if (configHandler.getBoolean("genDocs")) {
            SHOULD_GEN_DOCS = true;
        }

        ScriptGlobals gatherer = new ScriptGlobals();
        FabricLoader.getInstance().getEntrypoints("btatweakerPlugin", BTATweakerEntrypoint.class).forEach((plugin) -> {
            Docs.packagesToSearch.add(plugin.getClass().getPackage().getName());
            plugin.addGlobals(gatherer);
            plugin.initPlugin(this);
        });
        ScriptManager.initGlobals(gatherer);
        for (IScriptExecutionPoint executionPoint : executionPoints) {
            if (preprocessors.stream().noneMatch((p) -> p.getName().equals(executionPoint.getName()))) {
                preprocessors.add(new ExecutionPointPreprocessor(executionPoint));
            }
        }
        manager.loadScripts();
        manager.preprocessScripts();

        String[] args = FabricLoader.getInstance().getLaunchArguments(true);
        for (String arg : args) {
            if (arg.equals("--gen-btatweaker-docs")) {
                SHOULD_GEN_DOCS = true;
                break;
            }
        }

        if (SHOULD_GEN_DOCS) {
            Docs.genDocs();
        }
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
    public void addGlobals(ScriptGlobals globals) {
        globals.addGlobalLib(new UtilLib());
        globals.addGlobalLib(new ModLib());
        globals.addGlobalLib(new ItemLib());
        globals.addGlobalLib(new RecipeLib());
        globals.addGlobalLib(new EventLib());
    }

    @Override
    public void initPlugin(IBTATweaker registry) {
        registry.addExecutionPoint(REGISTER_RECIPES);
        registry.addExecutionPoint(PROCESS_RECIPES);
        registry.addExecutionPoint(BEFORE_GAME_START);
        registry.addExecutionPoint(BEFORE_CLIENT_START);
        registry.addExecutionPoint(AFTER_GAME_START);
        registry.addExecutionPoint(AFTER_CLIENT_START);
        registry.addExecutionPoint(new ExecutionPointNever());
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
        manager.executeScripts(BEFORE_GAME_START);
    }

    @Override
    public void afterGameStart() {
        manager.executeScripts(AFTER_GAME_START);
    }

    @Override
    public void beforeClientStart() {
        manager.executeScripts(BEFORE_CLIENT_START);
    }

    @Override
    public void afterClientStart() {
        manager.executeScripts(AFTER_CLIENT_START);
    }

    @Override
    public void onRecipesReady() {
        manager.executeScripts(REGISTER_RECIPES);
    }

    @Override
    public void initNamespaces() {
        RecipeBuilder.initNameSpace(MOD_ID);
    }
}
