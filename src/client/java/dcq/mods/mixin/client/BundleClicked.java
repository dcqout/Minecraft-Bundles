package dcq.mods.mixin.client;

import com.google.common.collect.Lists;
import dcq.mods.References;
import dcq.mods.Created.item.RemadeBundle;
import dcq.mods.utils.BundleRefer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ClientPlayerInteractionManager.class)
public class BundleClicked {
    @Shadow private final ClientPlayNetworkHandler networkHandler;

    public BundleClicked(ClientPlayNetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    @Overwrite
    public void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player) {
        ScreenHandler screenHandler = player.currentScreenHandler;
        if (syncId != screenHandler.syncId) {
            References.LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", (Object)syncId, (Object)screenHandler.syncId);
            return;
        }
        DefaultedList<Slot> defaultedList = screenHandler.slots;
        int i = defaultedList.size();
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(i);
        for (Slot slot : defaultedList) {
            list.add(slot.getStack().copy());
        }
        if (BundleRefer.active && button == 1) {
            References.LOGGER.info(String.valueOf(BundleRefer.active));
            button = 65+BundleRefer.scroll;
        }
        screenHandler.onSlotClick(slotId, button, actionType, player);
        Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<ItemStack>();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2;
            ItemStack itemStack = (ItemStack)list.get(j);
            if (ItemStack.areEqual((ItemStack)itemStack, (ItemStack)(itemStack2 = ((Slot)defaultedList.get(j)).getStack()))) continue;
            int2ObjectMap.put(j, itemStack2.copy());
        }
        this.networkHandler.sendPacket((Packet<?>) new ClickSlotC2SPacket(syncId, screenHandler.getRevision(), slotId, button, actionType, screenHandler.getCursorStack().copy(), int2ObjectMap));
        }
}
