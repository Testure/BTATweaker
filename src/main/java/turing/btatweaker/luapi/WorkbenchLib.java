package turing.btatweaker.luapi;

import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.util.LuaFunctionFactory;
import turing.docs.*;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderShaped;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderShapeless;

import java.util.ArrayList;
import java.util.List;

@Documented
@turing.docs.LuaClass(value = "WorkbenchLibrary", folder = "Recipes")
@Description("Allows access to workbench (crafting) recipes.")
public class WorkbenchLib extends LuaClass {
    public WorkbenchLib() {
        super();
        rawset("addShapeless", new AddShapeless());
        rawset("addShaped", new AddShaped());
        rawset("removeRecipe", new RemoveRecipe());
    }

    @Function(value = "removeRecipe", arguments = {@Argument(value = "string", name = "namespace"), @Argument(value = "string", name = "id")})
    protected static final class RemoveRecipe extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg, LuaValue arg2) {
            RecipeBuilder.ModifyWorkbench(arg.checkjstring()).removeRecipe(arg2.checkjstring());
            return NIL;
        }
    }

    @Function(value = "addShaped", returnType = "ShapedRecipeBuilder", arguments = {@Argument(value = "string", name = "id"), @Argument(value = "Item", name = "output")})
    protected static final class AddShaped extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            String id = arg1.checkjstring();

            LuaTable output = arg2.checktable();
            if (!LuaItem.isLuaItem(output)) {
                throw new LuaError("2nd argument of addShaped must be an item.");
            }

            return new ShapedBuilder(id, (LuaItem) output);
        }
    }

    @Function(value = "addShapeless", arguments = {
            @Argument(value = "string", name = "id"),
            @Argument(value = "Item", name = "output"),
            @Argument(value = "Ingredient...", name = "inputs")
    }, examples = @FunctionExample({
            "\"cool_recipe\"",
            "item(\"tile.wool\", 5)",
            "item(1)",
            "itemgroup(\"minecraft:iron_ores\")"
    }))
    protected static final class AddShapeless extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            String id = args.checkjstring(1);

            LuaTable output = args.checktable(2);
            if (!LuaItem.isLuaItem(output)) {
                throw new LuaError("2nd argument of addShapeless must be an item.");
            }

            Varargs extra = args.subargs(3);
            if (extra.narg() < 1) {
                throw new LuaError("addShapeless requires at least 1 input item. got 0");
            }

            List<RecipeSymbol> inputs = new ArrayList<>();

            for (int i = 1; i <= extra.narg(); i++) {
                RecipeSymbol symbol = RecipeLib.recipeSymbolFromLuaSilent(extra.arg(i));
                if (symbol != null) {
                    inputs.add(symbol);
                }
            }

            RecipeBuilderShapeless builder = RecipeBuilder.Shapeless(BTATweaker.MOD_ID);

            for (RecipeSymbol symbol : inputs) {
                builder = builder.addInput(symbol);
            }

            builder.create(id, ((LuaItem) output).getDefaultStack());

            return NIL;
        }
    }

    @Documented
    @turing.docs.LuaClass(value = "ShapedRecipeBuilder")
    protected static final class ShapedBuilder extends LuaClass {
        private RecipeBuilderShaped builder;

        public ShapedBuilder(String id, LuaItem output) {
            super();
            this.builder = RecipeBuilder.Shaped(BTATweaker.MOD_ID);

            rawset("WithInput", new Input());
            rawset("WithShape", new Shape());
            rawset("Build", LuaFunctionFactory.zeroArgBuilderMethod((self) -> {
                builder.create(id, output.getDefaultStack());
            }));
        }

        @Method(value = "Shape", builder = true, arguments = {@Argument(value = "string...", name = "shape")}, examples = @FunctionExample({
                "\n\t\"X\"",
                "\n\t\"#\"",
                "\n\t\"#\"\n"
        }))
        protected final class Shape extends VarArgFunction {
            @Override
            public LuaValue invoke(Varargs args) {
                LuaTable self = args.checktable(1);

                args = args.subargs(2);
                if (args.narg() < 1 || args.narg() > 3) {
                    throw new LuaError("Invalid argument count for shape expected 1-3 arguments. got " + args.narg());
                }

                String[] strings = new String[args.narg()];
                for (int i = 1; i <= args.narg(); i++) {
                    strings[i - 1] = args.checkjstring(i);
                }

                builder = builder.setShape(strings);

                return self;
            }
        }

        @Method(value = "Input", builder = true, arguments = {@Argument(value = "string", name = "key"), @Argument(value = "Ingredient", name = "input")}, examples = {@FunctionExample({
                "\"X\"",
                "item(\"item.ingot.gold\")"
        }), @FunctionExample({
                "\"#\"",
                "item(\"item.ingot.iron\")"
        })})
        protected final class Input extends ThreeArgFunction {
            @Override
            public LuaValue call(LuaValue self, LuaValue arg1, LuaValue arg2) {
                char c = arg1.checkjstring().charAt(0);
                RecipeSymbol symbol = RecipeLib.recipeSymbolFromLua(arg2);

                builder = builder.addInput(c, symbol);

                return self;
            }
        }
    }
}
