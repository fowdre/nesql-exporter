package com.github.dcysteine.nesql.exporter.plugin.thaumcraft;

import com.github.dcysteine.nesql.exporter.plugin.PluginExporter;
import com.github.dcysteine.nesql.exporter.plugin.PluginHelper;
import com.github.dcysteine.nesql.exporter.plugin.base.factory.ItemFactory;
import com.github.dcysteine.nesql.exporter.plugin.base.factory.RecipeTypeFactory;
import com.github.dcysteine.nesql.exporter.plugin.minecraft.MinecraftRecipeTypeHandler;
import com.github.dcysteine.nesql.exporter.util.ItemUtil;
import com.github.dcysteine.nesql.sql.base.item.Item;
import com.github.dcysteine.nesql.sql.base.recipe.RecipeType;
import net.minecraft.init.Blocks;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;

import java.util.EnumMap;

public class ThaumcraftRecipeTypeHandler extends PluginHelper {

    public ThaumcraftRecipeTypeHandler(PluginExporter exporter) {
        super(exporter);
        recipeTypeMap = new EnumMap<>(ThaumcraftRecipeTypeHandler.ThaumcraftRecipeType.class);
    }

    public static final String RECIPE_ID = "thaumcraft";
    public static final String RECIPE_CATEGORY = "thaumcraft";

    public enum ThaumcraftRecipeType {
        MAGIC_CRAFTING_SHAPED,
        MAGIC_CRAFTING_SHAPELESS,
        ALCHEMY,
        INFUSION,
    }

    private final EnumMap<ThaumcraftRecipeTypeHandler.ThaumcraftRecipeType, RecipeType> recipeTypeMap;

    public void initialize() {
        ItemFactory itemFactory = new ItemFactory(exporter);
        RecipeTypeFactory recipeTypeFactory = new RecipeTypeFactory(exporter);

        Item craftingTable = itemFactory.get(ItemUtil.getItemStack(ConfigBlocks.blockTable, 15).get());
        Item crucible = itemFactory.get(ItemUtil.getItemStack(ConfigBlocks.blockMetalDevice, 0).get());
        Item matrix = itemFactory.get(ItemUtil.getItemStack(ConfigBlocks.blockStoneDevice, 2).get());


        recipeTypeMap.put(
                ThaumcraftRecipeType.MAGIC_CRAFTING_SHAPED,
                recipeTypeFactory.newBuilder()
                        .setId(RECIPE_ID, "crafting", "shaped")
                        .setCategory(RECIPE_CATEGORY)
                        .setType("Magical Crafting (Shaped)")
                        .setIcon(craftingTable)
                        .setShapeless(false)
                        .setItemInputDimension(3, 5)
                        .setItemOutputDimension(1, 1)
                        .build());

        recipeTypeMap.put(
                ThaumcraftRecipeType.MAGIC_CRAFTING_SHAPELESS,
                recipeTypeFactory.newBuilder()
                        .setId(RECIPE_ID, "crafting", "shapeless")
                        .setCategory(RECIPE_CATEGORY)
                        .setType("Magical Crafting (Shapeless)")
                        .setIcon(craftingTable)
                        .setShapeless(true)
                        .setItemInputDimension(3, 5)
                        .setItemOutputDimension(1, 1)
                        .build());

        recipeTypeMap.put(
                ThaumcraftRecipeType.ALCHEMY,
                recipeTypeFactory.newBuilder()
                        .setId(RECIPE_ID, "alchemy")
                        .setCategory(RECIPE_CATEGORY)
                        .setType("Crucible")
                        .setIcon(crucible)
                        .setShapeless(true)
                        .setItemInputDimension(3, 3)
                        .setItemOutputDimension(1, 1)
                        .build());

        recipeTypeMap.put(
                ThaumcraftRecipeType.INFUSION,
                recipeTypeFactory.newBuilder()
                        .setId(RECIPE_ID, "infusion")
                        .setCategory(RECIPE_CATEGORY)
                        .setType("Arcane Infusion")
                        .setIcon(matrix)
                        .setShapeless(true)
                        .setItemInputDimension(5, 7)
                        .setItemOutputDimension(1, 1)
                        .build());
    }

    public RecipeType getRecipeType(ThaumcraftRecipeType recipeType) {
        return recipeTypeMap.get(recipeType);
    }
}
