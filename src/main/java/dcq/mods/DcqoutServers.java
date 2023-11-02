package dcq.mods;

import dcq.mods.Created.item.StickyBundle;
import dcq.mods.Registers.ModItems;
import dcq.mods.Created.item.RemadeBundle;
import net.fabricmc.api.ModInitializer;

public class DcqoutServers implements ModInitializer {

	@Override
	public void onInitialize() {
		References.LOGGER.info("Hello Fabric world!");
		ModItems.registerModItems();
		RemadeBundle.init();
		StickyBundle.init();
	}
}