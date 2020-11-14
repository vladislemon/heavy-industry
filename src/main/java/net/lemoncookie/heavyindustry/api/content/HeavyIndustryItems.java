package net.lemoncookie.heavyindustry.api.content;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public interface HeavyIndustryItems {
    @DoNotRegister
    Item.Settings DEFAULT_SETTINGS = new FabricItemSettings().group(ItemGroup.MISC);

    BlockItem test_block = new BlockItem(HeavyIndustryBlocks.test_block, DEFAULT_SETTINGS);
}
