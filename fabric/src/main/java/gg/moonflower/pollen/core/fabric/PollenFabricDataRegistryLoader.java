package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.registry.fabric.PollinatedDataRegistryLoader;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.PollenTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PollenFabricDataRegistryLoader implements PollinatedDataRegistryLoader {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void applyRegistries(Factory factory) {
        factory.bindRegistry(PollenRegistries.BIOME_MODIFIERS);
        if (Pollen.TESTS_ENABLED)
            factory.bindRegistry(PollenTest.MOONFLOWER_TYPES);
    }
}
