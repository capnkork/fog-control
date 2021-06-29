package com.capnkork.fogcontrol.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

public final class FogControlConfig {
    private static FogControlConfig INSTANCE = null;

    private static final boolean DEFAULT_NETHER_FOG_ENABLED = true;
    private static final boolean DEFAULT_OVERWORLD_FOG_ENABLED = true;

    private static final int MIN_NETHER_DISTANCE = 0;
    private static final int MAX_NETHER_DISTANCE = 512;
    private static final int DEFAULT_NETHER_DISTANCE = 192;

    private static final int MIN_MULTIPLIER = 0;
    private static final int MAX_MULTIPLIER = 125;
    private static final int MULTIPLIER_DIVIDER = 100;
    private static final int DEFAULT_NETHER_START_MULTIPLIER = 5;
    private static final int DEFAULT_NETHER_END_MULTIPLIER = 50;
    private static final int DEFAULT_OVERWORLD_START_MULTIPLIER = 75;
    private static final int DEFAULT_OVERWORLD_END_MULTIPLIER = 100;

    private static class Data {
        Boolean netherFogEnabled = DEFAULT_NETHER_FOG_ENABLED;
        Integer netherFogMaxDistance = DEFAULT_NETHER_DISTANCE;
        Integer netherFogStartMultiplier = DEFAULT_NETHER_START_MULTIPLIER;
        Integer netherFogEndMultiplier = DEFAULT_NETHER_END_MULTIPLIER;

        Boolean overworldFogEnabled = DEFAULT_OVERWORLD_FOG_ENABLED;
        Integer overworldFogStartMultiplier = DEFAULT_OVERWORLD_START_MULTIPLIER;
        Integer overworldFogEndMultiplier = DEFAULT_OVERWORLD_END_MULTIPLIER;
    }

    private Data data = new Data();

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("fogcontrolmod.json");

    private FogControlConfig() {
        loadConfig();
    }

    public static FogControlConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FogControlConfig();
        }

        return INSTANCE;
    }

    public boolean isNetherFogEnabled() {
        return data.netherFogEnabled;
    }

    public float getNetherFogMaxDistance() {
        return (float) data.netherFogMaxDistance;
    }

    public float getNetherFogStartMultiplier() {
        return ((float) data.netherFogStartMultiplier) / MULTIPLIER_DIVIDER;
    }

    public float getNetherFogEndMultiplier() {
        return ((float) data.netherFogEndMultiplier) / MULTIPLIER_DIVIDER;
    }

    public boolean isOverworldFogEnabled() {
        return data.overworldFogEnabled;
    }

    public float getOverworldFogStartMultiplier() {
        return ((float) data.overworldFogStartMultiplier) / MULTIPLIER_DIVIDER;
    }

    public float getOverworldFogEndMultiplier() {
        return ((float) data.overworldFogEndMultiplier) / MULTIPLIER_DIVIDER;
    }

    public Screen buildScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(new TranslatableText("Fog Control Mod"));

        builder.setSavingRunnable(this::saveConfig);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("Fog Controls"));

        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Nether Fog Enabled"), data.netherFogEnabled)
            .setDefaultValue(DEFAULT_NETHER_FOG_ENABLED)
            .setTooltip(new TranslatableText("Nether Fog Enabled"))
            .setSaveConsumer(enabled -> data.netherFogEnabled = enabled)
            .build());

        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("Nether Fog Max Distance"), data.netherFogMaxDistance, MIN_NETHER_DISTANCE, MAX_NETHER_DISTANCE)
            .setDefaultValue(DEFAULT_NETHER_DISTANCE)
            .setTooltip(new TranslatableText("Nether Fog Max Distance"))
            .setSaveConsumer(newDistance -> data.netherFogMaxDistance = newDistance)
            .build());

        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("Nether Fog Start Multiplier"), data.netherFogStartMultiplier, MIN_MULTIPLIER, MAX_MULTIPLIER)
            .setDefaultValue(DEFAULT_NETHER_START_MULTIPLIER)
            .setTooltip(new TranslatableText("Nether Fog Multiplier"))
            .setSaveConsumer(newMultiplier -> data.netherFogStartMultiplier = newMultiplier)
            .build());

        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("Nether Fog End Multiplier"), data.netherFogEndMultiplier, MIN_MULTIPLIER, MAX_MULTIPLIER)
            .setDefaultValue(DEFAULT_NETHER_END_MULTIPLIER)
            .setTooltip(new TranslatableText("Nether Fog Multiplier"))
            .setSaveConsumer(newMultiplier -> data.netherFogEndMultiplier = newMultiplier)
            .build());

        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("Overworld Fog Enabled"), data.overworldFogEnabled)
            .setDefaultValue(DEFAULT_OVERWORLD_FOG_ENABLED)
            .setTooltip(new TranslatableText("Overworld Fog Enabled"))
            .setSaveConsumer(enabled -> data.overworldFogEnabled = enabled)
            .build());

        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("Overworld Fog Start Multiplier"), data.overworldFogStartMultiplier, MIN_MULTIPLIER, MAX_MULTIPLIER)
            .setDefaultValue(DEFAULT_OVERWORLD_START_MULTIPLIER)
            .setTooltip(new TranslatableText("Overworld Fog Multiplier"))
            .setSaveConsumer(newMultiplier -> data.overworldFogStartMultiplier = newMultiplier)
            .build());

        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("Overworld Fog End Multiplier"), data.overworldFogEndMultiplier, MIN_MULTIPLIER, MAX_MULTIPLIER)
            .setDefaultValue(DEFAULT_OVERWORLD_END_MULTIPLIER)
            .setTooltip(new TranslatableText("Overworld Fog Multiplier"))
            .setSaveConsumer(newMultiplier -> data.overworldFogEndMultiplier = newMultiplier)
            .build());
        
        return builder.build();
    }

    private void saveConfig() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.printf("[Fog Control Mod] Unable to write Fog Control Mod config to %s\n\tException is: %s\n", CONFIG_PATH, e);
        }
    }

    private void loadConfig() {
        try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
            Gson gson = new GsonBuilder().create();
            data = gson.fromJson(reader, Data.class);

            // Ensure any null fields are defaulted
            if (data.netherFogEnabled == null) {
                data.netherFogEnabled = DEFAULT_NETHER_FOG_ENABLED;
            }
            if (data.netherFogMaxDistance == null) {
                data.netherFogMaxDistance = DEFAULT_NETHER_DISTANCE;
            }
            if (data.netherFogStartMultiplier == null) {
                data.netherFogStartMultiplier = DEFAULT_NETHER_START_MULTIPLIER;
            }
            if (data.netherFogEndMultiplier == null) {
                data.netherFogEndMultiplier = DEFAULT_NETHER_END_MULTIPLIER;
            }
            if (data.overworldFogEnabled == null) {
                data.overworldFogEnabled = DEFAULT_OVERWORLD_FOG_ENABLED;
            }
            if (data.overworldFogStartMultiplier == null) {
                data.overworldFogStartMultiplier = DEFAULT_OVERWORLD_START_MULTIPLIER;
            }
            if (data.overworldFogEndMultiplier == null) {
                data.overworldFogEndMultiplier = DEFAULT_OVERWORLD_END_MULTIPLIER;
            }
        } catch (Exception e) {
            System.err.printf("[Fog Control Mod] Unable to read Fog Control Mod config at %s\n\tException is: %s\n", CONFIG_PATH, e);
        }
    }
}
