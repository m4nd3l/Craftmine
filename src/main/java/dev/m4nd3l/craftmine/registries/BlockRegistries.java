package dev.m4nd3l.craftmine.registries;

import dev.m4nd3l.craftmine.blocks.Block;
import dev.m4nd3l.craftmine.blocks.BlockSettings;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;

import java.util.HashMap;
import java.util.Map;

public class BlockRegistries {
    private static Map<Integer, BlockRegistry> blocks = new HashMap<>();

    public static BlockRegistry AIR = register(new BlockRegistry(0, 0, new Block("air", BlockSettings.NONE)));
    public static BlockRegistry STONE = register(new BlockRegistry(1, 1, new Block("stone", BlockSettings.NONE)));
    public static BlockRegistry GRASS = register(new BlockRegistry(2, 4, 2, 3,
            new Block("grass_block", BlockSettings.NONE)));
    public static BlockRegistry DIRT = register(new BlockRegistry(3, 4, new Block("dirt", BlockSettings.NONE)));
    public static BlockRegistry SANDSTONE = register(new BlockRegistry(4, 8, 6, 7,
            new Block("sandstone", BlockSettings.NONE)));
    public static BlockRegistry SAND = register(new BlockRegistry(5, 5, new Block("sand", BlockSettings.NONE)));


    private static BlockRegistry register(BlockRegistry registry) { blocks.put(registry.getId(), registry); return registry; }
    public static BlockRegistry getBlock(int id) { return blocks.get(id); }
}