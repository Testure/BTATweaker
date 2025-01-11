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
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderTrommel;

public class TrommelLib extends LuaClass {
    public TrommelLib() {
        super();
        rawset("addRecipe", new AddRecipe());
        rawset("removeRecipe", new RemoveRecipe());
    }

    protected static final class AddRecipe extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            String id = args.checkjstring(1);
            LuaValue input = args.arg(2);

            return new TrommelBuilder(id, RecipeLib.recipeSymbolFromLua(input));
        }
    }

    protected static final class RemoveRecipe extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg, LuaValue arg2) {
            RecipeBuilder.ModifyTrommel(arg.checkjstring(), arg2.checkjstring()).deleteRecipe();
            return NIL;
        }
    }

    public static final class TrommelBuilder extends LuaClass {
        private final String id;
        private RecipeBuilderTrommel builder;

        public TrommelBuilder(String id, RecipeSymbol input) {
            super();
            this.id = id;
            this.builder = RecipeBuilder.Trommel(BTATweaker.MOD_ID).setInput(input);

            rawset("Build", new Build());
            rawset("AddEntry", new AddEntry());
        }

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

        protected final class Build extends OneArgFunction {
            @Override
            public LuaValue call(LuaValue self) {
                builder.create(id);
                return NIL;
            }
        }
    }
}
