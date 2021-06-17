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

    private static final int MIN_DISTANCE_NETHER = 0;
    private static final int DEFAULT_DISTANCE_NETHER = 192;
    private static final int MAX_DISTANCE_NETHER = 512;

    private static final int MIN_MULTIPLIER = 0;
    private static final int DEFAULT_MULTIPLIER_NETHER = 50;
    private static final int DEFAULT_MULTIPLIER_OVERWORLD = 75;
    private static final int MAX_MULTIPLIER = 200;
    private static final int MULTIPLIER_DIVIDER = 100;

    private class Data {
        Integer netherFogMaxDistance = DEFAULT_DISTANCE_NETHER;
        Integer netherFogMultiplier = DEFAULT_MULTIPLIER_NETHER;
        Integer overworldFogMultiplier = DEFAULT_MULTIPLIER_OVERWORLD;
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

    public Float getNetherFogMaxDistance() {
        return (float) data.netherFogMaxDistance;
    }

    public Float getNetherFogMultiplier() {
        return ((float) data.netherFogMultiplier) / MULTIPLIER_DIVIDER;
    }

    public Float getOverworldFogMultiplier() {
        return ((float) data.overworldFogMultiplier) / MULTIPLIER_DIVIDER;
    }

    public Screen buildScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(new TranslatableText("Fog Control Mod"));

        builder.setSavingRunnable(() -> {
            saveConfig();
        });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("Fog Controls"));

        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("Nether Fog Max Distance"), data.netherFogMaxDistance, MIN_DISTANCE_NETHER, MAX_DISTANCE_NETHER)
            .setDefaultValue(DEFAULT_DISTANCE_NETHER)
            .setTooltip(new TranslatableText("Nether Fog Max Distance"))
            .setSaveConsumer(newDistance -> {
                data.netherFogMaxDistance = newDistance;
            })
            .build()
        );

        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("Nether Fog Multiplier"), data.netherFogMultiplier, MIN_MULTIPLIER, MAX_MULTIPLIER)
            .setDefaultValue(DEFAULT_MULTIPLIER_NETHER)
            .setTooltip(new TranslatableText("Nether Fog Multiplier"))
            .setSaveConsumer(newMultiplier -> {
                data.netherFogMultiplier = newMultiplier;
            })
            .build()
        );

        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("Overworld Fog Multiplier"), data.overworldFogMultiplier, MIN_MULTIPLIER, MAX_MULTIPLIER)
            .setDefaultValue(DEFAULT_MULTIPLIER_OVERWORLD)
            .setTooltip(new TranslatableText("Overworld Fog Multiplier"))
            .setSaveConsumer(newMultiplier -> {
                data.overworldFogMultiplier = newMultiplier;
            })
            .build()
        );
        
        return builder.build();
    }

    private void saveConfig() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.printf("[Fog Control Mod] Unable to write Fog Control Mod config to %s\n\tException is: %s\n", CONFIG_PATH.toString(), e.toString());
        }
    }

    private void loadConfig() {
        try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
            Gson gson = new GsonBuilder().create();
            data = gson.fromJson(reader, Data.class);

            // Ensure any null fields are defaulted
            if (data.netherFogMaxDistance == null) {
                data.netherFogMaxDistance = DEFAULT_DISTANCE_NETHER;
            }
            if (data.netherFogMultiplier == null) {
                data.netherFogMultiplier = DEFAULT_MULTIPLIER_NETHER;
            }
            if (data.overworldFogMultiplier == null) {
                data.overworldFogMultiplier = DEFAULT_MULTIPLIER_OVERWORLD;
            }
        } catch (Exception e) {
            System.err.printf("[Fog Control Mod] Unable to read Fog Control Mod config at %s\n\tException is: %s\n", CONFIG_PATH.toString(), e.toString());
        }
    }
}
