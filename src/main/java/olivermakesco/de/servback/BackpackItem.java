package olivermakesco.de.servback;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BackpackItem extends Item implements PolymerItem {
    int slots;
    String name;

    public BackpackItem(Settings settings, int slots) {
        super(settings.maxCount(1));
        this.slots = slots;
        if (slots == 9)
            name = "Small";
        if (slots == 18)
            name = "Medium";
        if (slots == 27)
            name = "Large";
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.of(name + " Backpack");
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        var cast = user.raycast(5,0,false);
        if (cast.getType() == HitResult.Type.BLOCK) return TypedActionResult.pass(stack);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.pass(stack);
        if (player.isSneaking()) return TypedActionResult.pass(stack);
        new BackpackGui(player,slots,stack);
        return TypedActionResult.success(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getPlayer() instanceof ServerPlayerEntity player)) return ActionResult.PASS;
        if (player.isSneaking()) return ActionResult.PASS;
        new BackpackGui(player,slots,context.getStack());
        return ActionResult.PASS;
    }

    public Item getPolymerItem() {
        return Items.BUNDLE;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return getPolymerItem();
    }

    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        ItemStack stack = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, lookup, player);
        // TODO: MAKE THIS WORK
         NbtCompound nbt = stack.getOrCreateNbt().copy();
         nbt.putInt("backpack",slots/9); // set inventory size

        ComponentMap.Builder newComp = ComponentMap.builder();
        newComp.add(DataComponentTypes.CUSTOM_NAME, Text.literal("Backpack"));
        stack.applyComponentsFrom(newComp.build());
        return stack;
    }
}
