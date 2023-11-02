package dcq.mods.mixin.client;

import dcq.mods.References;
import dcq.mods.utils.BundleRefer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParentElement.class)
public interface ScreenInterceptor extends Element {
    @Inject(method = "mouseScrolled", at = @At("HEAD"))
    private void onScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if (!BundleRefer.active) {return;}
        if (verticalAmount <= -1.0) {
            if (BundleRefer.scroll != BundleRefer.scroll_max) {
                BundleRefer.scroll = BundleRefer.scroll + 1;
            }
        } else if (verticalAmount >= 1.0) {
            if (BundleRefer.scroll > 0) {
                BundleRefer.scroll = BundleRefer.scroll - 1;
            }
        }
    }

    @Inject(method = "keyReleased", at = @At("HEAD"))
    private void onKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!BundleRefer.active) {return;}
        if (keyCode == InputUtil.GLFW_KEY_D) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(true);
            ClientPlayNetworking.send(new Identifier(References.MOD_ID,"remadebundlekey"),buf);
            if (BundleRefer.scroll != BundleRefer.scroll_max) {
                BundleRefer.scroll = BundleRefer.scroll + 1;
            }
        } else if (keyCode == InputUtil.GLFW_KEY_A) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(false);
            ClientPlayNetworking.send(new Identifier(References.MOD_ID,"remadebundlekey"),buf);
            if (BundleRefer.scroll > 0) {
                BundleRefer.scroll = BundleRefer.scroll - 1;
            }
        }




    }
}