package turing.btatweaker.luapi;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import turing.btatweaker.BTATweaker;
import turing.docs.Argument;
import turing.docs.Description;
import turing.docs.Function;
import turing.docs.Method;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderTrommel;

@turing.docs.LuaClass(value = "TrommelLibrary", folder = "Recipes")
public class TrommelLib extends LuaClass {
    public TrommelLib() {
        super();
        rawset("addRecipe", new AddRecipe());
        rawset("removeRecipe", new RemoveRecipe());
    }

    @Function(value = "removeRecipe", arguments = {@Argument(value = "string", name = "namespace"), @Argument(value = "string", name = "id")})
    protected static final class RemoveRecipe extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg, LuaValue arg2) {
            RecipeBuilder.ModifyTrommel(arg.checkjstring(), arg2.checkjstring()).deleteRecipe();
            return NIL;
        }
    }

    @Function(value = "addRecipe", returnType = "TrommelRecipeBuilder", arguments = {@Argument(value = "string", name = "id"), @Argument(value = "Item", name = "input")})
    protected static final class AddRecipe extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue id, LuaValue input) {
            return new TrommelBuilder(id.checkjstring(), RecipeLib.recipeSymbolFromLua(input));
        }
    }

    @turing.docs.LuaClass(value = "TrommelRecipeBuilder")
    public static final class TrommelBuilder extends LuaClass {
        private RecipeBuilderTrommel builder;
        private final String id;

        public TrommelBuilder(String id, RecipeSymbol input) {
            super();
            this.id = id;
            this.builder = RecipeBuilder.Trommel(BTATweaker.MOD_ID).setInput(input);

            rawset("AddEntry", new AddEntry());
            rawset("Build", new Build());
        }

        @Method("Build")
        @Description("Builds and adds the recipe.")
        protected final class Build extends OneArgFunction {
            @Override
            public LuaValue call(LuaValue self) {
                builder.create(id);
                return NIL;
            }
        }

        @Method(value = "AddEntry", builder = true, arguments = {
                @Argument(value = "Item", name = "output"),
                @Argument(value = "number", name = "weight"),
                @Argument(value = "number?", name = "yieldMin"),
                @Argument(value = "number?", name = "yieldMax")
        })
        protected static final class AddEntry extends VarArgFunction {
            @Override
            public LuaValue invoke(Varargs args) {
                TrommelBuilder self = (TrommelBuilder) args.checktable(1);
                LuaTable item = args.checktable(2);
                if (!LuaItem.isLuaItem(item)) {
                    throw new LuaError("1st argument of AddEntry must be an item.");
                }

                int yieldMin = 1;
                int yieldMax = 1;
                double weight;

                switch (args.narg()) {
                    case 3:
                        weight = args.checkdouble(3);
                        break;
                    case 4:
                        yieldMax = args.checkint(4);
                        yieldMin = yieldMax;
                        weight = args.checkdouble(3);
                        break;
                    default:
                        yieldMin = args.checkint(4);
                        yieldMax = args.checkint(5);
                        weight = args.checkdouble(3);
                        break;
                }

                WeightedRandomLootObject object;

                if (yieldMax == 1) {
                    object = new WeightedRandomLootObject(((LuaItem) item).getDefaultStack());
                } else if (yieldMax == yieldMin) {
                    object = new WeightedRandomLootObject(((LuaItem) item).getDefaultStack(), yieldMax);
                } else {
                    object = new WeightedRandomLootObject(((LuaItem) item).getDefaultStack(), yieldMin, yieldMax);
                }

                self.builder = self.builder.addEntry(object, weight);

                return self;
            }
        }
    }
}
