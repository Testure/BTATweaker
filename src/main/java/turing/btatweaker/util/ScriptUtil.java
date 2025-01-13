package turing.btatweaker.util;

import net.minecraft.core.Global;
import net.minecraft.core.util.collection.Pair;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptPreprocessor;
import turing.btatweaker.lua.LuaScript;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ScriptUtil {
    public static FilenameFilter luaScriptFilter = (dir, name) -> name.endsWith(".lua") || name.endsWith(".luau");

    public static LuaScript getScriptFromFile(File file) {
        if (file.getName().endsWith(".luau")) {
            BTATweaker.LOGGER.warn("A .luau file is being loaded as a .lua file! This will cause problems if the .luau script uses luau features!");
        }
        return new LuaScript(file);
    }

    public static File getBaseDir() {
        return Global.accessor.getMinecraftDir();
    }

    public static File getScriptDir() {
        File scriptDir = new File(getBaseDir().getAbsolutePath() + "/scripts");

        if (!scriptDir.exists() && !scriptDir.mkdirs()) {
            throw new IllegalStateException("Could not create script directory!!");
        }

        return scriptDir;
    }

    public static List<LuaScript> gatherScripts(File baseFolder, boolean sortByFolderName) {
        if (!baseFolder.exists()) throw new IllegalStateException("Tried to operate on a non-existent file!");
        if (!baseFolder.isDirectory()) throw new UnsupportedOperationException("Tried to scan files in a non-directory file!");

        List<LuaScript> scripts = new ArrayList<>();
        List<File> folders = getAllFoldersInFolder(baseFolder);
        List<File> files = getAllFilesInFolder(baseFolder);

        BiConsumer<File, File> handleScript = (file, parent) -> {
            LuaScript script = getScriptFromFile(file);

            if (sortByFolderName) {
                BTATweaker.executionPoints.stream().filter((e) ->
                        e.appliesToFolderName(parent.getName())
                ).findFirst().ifPresent(executionPoint ->
                        script.executionPoint = executionPoint
                );
            }

            scripts.add(script);
        };

        files.forEach((file) -> handleScript.accept(file, baseFolder));
        recurseFolders(folders, handleScript);

        return scripts;
    }

    private static void recurseFolders(List<File> folders, BiConsumer<File, File> handler) {
        for (File folder : folders) {
            List<File> files = getAllFilesInFolder(folder);

            for (File file : files) {
                handler.accept(file, folder);
            }

            recurseFolders(getAllFoldersInFolder(folder), handler);
        }
    }

    public static List<File> getAllFoldersInFolder(File folder) {
        if (!folder.exists()) throw new IllegalStateException("Tried to operate on a non-existent file!");
        if (!folder.isDirectory()) throw new UnsupportedOperationException("Tried to scan folders in a non-directory file!");
        List<File> folders = new ArrayList<>();

        File[] array = folder.listFiles();
        if (array != null) {
            for (File file : array) {
                if (file.exists() && file.isDirectory()) {
                    folders.add(file);
                }
            }
        }

        return folders;
    }

    public static List<File> getAllFilesInFolder(File folder) {
        if (!folder.exists()) throw new IllegalStateException("Tried to operate on a non-existent file!");
        if (!folder.isDirectory()) throw new UnsupportedOperationException("Tried to scan files in a non-directory file!");
        List<File> files = new ArrayList<>();

        File[] array = folder.listFiles(luaScriptFilter);
        if (array != null) {
            for (File file : array) {
                if (file.exists() && !file.isDirectory()) {
                    files.add(file);
                }
            }
        }

        return files;
    }

    public static String getArgument(String line) {
        if (line.contains(" ") && line.substring(line.indexOf(' '), line.indexOf(' ') + 1).equals(" [") && line.contains("]")) {
            return line.substring(line.indexOf(' ') + 2, line.indexOf(']') - 1);
        }
        return null;
    }

    public static String getPreprocessorName(String line) {
        return line.substring(0, line.contains(" ") ? line.indexOf(' ') : line.length());
    }

    public static List<Pair<IScriptPreprocessor<?>, String>> gatherPreprocessors(LuaScript script) {
        List<Pair<IScriptPreprocessor<?>, String>> preprocessors = new ArrayList<>();

        String justPreprocessors = "";
        File file = script.getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();

            while (line != null && line.startsWith("--#")) {
                builder.append(line);
                line = reader.readLine();
                if (line != null && line.startsWith("--#")) {
                    builder.append("\n");
                }
            }

            justPreprocessors = builder.toString();
        } catch (Exception e) {
            BTATweaker.LOGGER.error(e.getMessage());
        }

        String[] split = justPreprocessors.split("\n");
        for (String line : split) {
            if (line.length() > 3 && line.startsWith("--#")) {
                line = line.substring(3);

                String preprocessorName = getPreprocessorName(line);
                String arg = getArgument(line);
                Optional<IScriptPreprocessor<?>> preprocessor = BTATweaker.preprocessors.stream().filter((p) -> p.getName().equals(preprocessorName)).findFirst();

                preprocessor.ifPresent(p ->
                        preprocessors.add(Pair.of(p, arg))
                );
            }
        }

        return preprocessors;
    }
}
