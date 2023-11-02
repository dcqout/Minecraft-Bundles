package dcq.mods.mixin.client;

import dcq.mods.Created.item.RemadeBundle;
import dcq.mods.Created.item.StickyBundle;
import dcq.mods.utils.BundleRefer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledBundle<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow
    protected Slot focusedSlot;
    @Shadow
    protected final T handler;
    public HandledBundle(T handler, PlayerInventory inventory, Text title) {
        super(title);
        this.handler = handler;
    }
    @Inject(method = "drawMouseoverTooltip", at = @At("TAIL"))
    public void drawIt(DrawContext context, int x, int y, CallbackInfo info) {
        if (!BundleRefer.active && ((ScreenHandler)this.handler).getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.focusedSlot.getStack().getItem().asItem() instanceof RemadeBundle || this.focusedSlot.getStack().getItem().asItem() instanceof StickyBundle) {
                BundleRefer.active = true;
                BundleRefer.scroll = 0;
            }
        } else if (BundleRefer.active && !(((ScreenHandler)this.handler).getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack())){
            BundleRefer.active = false;
        }
    }
}