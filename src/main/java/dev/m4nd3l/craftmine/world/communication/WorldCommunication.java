package dev.m4nd3l.craftmine.world.communication;

import dev.m4nd3l.craftmine.Main;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.util.Mix;
import dev.m4nd3l.craftmine.world.World;

import java.util.ArrayList;
import java.util.List;

public class WorldCommunication {
    public static List<Mix<SubChunkCoordinates, Communication>> pendingRequests = new ArrayList<>();

    public static void tellWorld(SubChunkCoordinates coordinates, Communication message) {
        if (pendingRequests.contains(new Mix<>(coordinates, message))) return;
        World world = Main.craftmine.getCurrentWorld();
        if (world != null) world.getCommunication(coordinates, message);
    }

    public static void markAsDone(SubChunkCoordinates coordinates, Communication message) { pendingRequests.remove(new Mix<>(coordinates, message)); }

    public static short getBlockID(int x, int y, int z) {
        World world = Main.craftmine.getCurrentWorld();
        if (world != null) return (short) world.getBlockRegistryRequest(x, y, z).getId();
        return 0;
    }
}
