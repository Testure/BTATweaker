package turing.btatweaker.lua;

import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.world.World;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptExecutionPoint;
import turing.btatweaker.util.ScriptUtil;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ScriptManager {
    public static final List<LuaScript> SCRIPTS = new ArrayList<>();
    public static final Globals GLOBALS = new Globals();
    public static final PrintStream log;

    public final List<String> toLog = new ArrayList<String>() {
        @Override
        public boolean add(String s) {
            log.println(s);
            return super.add(s);
        }
    };

    static {
        try {
            log = new PrintStream(ScriptUtil.getBaseDir().getPath() + "/" + BTATweaker.MOD_ID + ".log");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void initGlobals(ScriptGlobals gatherer) {
        GLOBALS.STDOUT = log;

        GLOBALS.load(new JseBaseLib());
        GLOBALS.load(new PackageLib());
        GLOBALS.load(new Bit32Lib());
        GLOBALS.load(new TableLib());
        GLOBALS.load(new StringLib());
        GLOBALS.load(new JseMathLib());
        GLOBALS.load(new CoroutineLib());
        GLOBALS.load(new JseOsLib());

        gatherer.load(GLOBALS);

        LoadState.install(GLOBALS);
        LuaC.install(GLOBALS);
    }

    public void loadScripts(File dir) {
        SCRIPTS.clear();
        EventHandler.EVENT_CONNECTIONS.clear();
        SCRIPTS.addAll(ScriptUtil.gatherScripts(dir, true));

        for (LuaScript script : SCRIPTS) {
            if (script.executionPoint == null) {
                script.executionPoint = BTATweaker.PROCESS_RECIPES;
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

        if (builder.indexOf("@") > -1) {
            builder.deleteCharAt(builder.indexOf("@"));
        }

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
                //removed until 7.2 is no longer supported
                //MinecraftServer.getInstance().logInfo(s);
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
