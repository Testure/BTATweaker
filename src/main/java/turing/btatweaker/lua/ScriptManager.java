package turing.btatweaker.lua;

import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptExecutionPoint;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.impl.ExecutionPointAfterGameStart;
import turing.btatweaker.impl.ExecutionPointBeforeGameStart;
import turing.btatweaker.impl.ExecutionPointProcessRecipes;
import turing.btatweaker.impl.ExecutionPointRegisterRecipes;
import turing.btatweaker.luapi.*;
import turing.btatweaker.util.ScriptUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScriptManager {
    public static final List<LuaScript> SCRIPTS = new ArrayList<>();
    public static final List<EventConnection> EVENT_CONNECTIONS = new ArrayList<>();
    public static final Globals GLOBALS = new Globals();
    public static final ExecutionPointRegisterRecipes REGISTER_RECIPES = new ExecutionPointRegisterRecipes();
    public static final ExecutionPointProcessRecipes PROCESS_RECIPES = new ExecutionPointProcessRecipes();
    public static final ExecutionPointBeforeGameStart BEFORE_GAME_START = new ExecutionPointBeforeGameStart();
    public static final ExecutionPointAfterGameStart AFTER_GAME_START = new ExecutionPointAfterGameStart();

    public final List<String> toLog = new ArrayList<>();

    public static Pair<EventConnection, LuaEventConnection> connectToEvent(LuaFunction function, String eventName) {
        Optional<IScriptableEvent> event = BTATweaker.events.stream().filter((e) -> e.getName().equals(eventName)).findFirst();
        if (event.isPresent()) {
            UUID uuid = UUID.randomUUID();
            EventConnection connection = new EventConnection(uuid, event.get(), function);
            LuaEventConnection luaConnection = new LuaEventConnection(event.get(), uuid);
            EVENT_CONNECTIONS.add(connection);
            return Pair.of(connection, luaConnection);
        }
        throw new NullPointerException("Could not find event with name '" + eventName + "'!");
    }

    public static void fireEvent(String eventName, Varargs varargs) {
        Optional<IScriptableEvent> event = BTATweaker.events.stream().filter((e) -> e.getName().equals(eventName)).findFirst();
        if (event.isPresent()) {
            event.get().fireEvent(varargs);
            return;
        }
        throw new NullPointerException("Could not find event with name '" + eventName + "'!");
    }

    public static void initGlobals(LibGatherer gatherer) {
        GLOBALS.load(new JseBaseLib());
        GLOBALS.load(new PackageLib());
        GLOBALS.load(new Bit32Lib());
        GLOBALS.load(new TableLib());
        GLOBALS.load(new StringLib());
        GLOBALS.load(new JseMathLib());
        GLOBALS.load(new CoroutineLib());
        GLOBALS.load(new JseOsLib());
        GLOBALS.load(new ItemLib());
        GLOBALS.load(new EventLib());
        GLOBALS.load(new ModLib());
        GLOBALS.load(new RecipeLib());

        gatherer.load(GLOBALS);

        LoadState.install(GLOBALS);
        LuaC.install(GLOBALS);
    }

    public void loadScripts(File dir) {
        SCRIPTS.clear();
        EVENT_CONNECTIONS.clear();
        SCRIPTS.addAll(ScriptUtil.gatherScripts(dir, true));

        for (LuaScript script : SCRIPTS) {
            if (script.executionPoint == null) {
                script.executionPoint = PROCESS_RECIPES;
            }
            script.preprocessors.addAll(ScriptUtil.gatherPreprocessors(script));
        }
    }

    public void loadScripts() {
        loadScripts(ScriptUtil.getScriptDir());
    }

    public void executeScripts(String executionPoint) {
        for (LuaScript script : SCRIPTS) {
            if (script.executionPoint != null && script.executionPoint.getName().equals(executionPoint)) {
                try {
                    GLOBALS.loadfile(script.getFile().getPath()).invoke();
                } catch (LuaError error) {
                    catchError(error);
                }
            }
        }
    }

    public void executeScripts(IScriptExecutionPoint executionPoint) {
        executeScripts(executionPoint.getName());
    }

    public void catchError(LuaError error) {
        BTATweaker.LOGGER.error(error.getMessage());
        addError(error);
    }

    private void addError(LuaError error) {
        if (toLog.isEmpty()) {
            toLog.add(TextFormatting.formatted("BTATweaker encountered script errors!", TextFormatting.RED));
        }
        StringBuilder builder = new StringBuilder(error.getMessage());

        if (builder.indexOf("@") > -1) builder.deleteCharAt(builder.indexOf("@"));
        String fullPath = ScriptUtil.getBaseDir().getAbsolutePath();
        int i = builder.indexOf(fullPath);

        if (i > -1) {
            builder.delete(i, fullPath.length() + 1);
        }

        String message = builder.toString();
        for (String s : message.split("\n")) {
            toLog.add(TextFormatting.formatted(s, TextFormatting.RED));
        }
    }

    public void log(World world) {
        if (toLog.isEmpty()) return;
        if (world == null) {
            for (String s : toLog) {
                MinecraftServer.getInstance().logInfo(s);
            }
        } else {
            for (String s : toLog) {
                world.sendGlobalMessage(s);
            }
        }
        toLog.clear();
    }

    public void preprocessScripts() {
        for (LuaScript script : SCRIPTS) {
            if (!script.handlePreprocessors()) {
                BTATweaker.LOGGER.warn("Error in script preprocessor!");
            }
        }
    }
}
