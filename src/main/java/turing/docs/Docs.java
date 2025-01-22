package turing.docs;

import net.minecraft.core.util.collection.Pair;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.util.ScriptUtil;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;

public class Docs {
    private static File getDir(String name) {
        File dir = new File(ScriptUtil.getBaseDir().getAbsolutePath() + "/" + name);
        if (!dir.exists() && !dir.mkdir()) throw new IllegalStateException("Could not make directory '" + name + "'");
        return dir;
    }

    private static File getDocsDir() {
        return getDir("BTATweakerDocs");
    }

    private static <A extends Annotation> Optional<A> getAnnotation(Class<?> clazz, Class<A> annotation) {
        if (clazz.isAnnotationPresent(annotation)) {
            return Optional.of(clazz.getAnnotation(annotation));
        }
        return Optional.empty();
    }

    private static String getClassName(Class<?> clazz) {
        String[] strings = clazz.getName().split("[.]");
        return strings[strings.length - 1];
    }

    private static void documentFunction(@Nullable Description functionDescription, MarkdownBuilder builder, String parentName, String funcName, String returnType, Argument[] arguments, FunctionExample[] examples, String invoker) {
        Optional<Description> description = Optional.ofNullable(functionDescription);

        builder.appendHeader(funcName, 2).newLine();

        description.ifPresent(desc -> {
            for (String s : desc.value()) {
                builder.append(s).newLine();
            }
        });

        if (arguments.length > 0) {
            builder.newLine().appendHeader("Arguments", 3).newLine();
            for (Argument arg : arguments) {
                if (!arg.name().isEmpty()) {
                    builder.append(arg.name()).append(": ");
                }
                builder.appendCode(arg.value());
                if (!arg.description().isEmpty()) {
                    builder.append(" ").append(arg.description()).newLine();
                }
                builder.newLine().newLine();
            }
        }

        if (!returnType.isEmpty()) {
            builder.newLine().appendHeader("Returns ", 3).appendCode(returnType).newLine();
        }

        if (examples.length > 0) {
            builder.newLine().appendHeader("Example", 2);
            if (examples.length > 1) {
                builder.append("s");
            }
            builder.newLine();
        }
        for (FunctionExample example : examples) {
            builder.startCodeBlock();
            for (String exampleLine : example.comments()) {
                builder.append(exampleLine).newLine();
            }
            if (example.returnValues().length > 0) {
                builder.append("local ");
                for (int i = 0; i < example.returnValues().length; i++) {
                    builder.append(example.returnValues()[i]);
                    if (i < example.returnValues().length - 1) {
                        builder.append(", ");
                    }
                }
                builder.append(" = ");
            }
            builder.append(parentName).append(invoker).append(funcName).append("(");
            for (int i = 0; i < example.value().length; i++) {
                builder.append(example.value()[i]);
                if (i < example.value().length - 1) {
                    builder.append(", ");
                }
            }
            builder.append(")").endCodeBlock();
        }
    }

    private static void gatherProperties(List<Property> properties, MarkdownBuilder builder) {
        if (!properties.isEmpty()) {
            builder.newLine().appendHorizontalRule().newLine();
            builder.appendHeader("Properties", 2).newLine();
            for (Property property : properties) {
                builder.append(property.name()).append(": ").appendCode(property.value());
                if (!property.description().isEmpty()) {
                    builder.append(" ").append(property.description());
                }
                builder.newLine().newLine();
            }
            builder.newLine();
        }
    }

    private static void gatherFunctions(String className, List<Pair<Class<?>, Function>> functions, MarkdownBuilder builder) {
        if (!functions.isEmpty()) {
            builder.newLine().appendHorizontalRule().newLine();
            builder.appendHeader("Functions", 2).newLine();
            for (int i = 0; i < functions.size(); i++) {
                Pair<Class<?>, Function> pair = functions.get(i);
                Function def = pair.getRight();
                documentFunction(pair.getLeft().getAnnotation(Description.class), builder, className, def.value(), def.returnType(), def.arguments(), def.examples(), ".");
                if (i < functions.size() - 1) {
                    builder.newLine().appendHorizontalRule().newLine();
                }
            }
        }
    }

    private static void gatherMethods(String className, List<Pair<Class<?>, Method>> methods, MarkdownBuilder builder) {
        if (!methods.isEmpty()) {
            builder.newLine().appendHorizontalRule().newLine();
            builder.appendHeader("Methods", 2).newLine();
            for (int i = 0; i < methods.size(); i++) {
                Pair<Class<?>, Method> pair = methods.get(i);
                Method def = pair.getRight();
                documentFunction(pair.getLeft().getAnnotation(Description.class), builder, className, def.value(), def.builder() ? className : def.returnValue(), def.arguments(), def.examples(), ":");
                if (i < methods.size() - 1) {
                    builder.newLine().appendHorizontalRule().newLine();
                }
            }
        }
    }

    private static void gatherDocs(Class<?> clazz, String className, MarkdownBuilder builder) {
        List<Pair<Class<?>, Method>> methods = new ArrayList<>();
        List<Pair<Class<?>, Function>> functions = new ArrayList<>();
        List<Property> properties = Arrays.asList(clazz.getAnnotationsByType(Property.class));
        for (Class<?> methodClass : clazz.getDeclaredClasses()) {
            if (methodClass.isAnnotationPresent(Method.class)) {
                methods.add(Pair.of(methodClass, methodClass.getAnnotation(Method.class)));
            } else if (methodClass.isAnnotationPresent(Function.class)) {
                functions.add(Pair.of(methodClass, methodClass.getAnnotation(Function.class)));
            }
        }

        gatherProperties(properties, builder);
        gatherFunctions(className, functions, builder);
        gatherMethods(className, methods, builder);
    }

    private static void genDocs(Class<?> clazz, String folder, String name, String defClassName, @Nullable Function constructor, @Nullable String extend) {
        File dir = getDir(getDocsDir().getName() + "/" + folder);
        String className = !defClassName.isEmpty() ? defClassName : getClassName(clazz);
        Optional<Description> description = getAnnotation(clazz, Description.class);
        MarkdownBuilder builder = new MarkdownBuilder();

        builder.appendHeader(className).newLine();

        description.ifPresent(desc -> {
            builder.newLine();
            for (String s : desc.value()) {
                builder.append(s).newLine();
            }
        });

        if (clazz.isAnnotationPresent(ExecutionPoint.class)) {
            builder.newLine().appendCode("--#" + name).newLine();
        }

        if (extend != null) {
            builder.append("Extends ").appendCode(extend).newLine();
        }

        if (constructor != null) {
            builder.newLine().appendHorizontalRule().newLine();
            builder.appendHeader("Constructor").newLine();
            documentFunction(null, builder, "", constructor.value(), constructor.returnType(), constructor.arguments(), constructor.examples(), "");
        }

        gatherDocs(clazz, name, builder.newLine());

        try (PrintWriter writer = new PrintWriter(dir.getAbsolutePath() + "/" + className + ".md")) {
            String[] lines = builder.toString().split("\n");
            for (String line : lines) {
                writer.println(line);
            }
        } catch (Exception e) {
            BTATweaker.LOGGER.error(e.getMessage());
        }
    }

    private static void genManualDoc(Class<?> clazz, ManualDoc def) {
        try {
            if (!def.name().isEmpty()) {
                java.lang.reflect.Method method = clazz.getDeclaredMethod("writeDoc", MarkdownBuilder.class);
                String folder = !def.folder().isEmpty() ? def.folder() : "extra";

                File dir = getDir(getDocsDir().getName() + "/" + folder);
                MarkdownBuilder builder = new MarkdownBuilder();

                method.invoke(null, builder);

                if (!builder.isEmpty()) {
                    try (PrintWriter writer = new PrintWriter(dir.getAbsolutePath() + "/" + def.name() + ".md")) {
                        String[] lines = builder.toString().split("\n");
                        for (String s : lines) {
                            writer.println(s);
                        }
                    }
                }
            }
        } catch (Exception e) {
            BTATweaker.LOGGER.error(e.getMessage());
        }
    }

    public static void genDocs() {
        long time = System.currentTimeMillis();

        File dir = getDocsDir();
        File[] l = dir.listFiles();
        if (l != null && l.length > 0 && dir.delete()) {
            if (!dir.mkdir()) {
                throw new IllegalStateException("Error creating directory!");
            }
        }

        Set<Class<?>> list = new Reflections(new ConfigurationBuilder().forPackages(BTATweaker.packagesToSearch.toArray(new String[0]))).getTypesAnnotatedWith(Documented.class);
        for (Class<?> clazz : list) {
            if (clazz.isAnnotationPresent(ManualDoc.class)) {
                genManualDoc(clazz, clazz.getAnnotation(ManualDoc.class));
            } else if (clazz.isAnnotationPresent(Library.class)) {
                Library def = clazz.getAnnotation(Library.class);
                genDocs(clazz, "Libraries", def.value(), def.className(), null, null);
            } else if (clazz.isAnnotationPresent(LuaClass.class)) {
                LuaClass def = clazz.getAnnotation(LuaClass.class);
                genDocs(clazz, def.folder(), def.value(), def.value(), !def.constructor().value().isEmpty() ? def.constructor() : null, !def.extend().isEmpty() ? def.extend() : null);
            } else if (clazz.isAnnotationPresent(ModLibrary.class)) {
                ModLibrary def = clazz.getAnnotation(ModLibrary.class);
                genDocs(clazz, "Mods", "mods." + def.value(), def.className(), null, null);
            } else if (clazz.isAnnotationPresent(ExecutionPoint.class)) {
                ExecutionPoint def = clazz.getAnnotation(ExecutionPoint.class);
                genDocs(clazz, "ExecutionPoints", def.value().replaceFirst(String.valueOf(def.value().charAt(0)), String.valueOf(def.value().charAt(0)).toLowerCase()), def.value(), null, null);
            }
        }

        BTATweaker.LOGGER.info("Created docs in {}ms", System.currentTimeMillis() - time);
    }
}
