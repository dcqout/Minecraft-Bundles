package dcq.mods.Created.item;

import dcq.mods.References;
import dcq.mods.Registers.ModItems;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.print.attribute.standard.Finishings;
import javax.print.attribute.standard.PageRanges;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;

public class StickyBundle extends Item {
    private static final String ITEMS_KEY = "Items";
    private static final int STACKS = 4;
    public static final int MAX_STORAGE = 64 * STACKS;
    private static final int OVER_COST = STACKS*2;
    private static final int STACK_COST = 1;
    private static final int BUNDLE_ITEM_OCCUPANCY = 16;
    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.2f, 0.3f, 0.4f);
    public StickyBundle(Settings settings) {
        super(settings);
    }

    public static void init() {
        References.LOGGER.info("Initialized");
    }

    public static float getAmountFilled(ItemStack stack) {
        return (float) getBundleOccupancy(stack) / 256.0f;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        }
        ItemStack itemStack = slot.getStack();
        if (itemStack.isEmpty()) {
            this.playRemoveOneSound(player);
            removeFirstStackItem(stack).ifPresent(removedStack -> addToBundle(stack, slot.insertStack((ItemStack)removedStack)));
        } else if (itemStack.getItem().canBeNested()) {
            int i = (MAX_STORAGE - getBundleOccupancy(stack)) / getItemCost(stack,itemStack);
            int j = addToBundle(stack, slot.takeStackRange(itemStack.getCount(), i, player));
            if (j > 0) {
                this.playInsertSound(player);
            }
        }
        return true;
    }
    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return false;
        }
        int selection = 0;
        if (stack.getSubNbt("Selection") != null) {
            selection = stack.getSubNbt("Selection").getInt("value");
        }
        if (otherStack.isEmpty()) {
            if (false == true){//InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),InputUtil.GLFW_KEY_LEFT_CONTROL)) {
                removeFirstStack(selection,stack).ifPresent(itemStack -> {
                    this.playRemoveOneSound(player);
                    cursorStackReference.set((ItemStack)itemStack);
                });
            } else {
                removeFirstStack(selection,stack).ifPresent(itemStack -> {
                    this.playRemoveOneSound(player);
                    cursorStackReference.set((ItemStack)itemStack);
                });}
        } else {
            int i = addToBundle(stack, otherStack);
            if (i > 0) {
                this.playInsertSound(player);
                otherStack.decrement(i);
            }
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (dropAllBundledItems(itemStack, user)) {
            this.playDropContentsSound(user);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getBundleOccupancy(stack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * getBundleOccupancy(stack) / MAX_STORAGE, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }
    private static int addToBundle(ItemStack bundle, ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().canBeNested()) {
            return 0;
        }
        NbtCompound nbtCompound = bundle.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) {
            nbtCompound.put(ITEMS_KEY, new NbtList());
        }
        int i = 0;
        int j = 0;
        int k = 0;
        if (getItemCost(bundle,stack) == STACK_COST) {
            References.LOGGER.info("using stack_cost");
            i = getBundleOccupancy(bundle);
            j = STACK_COST;
            k = Math.min(stack.getCount(),(MAX_STORAGE - i) / j);
        } else {
            References.LOGGER.info("using over_cost");
            i = getBundleOver(MAX_STORAGE,bundle);
            j = getItemCost(bundle, stack);
            k = Math.min(stack.getCount(),(MAX_STORAGE - i) / j);
        }
        References.LOGGER.info("k is "+k);
        if (k <= 0) {
            return 0;
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
        if (optional.isPresent()) {
            NbtCompound nbtCompound2 = optional.get();
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
            int newv = itemStack.getCount() + k;
            int mins = 64 - itemStack.getCount();
            int v = k;
            if (newv > 64) {
                if (mins > 0) {
                    if (v - mins >= 0) {
                        v = v - mins;
                        itemStack.increment(mins);
                        itemStack.writeNbt(nbtCompound2);
                        nbtList.remove(nbtCompound2);
                        nbtList.add(0, nbtCompound2);
                    }
                }
                if (v > 0) {
                    ItemStack itemStack2 = stack.copyWithCount(v);
                    NbtCompound nbtCompound3 = new NbtCompound();
                    itemStack2.writeNbt(nbtCompound3);
                    nbtList.add(0, nbtCompound3);
                }
            } else {
                itemStack.increment(k);
                itemStack.writeNbt(nbtCompound2);
                nbtList.remove(nbtCompound2);
                nbtList.add(0, nbtCompound2);
            }
        } else {
            ItemStack itemStack2 = stack.copyWithCount(k);
            NbtCompound nbtCompound3 = new NbtCompound();
            itemStack2.writeNbt(nbtCompound3);
            nbtList.add(0, nbtCompound3);
        }
        return k;
    }

    private static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        if (stack.isOf(Items.BUNDLE)) {
            return Optional.empty();
        }
        return items.stream().filter(NbtCompound.class::isInstance).map(NbtCompound.class::cast).filter(item -> ItemStack.canCombine(ItemStack.fromNbt(item), stack)).findFirst();
    }

    private static int getBundleCost(int max,ItemStack bundle,ItemStack stack) {
        NbtCompound nbtCompound;
        if (stack.isOf(ModItems.BUNDLE) || stack.isOf(ModItems.STICKY_BUNDLE) || (stack.isOf(Items.BEEHIVE) || stack.isOf(Items.BEE_NEST)) && stack.hasNbt() && (nbtCompound = BlockItem.getBlockEntityNbt(stack)) != null && !nbtCompound.getList("Bees", NbtElement.COMPOUND_TYPE).isEmpty()) {
            return max*2;
        } else if (stack.getMaxCount() == 64) {
            return (int) (((float)max/(float)64) * (float)2);
        }
        return max / (stack.getMaxCount()*2);
    }

    private static boolean BundleTypesDo(ItemStack bundle) {
        NbtCompound nbtBundle = bundle.getOrCreateNbt();
        NbtList nbtlist = nbtBundle.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        ItemStack stack = ItemStack.fromNbt(nbtlist.getCompound(0));
        boolean value = true;
        for (int i = 1;i<nbtlist.size();i++) {
            Item item = ItemStack.fromNbt(nbtlist.getCompound(i)).getItem();
            if (!stack.isOf(item)) {
                value = false;
                break;
            }
        }
        return value;
    }

    private static int getItemCost(ItemStack bundle,ItemStack stack) {
        NbtCompound nbtCompound;
        if (stack.isOf(ModItems.BUNDLE) || stack.isOf(ModItems.STICKY_BUNDLE) || (stack.isOf(Items.BEEHIVE) || stack.isOf(Items.BEE_NEST)) && stack.hasNbt() && (nbtCompound = BlockItem.getBlockEntityNbt(stack)) != null && !nbtCompound.getList("Bees", NbtElement.COMPOUND_TYPE).isEmpty()) {
            return MAX_STORAGE;
        }
        NbtCompound nbtBundle = bundle.getOrCreateNbt();
        Item item = null;
        int iammt = 0;
        if (nbtBundle.contains(ITEMS_KEY)) {
            NbtList nbtlist = nbtBundle.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
            item = ItemStack.fromNbt(nbtlist.getCompound(0)).getItem();
            iammt = nbtlist.size();
        }
        if (stack.getMaxCount() == 64) {
            if (iammt == 0 || BundleTypesDo(bundle) && stack.isOf(item)) {
                return STACK_COST;
            }
            return OVER_COST;
        }
        return MAX_STORAGE / (stack.getMaxCount()*2);
    }

    private static int getBundleOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt(itemStack -> getItemCost(stack,itemStack) * itemStack.getCount()).sum();
    }
    private static int getBundleOver(int max,ItemStack stack) {
        return getBundledStacks(stack).mapToInt(itemStack -> getBundleCost(max,stack,itemStack) * itemStack.getCount()).sum();
    }

    private static Optional<ItemStack> removeFirstStackItem(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) {
            return Optional.empty();
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        if (nbtList.isEmpty()) {
            return Optional.empty();
        }
        boolean i = false;
        int numberr = 0;
        NbtCompound nbtCompound2 = nbtList.getCompound(numberr);
        ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
        nbtList.remove(numberr);
        if (nbtList.isEmpty()) {
            stack.removeSubNbt(ITEMS_KEY);
        }
        return Optional.of(itemStack);
    }

    private static Optional<ItemStack> removeFirstStack(int sel, ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) {
            return Optional.empty();
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        if (nbtList.isEmpty()) {
            return Optional.empty();
        }
        boolean i = false;
        int numberr = sel;
        NbtCompound nbtCompound2 = nbtList.getCompound(numberr);
        ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
        nbtList.remove(numberr);
        if (nbtList.isEmpty()) {
            stack.removeSubNbt(ITEMS_KEY);
        }
        return Optional.of(itemStack);
    }

    private static boolean dropAllBundledItems(ItemStack stack, PlayerEntity player) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!player.isSneaking() || !nbtCompound.contains(ITEMS_KEY)) {
            return false;
        }
        if (player instanceof ServerPlayerEntity) {
            NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound2 = nbtList.getCompound(i);
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                References.LOGGER.info("drop "+itemStack.getCount());
                player.dropItem(itemStack, true);
            }
        }
        stack.removeSubNbt(ITEMS_KEY);
        return true;
    }

    private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        return nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        getBundledStacks(stack).forEach(defaultedList::add);
        return Optional.of(new BundleTooltipData(defaultedList,getBundleOccupancy(stack)+100));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        boolean type = BundleTypesDo(stack);
        tooltip.add(Text.translatable("item.minecraft.bundle.fullness", type ? getBundleOccupancy(stack) : getBundleOver(32,stack),type ? MAX_STORAGE : 32).formatted(Formatting.GRAY));
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getBundledStacks(entity.getStack()));
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }
}
