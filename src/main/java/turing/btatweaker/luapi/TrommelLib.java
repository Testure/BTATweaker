package turing.btatweaker.luapi;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.util.LuaFunctionFactory;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderTrommel;

public class TrommelLib extends LuaClass {
    public TrommelLib() {
        super();
        rawset("addRecipe", LuaFunctionFactory.twoArgFunction((id, input) ->
                new TrommelBuilder(id.checkjstring(), RecipeLib.recipeSymbolFromLua(input)))
        );
        rawset("removeRecipe", LuaFunctionFactory.twoArgFunction((arg, arg2) -> {
            RecipeBuilder.ModifyTrommel(arg.checkjstring(), arg2.checkjstring()).deleteRecipe();
            return NIL;
        }));
    }

    public static final class TrommelBuilder extends LuaClass {
        private RecipeBuilderTrommel builder;

        public TrommelBuilder(String id, RecipeSymbol input) {
            super();
            this.builder = RecipeBuilder.Trommel(BTATweaker.MOD_ID).setInput(input);

            rawset("AddEntry", new AddEntry());
            rawset("Build", LuaFunctionFactory.zeroArgBuilderMethod((self) ->
                builder.create(id)
            ));
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
    }
}
