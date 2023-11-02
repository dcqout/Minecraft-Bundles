package dcq.mods;

import dcq.mods.Created.item.StickyBundle;
import dcq.mods.Registers.ModItems;
import dcq.mods.Created.item.RemadeBundle;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class DcqoutServersClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModItems.registerModItems();
		ModelPredicateProviderRegistry.register(ModItems.BUNDLE, new Identifier("fillmetter"), (itemStack, clientWorld, livingEntity, seed) -> RemadeBundle.getAmountFilled(itemStack));
		ModelPredicateProviderRegistry.register(ModItems.STICKY_BUNDLE, new Identifier("fillmetter"), (itemStack, clientWorld, livingEntity, seed) -> StickyBundle.getAmountFilled(itemStack));
		RemadeBundle.init();
		StickyBundle.init();
	}
}