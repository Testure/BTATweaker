package turing.btatweaker.luapi;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import turing.btatweaker.BTATweaker;
import turing.docs.Argument;
import turing.docs.Documented;
import turing.docs.Function;
import turing.docs.FunctionExample;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.recipeBuilders.RecipeBuilderFurnace;

@Documented
@turing.docs.LuaClass(value = "FurnaceLibrary", folder = "Recipes")
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

    @Function(value = "removeRecipe", arguments = {@Argument(value = "string", name = "namespace"), @Argument(value = "string", name = "id")})
    protected final class RemoveRecipe extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg, LuaValue arg2) {
            removeRecipe(arg.checkjstring(), arg2.checkjstring());
            return NIL;
        }
    }

    @Function(value = "addRecipe", arguments = {
            @Argument(value = "string", name = "id"),
            @Argument(value = "Item", name = "output"),
            @Argument(value = "Ingredient", name = "input")
    }, examples = @FunctionExample({
            "\"my_recipe\"",
            "item(\"item.ingot.gold\") * 5",
            "item(\"tile.obsidian\")"
    }))
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
}
