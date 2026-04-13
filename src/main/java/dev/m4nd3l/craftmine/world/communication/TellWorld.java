package dev.m4nd3l.craftmine.world.communication;

import dev.m4nd3l.craftmine.Main;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.world.World;

public class TellWorld {
    public static void tellWorld(SubChunkCoordinates coordinates, Communication message) {
        World world = Main.craftmine.getCurrentWorld();
        if (world != null) world.getCommunication(coordinates, message);
    }
}
