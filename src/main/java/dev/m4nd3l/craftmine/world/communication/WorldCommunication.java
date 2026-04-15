package dev.m4nd3l.craftmine.world.communication;

import dev.m4nd3l.craftmine.Main;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.world.World;

public class WorldCommunication {
    public static void tellWorld(SubChunkCoordinates coordinates, Communication message) {
        World world = Main.craftmine.getCurrentWorld();
        if (world != null) world.getCommunication(coordinates, message);
    }

    public static short getBlockID(int x, int y, int z) {
        World world = Main.craftmine.getCurrentWorld();
        if (world != null) return (short) world.getBlockRegistryRequest(x, y, z).getId();
        return 0;
    }
}
