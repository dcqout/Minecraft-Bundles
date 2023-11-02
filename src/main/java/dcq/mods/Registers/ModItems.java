package dcq.mods.Registers;

import dcq.mods.Created.item.StickyBundle;
import dcq.mods.References;
import dcq.mods.Created.item.RemadeBundle;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item BUNDLE = registerItem("bundle",
            new RemadeBundle(new Item.Settings().maxCount(1)));

    public static final Item STICKY_BUNDLE = registerItem("sticky_bundle",
            new StickyBundle(new Item.Settings().maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(References.MOD_ID, name), item);
    }
    public static void registerModItems() {
        References.LOGGER.info("Registering Mod Items for " + References.MOD_ID);
    }
}
