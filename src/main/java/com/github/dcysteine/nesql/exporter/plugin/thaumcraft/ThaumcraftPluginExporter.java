package com.github.dcysteine.nesql.exporter.plugin.thaumcraft;

import com.github.dcysteine.nesql.exporter.plugin.ExporterState;
import com.github.dcysteine.nesql.exporter.plugin.PluginExporter;
import com.github.dcysteine.nesql.sql.Plugin;

/** Plugin which exports BetterQuesting quests. */
public class ThaumcraftPluginExporter extends PluginExporter {
    private final ThaumcraftRecipeTypeHandler recipeTypeHandler;

    public ThaumcraftPluginExporter(Plugin plugin, ExporterState exporterState) {
        super(plugin, exporterState);
        recipeTypeHandler = new ThaumcraftRecipeTypeHandler(this);
    }

    @Override
    public void initialize() {
        exporterState.addItemListener(new AspectEntryListener(this));
        recipeTypeHandler.initialize();
    }

    @Override
    public void process() {
        new AspectProcessor(this).process();
        new ThaumcraftRecipeProcessor(this, recipeTypeHandler).process();
    }

    @Override
    public void postProcess() {
        new AspectPostProcessor(this).postProcess();
    }
}
