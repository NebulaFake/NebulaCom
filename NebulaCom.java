package com.example.nebulacom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class NebulaCom implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("NebulaCom");
    private static final Path LOG_FILE = Paths.get("config/nebula_com_log.txt");
    private final Map<String, BlockPos> playerPositions = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> trackPlayers(client));
    }

    private void trackPlayers(MinecraftClient client) {
        if (client.world == null || client.player == null) return;
        
        ClientWorld world = client.world;
        
        for (PlayerEntity player : world.getPlayers()) {
            if (player == client.player) continue; // Ignore self
            
            String username = player.getGameProfile().getName();
            BlockPos pos = player.getBlockPos();
            playerPositions.put(username, pos);
        }
        
        savePlayerPositions();
    }

    private void savePlayerPositions() {
        try (FileWriter writer = new FileWriter(LOG_FILE.toFile(), false)) {
            for (Map.Entry<String, BlockPos> entry : playerPositions.entrySet()) {
                writer.write(entry.getKey() + " at " + entry.getValue().toShortString() + "\n");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write player positions", e);
        }
    }
}
