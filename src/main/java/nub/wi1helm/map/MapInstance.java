package nub.wi1helm.map;

import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biome.Biome;
import net.minestom.server.world.biome.BiomeEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MapInstance extends InstanceContainer {
    public MapInstance(String path, int chunks_x, int chunks_z) {
        super(UUID.randomUUID(), DimensionType.OVERWORLD, new AnvilLoader(path));

        this.enableAutoChunkLoad(false);
        // Store the futures so we can use CompletableFuture#allOf
        Set<CompletableFuture<Chunk>> futures = new HashSet<>();

        setTime(1000);
        setTimeRate(0);


        // Code taken from emortalMC
        for (int x = -chunks_x; x <= chunks_x; x++) {
            for (int z = -chunks_z; z <= chunks_z; z++) {
                CompletableFuture<Chunk> future = this.loadChunk(x, z);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        MinecraftServer.getInstanceManager().registerInstance(this);

    }
}
