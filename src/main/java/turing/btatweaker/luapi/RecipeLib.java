package turing.btatweaker.luapi;

import com.google.gson.JsonArray;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import turing.btatweaker.util.JSONUtils;
import turing.btatweaker.util.LuaFunctionFactory;
import turing.docs.*;
import turniplabs.halplibe.helper.RecipeBuilder;

import java.util.ArrayList;
import java.util.List;

@Library(value = "recipes", className = "Recipes")
@Description("Provides access to recipes.")
@Property(name = "Workbench", value = "WorkbenchLibrary", description = "Provides access to workbench library.")
@Property(name = "CraftingTable", value = "WorkbenchLibrary")
@Property(name = "Furnace", value = "FurnaceLibrary", description = "")
@Property(name = "BlastFurnace", value = "BlastFurnaceLibrary", description = "")
@Property(name = "Trommel", value = "TrommelLibrary", description = "")
public class RecipeLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();
        OneArgFunction groupFunction = LuaFunctionFactory.oneArgFunction((groupName) ->
                new ItemGroupIngredient(groupName.checkjstring())
        );

        t.set("addItemsToGroup", new AddItemsToGroup());
        t.set("getItemsInGroup", new GetItemsInGroup());
        t.set("removeItemsFromGroup", new RemoveItemsFromGroup());
        t.set("removeRecipe", new RemoveRecipe());
        t.set("addJSONRecipe", new AddJSONRecipe());
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

        if (value.istable()) {
            List<ItemStack> stacks = new ArrayList<>();
            LuaTable t = value.checktable();
            Varargs pair = t.next(LuaValue.NIL);

            while (pair != NIL) {
                LuaValue key = pair.arg(1);
                LuaValue v = pair.arg(2);

                if (v.istable() && v instanceof LuaItem) {
                    stacks.add(((LuaItem) v).getDefaultStack());
                } else if (v.istable() && v instanceof ItemGroupIngredient) {
                    stacks.addAll(((ItemGroupIngredient) v).resolve());
                } else if (v.isstring()) {
                    String s = v.checkjstring();
                    if (Registries.ITEM_GROUPS.getItem(s) != null) {
                        stacks.addAll(Registries.ITEM_GROUPS.getItem(s));
                    }
                }

                pair = t.next(key);
            }

            if (!stacks.isEmpty()) {
                return new RecipeSymbol(stacks);
            }
        }

        return null;
    }

    @Function(value = "addJSONRecipe", arguments = @Argument(value = "{}", name = "recipe"), examples = @FunctionExample(
            "{\n\t[\"name\"] = \"btatweaker:workbench/epic_shovel\",\n\t" +
                    "[\"type\"] = \"minecraft:crafting/shaped\",\n\t" +
                    "[\"pattern\"] = {\n\t\t\"X\",\n\t\t\"#\",\n\t\t\"#\"\n\t" +
                    "},\n\t[\"symbols\"] = {\n\t\t" +
                    "{\n\t\t\t[\"symbol\"] = \"#\",\n\t\t\t" +
                    "[\"group\"] = \"minecraft:iron_ores\"\n\t\t},\n\t\t{\n\t\t\t" +
                    "[\"symbol\"] = \"X\",\n\t\t\t" +
                    "[\"stack\"] = {\n\t\t\t\t[\"id\"] = 180,\n\t\t\t\t[\"amount\"] = 1,\n\t\t\t\t[\"meta\"] = 0\n\t\t\t}\n\t\t}\n\t},\n\t" +
                    "[\"result\"] = {\n\t\t[\"id\"] = 4,\n\t\t[\"amount\"] = 1,\n\t\t[\"meta\"] = 0\n\t},\n\t" +
                    "[\"consumeContainers\"] = true\n}"
    ))
    @Description({"Attempts to convert a table into a JSON object and tries to add it as a recipe.", "Recipes created with BTATweaker must should always have the namespace `btatweaker`"})
    protected static final class AddJSONRecipe extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue json) {
            LuaTable t = json.checktable();

            JsonArray array = new JsonArray();
            array.add(JSONUtils.convertLuaToJSON(t));

            DataLoader.loadRecipesFromString(JSONUtils.GSON.toJson(array));

            return NIL;
        }
    }

    //@Function(value = "removeRecipe", arguments = {@Argument(value = "string", name = "namespace"), @Argument(value = "string", name = "group"), @Argument(value = "string", name = "recipeId")})
    @Description("Removes the given recipe.")
    protected static final class RemoveRecipe extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue namespace, LuaValue group, LuaValue recipeId) {
            String modId = namespace.checkjstring();
            String recipeGroup = group.checkjstring();
            String recipe = recipeId.checkjstring();

            RecipeBuilder.getRecipeNamespace(modId).getItem(recipeGroup).unregister(recipe);

            return NIL;
        }
    }

    @Function(value = "getItemsInGroup", returnType = "{Item}", arguments = {
            @Argument(value = "string", name = "namespace"),
            @Argument(value = "string", name = "key")
    }, examples = @FunctionExample(value = {"\"minecraft\"", "\"iron_ores\""}, returnValues = "ironOres"))
    @Description("Gets a table containing all items that are in the given item group.")
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

    @Function(value = "removeItemsFromGroup", arguments = {
            @Argument(value = "string", name = "namespace"),
            @Argument(value = "string", name = "key"),
            @Argument(value = "Item...", name = "items")
    }, examples = {
            @FunctionExample({"\"minecraft\"", "\"iron_ores\"", "item(363)"}),
            @FunctionExample({"\"minecraft\"", "\"diamond_ores\"", "item(\"tile.ore.diamond.basalt\")", "item(412)"})
    })
    @Description("removes all given items from the item group `namespace:key`")
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

    @Function(value = "addItemsToGroup", arguments = {
            @Argument(value = "string", name = "namespace"),
            @Argument(value = "string", name = "key"),
            @Argument(value = "Item...", name = "items")
    }, examples = {
            @FunctionExample({"\"minecraft\"", "\"iron_ores\"", "item(260)"}),
            @FunctionExample({"\"minecraft\"", "\"diamond_ores\"", "item(\"tile.obsidian\")", "item(110, 5)"})
    })
    @Description("Adds all given items to the item group `namespace:key`")
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
