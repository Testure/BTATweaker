package turing.btatweaker.luapi;

import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import turniplabs.halplibe.helper.RecipeBuilder;

import java.util.ArrayList;
import java.util.List;

public class RecipeLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();
        OneArgFunction groupFunction = new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return new ItemGroupIngredient(arg.checkjstring());
            }
        };

        t.set("addItemsToGroup", new AddItemsToGroup());
        t.set("getItemsInGroup", new GetItemsInGroup());
        t.set("removeItemsFromGroup", new RemoveItemsFromGroup());
        t.set("addJsonRecipe", new AddJsonRecipe());
        t.set("Workbench", new WorkbenchLib());
        t.set("CraftingTable", t.get("Workbench"));
        t.set("Furnace", new FurnaceLib());
        t.set("BlastFurnace", new BlastFurnaceLib());
        t.set("Trommel", new TrommelLib());

        env.set("recipes", t);
        env.get("package").get("loaded").set("recipes", t);
        env.set("itemgroup", groupFunction);
        env.get("package").get("loaded").set("itemgroup", groupFunction);

        return t;
    }

    public static RecipeSymbol recipeSymbolFromLua(LuaValue value) {
        RecipeSymbol symbol = recipeSymbolFromLuaSilent(value);
        if (symbol == null) {
            throw new IllegalStateException("Could not convert lua value to a RecipeSymbol!");
        }
        return symbol;
    }

    @Nullable
    public static RecipeSymbol recipeSymbolFromLuaSilent(LuaValue value) {
        if (value.isstring()) {
            return new RecipeSymbol(value.checkjstring());
        }
        if (value.istable() && value instanceof IIngredient) {
            if (value instanceof LuaItem) {
                return new RecipeSymbol(((LuaItem) value).getDefaultStack(), ((IIngredient) value).getAmount());
            } else if (value instanceof ItemGroupIngredient) {
                return new RecipeSymbol(((ItemGroupIngredient) value).itemGroup, ((IIngredient) value).getAmount());
            } else {
                return new RecipeSymbol(((IIngredient) value).resolve(), ((IIngredient) value).getAmount());
            }
        }
        return null;
    }

    protected static final class AddJsonRecipe extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            throw new LuaError("Unimplemented method.");
        }
    }

    protected static final class GetItemsInGroup extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue modid, LuaValue key) {
            String modId = modid.checkjstring();
            String sKey = key.checkjstring();

            LuaTable table = new LuaTable();

            int i = 1;
            for (ItemStack stack : RecipeBuilder.getItemGroup(modId, sKey)) {
                table.insert(i, new LuaItem(stack));
                i++;
            }

            return table;
        }
    }

    protected static final class RemoveItemsFromGroup extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            String modId = args.checkjstring(1);
            String key = args.checkjstring(2);
            LuaTable firstItem = args.checktable(3);
            if (firstItem instanceof LuaItem) {
                List<LuaItem> items = new ArrayList<>();
                items.add((LuaItem) firstItem);
                Varargs extra = args.subargs(4);
                for (int i = 1; i <= extra.narg(); i++) {
                    LuaTable t = extra.checktable(i);
                    if (t instanceof LuaItem) {
                        items.add((LuaItem) t);
                    }
                }
                for (LuaItem item : items) {
                    RecipeBuilder.getItemGroup(modId, key).removeIf(item::isItemEqual);
                }
            } else {
                throw new LuaError("3rd argument of removeItemsFromGroup must be an Item.");
            }
            return NIL;
        }
    }

    protected static final class AddItemsToGroup extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            String modId = args.checkjstring(1);
            String key = args.checkjstring(2);
            LuaTable firstItem = args.checktable(3);
            if (firstItem instanceof LuaItem) {
                List<LuaItem> items = new ArrayList<>();
                items.add((LuaItem) firstItem);
                Varargs extra = args.subargs(4);
                for (int i = 1; i <= extra.narg(); i++) {
                    LuaTable t = extra.checktable(i);
                    if (t instanceof LuaItem) {
                        items.add((LuaItem) t);
                    }
                }
                RecipeBuilder.addItemsToGroup(modId, key, items.toArray());
            } else {
                throw new LuaError("3rd argument of addItemsToGroup must be an Item.");
            }
            return NIL;
        }
    }
}
