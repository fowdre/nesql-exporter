package com.github.dcysteine.nesql.exporter.plugin.gregtech;

import com.github.dcysteine.nesql.exporter.plugin.EntityFactory;
import com.github.dcysteine.nesql.exporter.plugin.PluginExporter;
import com.github.dcysteine.nesql.exporter.plugin.base.factory.ItemFactory;
import com.github.dcysteine.nesql.exporter.plugin.gregtech.util.GregTechRecipeMap;
import com.github.dcysteine.nesql.exporter.plugin.gregtech.util.Voltage;
import com.github.dcysteine.nesql.exporter.util.IdPrefixUtil;
import com.github.dcysteine.nesql.exporter.util.NumberUtil;
import com.github.dcysteine.nesql.sql.base.item.Item;
import com.github.dcysteine.nesql.sql.base.recipe.Recipe;
import com.github.dcysteine.nesql.sql.gregtech.GregTechRecipe;
import com.github.dcysteine.nesql.sql.gregtech.GregTechRecipeMetadata;
import com.github.dcysteine.nesql.sql.quest.QuestLineEntry;
import com.google.common.base.Joiner;
import cpw.mods.fml.common.ModContainer;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;
import jakarta.persistence.ElementCollection;
import lombok.SneakyThrows;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GregTechRecipeFactory extends EntityFactory<GregTechRecipe, String> {
    private final ItemFactory itemFactory;

    public GregTechRecipeFactory(PluginExporter exporter) {
        super(exporter);
        this.itemFactory = new ItemFactory(exporter);
    }

    @SneakyThrows
    public GregTechRecipe get(
            Recipe recipe, GregTechRecipeMap GregTechRecipeMap, GTRecipe gregTechRecipe,
            Voltage voltageTier, int voltage, List<ItemStack> specialItems) {
        String id = IdPrefixUtil.GREG_TECH_RECIPE.applyPrefix(recipe.getId());

        boolean requiresCleanroom =
                gregTechRecipe.mSpecialValue == -200 || gregTechRecipe.mSpecialValue == -300;
        boolean requiresLowGravity =
                gregTechRecipe.mSpecialValue == -100 || gregTechRecipe.mSpecialValue == -300;

        List<Item> specialItemEntities =
                specialItems.stream()
                        .map(itemFactory::get)
                        .collect(Collectors.toCollection(ArrayList::new));

        List<String> modOwners = gregTechRecipe.owners == null ? new ArrayList<>() :
                gregTechRecipe.owners.stream()
                        .map(ModContainer::getModId)
                        .collect(Collectors.toCollection(ArrayList::new));

        int recipeSpecialValue = gregTechRecipe.mSpecialValue;
        List<String> additionalInfo = new ArrayList<>();
        switch (GregTechRecipeMap.getShortName()) {
            case "gt.recipe.fusionreactor": {
                // Special handling for fusion recipes.
                long euToStart = gregTechRecipe.getMetadataOrDefault(GTRecipeConstants.FUSION_THRESHOLD, 0L);
                recipeSpecialValue = (int)euToStart;

                int euTier;
                if (euToStart <= 160_000_000) {
                    euTier = 1;
                } else if (euToStart <= 320_000_000) {
                    euTier = 2;
                } else if (euToStart <= 640_000_000) {
                    euTier = 3;
                } else {
                    euTier = 4;
                }

                int vTier;
                if (voltage <= GTValues.V[6]) {
                    vTier = 1;
                } else if (voltage <= GTValues.V[7]) {
                    vTier = 2;
                } else if (voltage <= GTValues.V[8]) {
                    vTier = 3;
                } else {
                    vTier = 4;
                }

                additionalInfo.add(
                        String.format(
                                "To start: %s EU (MK %d)",
                                NumberUtil.formatInteger(euToStart),
                                Math.max(euTier, vTier)));
                break;
            }

            case "gt.recipe.blastfurnace":
            case "gt.recipe.plasmaforge": {
                // Special handling for EBF and DTPF recipes.
                int heat = gregTechRecipe.mSpecialValue;

                String tier = HeatingCoilLevel.MAX.getName();
                for (HeatingCoilLevel heatLevel : HeatingCoilLevel.values()) {
                    if (heatLevel == HeatingCoilLevel.None || heatLevel == HeatingCoilLevel.ULV) {
                        continue;
                    }
                    if (heat <= heatLevel.getHeat()) {
                        tier = heatLevel.getName();
                        break;
                    }
                }

                additionalInfo.add(
                        String.format(
                                "Heat capacity: %sK (%s)", NumberUtil.formatInteger(heat), tier));
                break;
            }
        }
        if (gregTechRecipe.getNeiDesc() != null) {
            additionalInfo.addAll(Arrays.asList(gregTechRecipe.getNeiDesc()));
        }


        Class<?> clazz = RecipeMetadataKey.class;
        Field idField = clazz.getDeclaredField("identifier");
        idField.setAccessible(true);

        var metadata = new ArrayList<GregTechRecipeMetadata>();
        for (var meta: gregTechRecipe.getMetadataStorage().getEntries()) {
            var value = meta.getValue();
            long exportValue;
            if (value instanceof Number)
                exportValue = ((Number)value).longValue();
            else if (value instanceof Boolean)
                exportValue = (Boolean) value ? 1 : 0;
            else continue;

            var key = idField.get(meta.getKey());
            if (key instanceof String)
                metadata.add(new GregTechRecipeMetadata((String)key, exportValue));
        }

        GregTechRecipe gregTechRecipeEntity =
                new GregTechRecipe(
                        id,
                        recipe,
                        voltageTier.getName(),
                        voltage,
                        GregTechRecipeMap.getAmperage(),
                        gregTechRecipe.mDuration,
                        recipeSpecialValue,
                        requiresCleanroom,
                        requiresLowGravity,
                        specialItemEntities,
                        modOwners,
                        Joiner.on('\n').join(additionalInfo), metadata);

        return findOrPersist(GregTechRecipe.class, gregTechRecipeEntity);
    }
}
