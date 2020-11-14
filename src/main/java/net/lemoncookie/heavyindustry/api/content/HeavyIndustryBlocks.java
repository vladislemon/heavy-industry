package net.lemoncookie.heavyindustry.api.content;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;

public interface HeavyIndustryBlocks {
    Block test_block = new Block(FabricBlockSettings.of(Material.METAL));
}
