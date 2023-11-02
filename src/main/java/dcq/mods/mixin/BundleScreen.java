package dcq.mods.mixin;

import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ScreenHandler.class)
public class BundleScreen {
    /*

    @Shadow public final DefaultedList<Slot> slots = DefaultedList.of();

    private int casted;
    @ModifyArg(method = "onSlotClick",at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"
    ),index = 1)
    private int ButtonCheck(int orig) {
        this.casted = 0;
        if (orig >= 65) {
            this.casted = orig;
            orig = 1;
        }
        return orig;
    }
        @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;onPickupSlotClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/ClickType;)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void OnBundleClicked(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo cir) {
        if (this.casted >= 65) {
            ItemStack stack = this.slots.get(slotIndex).getStack();
            if (stack.getItem() instanceof RemadeBundle) {
                RemadeBundle bundle = (RemadeBundle) stack.getItem();
                bundle.selection = this.casted - 65;
                References.LOGGER.info("bundle used");
            }
        }
    }*/
}
