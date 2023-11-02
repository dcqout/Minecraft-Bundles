package dcq.mods.mixin;

import dcq.mods.Created.item.RemadeBundle;
import dcq.mods.Created.item.StickyBundle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScreenHandler.class)
public abstract class BundleScreenExperimental2 {

    @Shadow public final DefaultedList<Slot> slots;

    protected BundleScreenExperimental2(DefaultedList<Slot> slots) {this.slots = slots;}

    @Shadow
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {}

    @Redirect(method = "onSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void selections(ScreenHandler sc, int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (button < 65) { internalOnSlotClick(slotIndex,button,actionType,player); return; }
        int selection = button-65;
        ItemStack stack = this.slots.get(slotIndex).getStack();
        if (stack.getItem() instanceof RemadeBundle || stack.getItem() instanceof StickyBundle) {
            NbtCompound nbtS = new NbtCompound();
            nbtS.putInt("value",selection);
            stack.setSubNbt("Selection", nbtS);
        }
        internalOnSlotClick(slotIndex,1,actionType,player);
    }
}
