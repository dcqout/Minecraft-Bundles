package dcq.mods.mixin.client;

import dcq.mods.Created.item.StickyBundle;
import dcq.mods.utils.BundleRefer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BundleTooltipComponent.class)
public abstract class BundleComponent implements TooltipComponent {
    @Shadow
    private final DefaultedList<ItemStack> inventory;

    @Shadow
    private final int occupancy;

    public BundleComponent(BundleTooltipData data) {
        this.inventory = data.getInventory();
        this.occupancy = data.getBundleOccupancy();
    }

    @Overwrite
    private void drawSlot(int x, int y, int index, boolean shouldBlock, DrawContext context, TextRenderer textRenderer) {
        BundleRefer.scroll_max = this.inventory.size() - 1;
        if (index >= this.inventory.size()) {
            if (this.occupancy >= 100) {
                shouldBlock = this.occupancy-100 >= StickyBundle.MAX_STORAGE;
            }
            Identifier sprite = new Identifier("container/bundle/slot");
            Identifier blocked_sprite = new Identifier("container/bundle/blocked_slot");
            context.drawGuiTexture(shouldBlock ? blocked_sprite : sprite,x,y,0,18,20);
            return;
        }
        ItemStack itemStack = this.inventory.get(index);
        Identifier sprite = new Identifier("container/bundle/slot");
        context.drawGuiTexture(sprite,x,y,0,18,20);
        context.drawItem(itemStack, x + 1, y + 1, index);
        context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);
        if (BundleRefer.scroll == index) {
            HandledScreen.drawSlotHighlight(context, x + 1, y + 1, 0);
        }
    }
}