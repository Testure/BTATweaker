package turing.btatweaker.luapi;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import turing.btatweaker.BTATweaker;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderFurnace;

public class FurnaceLib extends LuaClass {
    public FurnaceLib() {
        super();
        rawset("removeRecipe", new RemoveRecipe());
        rawset("addRecipe", new AddRecipe());
    }

    protected RecipeBuilderFurnace getBuilder() {
        return RecipeBuilder.Furnace(BTATweaker.MOD_ID);
    }

    protected void removeRecipe(String namespace, String id) {
        RecipeBuilder.ModifyFurnace(namespace).removeRecipe(id);
    }

    protected final class AddRecipe extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            RecipeBuilderFurnace builder = getBuilder();

            builder = builder.setInput(RecipeLib.recipeSymbolFromLua(args.arg(3)));

            String recipeId = args.checkjstring(1);
            LuaValue output = args.arg(2);
            if (!LuaItem.isLuaItem(output)) {
                throw new LuaError("2nd argument of addRecipe must be an item.");
            }


            builder.create(recipeId, ((LuaItem) output).getDefaultStack());

            return NIL;
        }
    }

    protected final class RemoveRecipe extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg, LuaValue arg2) {
            removeRecipe(arg.checkjstring(), arg2.checkjstring());
            return NIL;
        }
    }
}
