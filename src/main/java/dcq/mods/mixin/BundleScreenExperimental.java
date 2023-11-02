package dcq.mods.mixin;

import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ScreenHandler.class)
public abstract class BundleScreenExperimental {
    /*

    @Shadow public static final int EMPTY_SPACE_SLOT_INDEX = -999;
    @Shadow private int quickCraftButton;

    @Shadow private int quickCraftStage;

    public BundleScreenExperimental(DefaultedList<Slot> slots, Set<Slot> quickCraftSlots) {
        this.slots = slots;
        this.quickCraftSlots = quickCraftSlots;
    }

    @Shadow protected void endQuickCraft() {}

    @Shadow public ItemStack getCursorStack() {return null;}

    @Shadow public final DefaultedList<Slot> slots;

    @Shadow private final Set<Slot> quickCraftSlots;

    @Shadow  public boolean canInsertIntoSlot(Slot slot) {return true;}

    @Shadow public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return true;
    }

    @Shadow public void setCursorStack(ItemStack stack) {}

    @Shadow public abstract ItemStack quickMove(PlayerEntity var1, int var2);

    @Shadow
    private boolean handleSlotClick(PlayerEntity player, ClickType clickType, Slot slot, ItemStack stack, ItemStack cursorStack) {
        return false;
    }

    @Overwrite
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        int selection = -1;
        References.LOGGER.info("Selection is: ");
        if (button >= 65) {
            selection = button-65;
            button = 1;
        }
        block39: {
            block50: {
                block46: {
                    ItemStack itemStack;
                    ItemStack itemStack2;
                    Slot slot3;
                    PlayerInventory playerInventory;
                    block49: {
                        block48: {
                            block47: {
                                block44: {
                                    ClickType clickType;
                                    block45: {
                                        block43: {
                                            block37: {
                                                block42: {
                                                    ItemStack itemStack3;
                                                    block41: {
                                                        block40: {
                                                            block38: {
                                                                playerInventory = player.getInventory();
                                                                if (actionType != SlotActionType.QUICK_CRAFT) break block37;
                                                                int i = this.quickCraftStage;
                                                                this.quickCraftStage = ScreenHandler.unpackQuickCraftStage(button);
                                                                if (i == 1 && this.quickCraftStage == 2 || i == this.quickCraftStage) break block38;
                                                                this.endQuickCraft();
                                                                break block39;
                                                            }
                                                            if (!this.getCursorStack().isEmpty()) break block40;
                                                            this.endQuickCraft();
                                                            break block39;
                                                        }
                                                        if (this.quickCraftStage != 0) break block41;
                                                        this.quickCraftButton = ScreenHandler.unpackQuickCraftButton(button);
                                                        if (ScreenHandler.shouldQuickCraftContinue(this.quickCraftButton, player)) {
                                                            this.quickCraftStage = 1;
                                                            this.quickCraftSlots.clear();
                                                        } else {
                                                            this.endQuickCraft();
                                                        }
                                                        break block39;
                                                    }
                                                    if (this.quickCraftStage != 1) break block42;
                                                    Slot slot = this.slots.get(slotIndex);
                                                    if (!ScreenHandler.canInsertItemIntoSlot(slot, itemStack3 = this.getCursorStack(), true) || !slot.canInsert(itemStack3) || this.quickCraftButton != 2 && itemStack3.getCount() <= this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot)) break block39;
                                                    this.quickCraftSlots.add(slot);
                                                    break block39;
                                                }
                                                if (this.quickCraftStage == 2) {
                                                    if (!this.quickCraftSlots.isEmpty()) {
                                                        if (this.quickCraftSlots.size() == 1) {
                                                            int j = this.quickCraftSlots.iterator().next().id;
                                                            this.endQuickCraft();
                                                            this.internalOnSlotClick(j, this.quickCraftButton, SlotActionType.PICKUP, player);
                                                            return;
                                                        }
                                                        ItemStack itemStack22 = this.getCursorStack().copy();
                                                        if (itemStack22.isEmpty()) {
                                                            this.endQuickCraft();
                                                            return;
                                                        }
                                                        int k = this.getCursorStack().getCount();
                                                        for (Slot slot2 : this.quickCraftSlots) {
                                                            ItemStack itemStack3 = this.getCursorStack();
                                                            if (slot2 == null || !ScreenHandler.canInsertItemIntoSlot(slot2, itemStack3, true) || !slot2.canInsert(itemStack3) || this.quickCraftButton != 2 && itemStack3.getCount() < this.quickCraftSlots.size() || !this.canInsertIntoSlot(slot2)) continue;
                                                            int l = slot2.hasStack() ? slot2.getStack().getCount() : 0;
                                                            int m = Math.min(itemStack22.getMaxCount(), slot2.getMaxItemCount(itemStack22));
                                                            int n = Math.min(ScreenHandler.calculateStackSize(this.quickCraftSlots, this.quickCraftButton, itemStack22) + l, m);
                                                            k -= n - l;
                                                            slot2.setStack(itemStack22.copyWithCount(n));
                                                        }
                                                        itemStack22.setCount(k);
                                                        this.setCursorStack(itemStack22);
                                                    }
                                                    this.endQuickCraft();
                                                } else {
                                                    this.endQuickCraft();
                                                }
                                                break block39;
                                            }
                                            if (this.quickCraftStage == 0) break block43;
                                            this.endQuickCraft();
                                            break block39;
                                        }
                                        if (actionType != SlotActionType.PICKUP && actionType != SlotActionType.QUICK_MOVE || button != 0 && button != 1) break block44;
                                        ClickType clickType2 = clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
                                        if (slotIndex != EMPTY_SPACE_SLOT_INDEX) break block45;
                                        if (this.getCursorStack().isEmpty()) break block39;
                                        if (clickType == ClickType.LEFT) {
                                            player.dropItem(this.getCursorStack(), true);
                                            this.setCursorStack(ItemStack.EMPTY);
                                        } else {
                                            player.dropItem(this.getCursorStack().split(1), true);
                                        }
                                        break block39;
                                    }
                                    if (actionType == SlotActionType.QUICK_MOVE) {
                                        if (slotIndex < 0) {
                                            return;
                                        }
                                        Slot slot = this.slots.get(slotIndex);
                                        if (!slot.canTakeItems(player)) {
                                            return;
                                        }
                                        ItemStack itemStack4 = this.quickMove(player, slotIndex);
                                        while (!itemStack4.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemStack4)) {
                                            itemStack4 = this.quickMove(player, slotIndex);
                                        }
                                    } else {
                                        if (slotIndex < 0) {
                                            return;
                                        }
                                        Slot slot = this.slots.get(slotIndex);
                                        ItemStack itemStack5 = slot.getStack();
                                        ItemStack itemStack4 = this.getCursorStack();
                                        player.onPickupSlotClick(itemStack4, slot.getStack(), clickType);
                                        if (selection >= 0) {
                                            if (itemStack5.getItem() instanceof RemadeBundle) {
                                                ((RemadeBundle) itemStack5.getItem()).selection = selection;
                                            }
                                        }
                                        if (!this.handleSlotClick(player, clickType, slot, itemStack5, itemStack4)) {
                                            if (itemStack5.isEmpty()) {
                                                if (!itemStack4.isEmpty()) {
                                                    int o = clickType == ClickType.LEFT ? itemStack4.getCount() : 1;
                                                    this.setCursorStack(slot.insertStack(itemStack4, o));
                                                }
                                            } else if (slot.canTakeItems(player)) {
                                                if (itemStack4.isEmpty()) {
                                                    int o = clickType == ClickType.LEFT ? itemStack5.getCount() : (itemStack5.getCount() + 1) / 2;
                                                    Optional<ItemStack> optional = slot.tryTakeStackRange(o, Integer.MAX_VALUE, player);
                                                    optional.ifPresent(stack -> {
                                                        this.setCursorStack((ItemStack)stack);
                                                        slot.onTakeItem(player, (ItemStack)stack);
                                                    });
                                                } else if (slot.canInsert(itemStack4)) {
                                                    if (ItemStack.canCombine(itemStack5, itemStack4)) {
                                                        int o = clickType == ClickType.LEFT ? itemStack4.getCount() : 1;
                                                        this.setCursorStack(slot.insertStack(itemStack4, o));
                                                    } else if (itemStack4.getCount() <= slot.getMaxItemCount(itemStack4)) {
                                                        this.setCursorStack(itemStack5);
                                                        slot.setStack(itemStack4);
                                                    }
                                                } else if (ItemStack.canCombine(itemStack5, itemStack4)) {
                                                    Optional<ItemStack> optional2 = slot.tryTakeStackRange(itemStack5.getCount(), itemStack4.getMaxCount() - itemStack4.getCount(), player);
                                                    optional2.ifPresent(stack -> {
                                                        itemStack4.increment(stack.getCount());
                                                        slot.onTakeItem(player, (ItemStack)stack);
                                                    });
                                                }
                                            }
                                        }
                                        slot.markDirty();
                                    }
                                    break block39;
                                }
                                if (actionType != SlotActionType.SWAP) break block46;
                                slot3 = this.slots.get(slotIndex);
                                itemStack2 = playerInventory.getStack(button);
                                itemStack = slot3.getStack();
                                if (itemStack2.isEmpty() && itemStack.isEmpty()) break block39;
                                if (!itemStack2.isEmpty()) break block47;
                                if (!slot3.canTakeItems(player)) break block39;
                                playerInventory.setStack(button, itemStack);
                                //slot3.onTake(itemStack.getCount());
                                slot3.setStack(ItemStack.EMPTY);
                                slot3.onTakeItem(player, itemStack);
                                break block39;
                            }
                            if (!itemStack.isEmpty()) break block48;
                            if (!slot3.canInsert(itemStack2)) break block39;
                            int p = slot3.getMaxItemCount(itemStack2);
                            if (itemStack2.getCount() > p) {
                                slot3.setStack(itemStack2.split(p));
                            } else {
                                playerInventory.setStack(button, ItemStack.EMPTY);
                                slot3.setStack(itemStack2);
                            }
                            break block39;
                        }
                        if (!slot3.canTakeItems(player) || !slot3.canInsert(itemStack2)) break block39;
                        int p = slot3.getMaxItemCount(itemStack2);
                        if (itemStack2.getCount() <= p) break block49;
                        slot3.setStack(itemStack2.split(p));
                        slot3.onTakeItem(player, itemStack);
                        if (playerInventory.insertStack(itemStack)) break block39;
                        player.dropItem(itemStack, true);
                        break block39;
                    }
                    playerInventory.setStack(button, itemStack);
                    slot3.setStack(itemStack2);
                    slot3.onTakeItem(player, itemStack);
                    break block39;
                }
                if (actionType != SlotActionType.CLONE || !player.getAbilities().creativeMode || !this.getCursorStack().isEmpty() || slotIndex < 0) break block50;
                Slot slot3 = this.slots.get(slotIndex);
                if (!slot3.hasStack()) break block39;
                ItemStack itemStack2 = slot3.getStack();
                this.setCursorStack(itemStack2.copyWithCount(itemStack2.getMaxCount()));
                break block39;
            }
            if (actionType == SlotActionType.THROW && this.getCursorStack().isEmpty() && slotIndex >= 0) {
                Slot slot3 = this.slots.get(slotIndex);
                int j = button == 0 ? 1 : slot3.getStack().getCount();
                ItemStack itemStack = slot3.takeStackRange(j, Integer.MAX_VALUE, player);
                player.dropItem(itemStack, true);
            } else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
                Slot slot3 = this.slots.get(slotIndex);
                ItemStack itemStack2 = this.getCursorStack();
                if (!(itemStack2.isEmpty() || slot3.hasStack() && slot3.canTakeItems(player))) {
                    int k = button == 0 ? 0 : this.slots.size() - 1;
                    int p = button == 0 ? 1 : -1;
                    for (int o = 0; o < 2; ++o) {
                        for (int q = k; q >= 0 && q < this.slots.size() && itemStack2.getCount() < itemStack2.getMaxCount(); q += p) {
                            Slot slot4 = this.slots.get(q);
                            if (!slot4.hasStack() || !ScreenHandler.canInsertItemIntoSlot(slot4, itemStack2, true) || !slot4.canTakeItems(player) || !this.canInsertIntoSlot(itemStack2, slot4)) continue;
                            ItemStack itemStack5 = slot4.getStack();
                            if (o == 0 && itemStack5.getCount() == itemStack5.getMaxCount()) continue;
                            ItemStack itemStack6 = slot4.takeStackRange(itemStack5.getCount(), itemStack2.getMaxCount() - itemStack2.getCount(), player);
                            itemStack2.increment(itemStack6.getCount());
                        }
                    }
                }
            }
        }
    }
}
*/
}