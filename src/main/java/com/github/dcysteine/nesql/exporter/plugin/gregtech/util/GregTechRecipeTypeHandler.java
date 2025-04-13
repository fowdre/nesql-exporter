package com.github.dcysteine.nesql.exporter.plugin.gregtech.util;

import com.github.dcysteine.nesql.exporter.plugin.PluginExporter;
import com.github.dcysteine.nesql.exporter.plugin.PluginHelper;
import com.github.dcysteine.nesql.exporter.plugin.base.factory.ItemFactory;
import com.github.dcysteine.nesql.exporter.plugin.base.factory.RecipeTypeFactory;
import com.github.dcysteine.nesql.sql.base.item.Item;
import com.github.dcysteine.nesql.sql.base.recipe.RecipeType;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GregTechRecipeTypeHandler extends PluginHelper {
    public static final String RECIPE_ID = "gregtech";
    public static final String RECIPE_CATEGORY = "gregtech";

    private final Table<GregTechRecipeMap, Voltage, RecipeType> recipeTypeTable;

    public GregTechRecipeTypeHandler(PluginExporter exporter) {
        super(exporter);
        recipeTypeTable =
                ArrayTable.create(
                        GregTechRecipeMap.allNEIRecipeMaps.values(), Arrays.asList(Voltage.values()));
    }

    public void initialize() {
        ItemFactory itemFactory = new ItemFactory(exporter);
        RecipeTypeFactory recipeTypeFactory = new RecipeTypeFactory(exporter);

        for (GregTechRecipeMap GregTechRecipeMap : GregTechRecipeMap.allNEIRecipeMaps.values()) {
            GregTechRecipeMap.getIcon();
            List<Item> icon = GregTechRecipeMap.getIcon().stream().map(x -> itemFactory.get(x)).collect(Collectors.toList());
            for (Voltage voltage : Voltage.values()) {
                recipeTypeTable.put(
                        GregTechRecipeMap, voltage,
                        recipeTypeFactory.newBuilder()
                                .setId(RECIPE_ID, GregTechRecipeMap.getShortName(), voltage.getName())
                                .setCategory(RECIPE_CATEGORY)
                                .setType(GregTechRecipeMap.getName(voltage))
                                .setIcon(icon)
                                .setIconInfo(voltage.getName())
                                .setShapeless(GregTechRecipeMap.isShapeless())
                                .setItemInputDimension(GregTechRecipeMap.getItemInputDimension())
                                .setFluidInputDimension(GregTechRecipeMap.getFluidInputDimension())
                                .setItemOutputDimension(GregTechRecipeMap.getItemOutputDimension())
                                .setFluidOutputDimension(GregTechRecipeMap.getFluidOutputDimension())
                                .build());
            }
        }
    }

    public RecipeType getRecipeType(GregTechRecipeMap GregTechRecipeMap, Voltage voltage) {
        return recipeTypeTable.get(GregTechRecipeMap, voltage);
    }
}
