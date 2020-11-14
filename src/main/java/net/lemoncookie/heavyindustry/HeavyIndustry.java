package net.lemoncookie.heavyindustry;

import net.fabricmc.api.ModInitializer;
import net.lemoncookie.heavyindustry.api.content.HeavyIndustryBlocks;
import net.lemoncookie.heavyindustry.api.content.HeavyIndustryItems;
import net.lemoncookie.heavyindustry.content.StaticContentRegister;

public class HeavyIndustry implements ModInitializer {
    @Override
    public void onInitialize() {
        new StaticContentRegister("heavy-industry", HeavyIndustryBlocks.class, HeavyIndustryItems.class).register();
    }
}
