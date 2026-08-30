package pcl.openprinter;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public final class PortableFolderItem extends Item {
    public PortableFolderItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack folder = player.getItemInHand(hand);
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack other = player.getItemInHand(otherHand);
        if (player.isCrouching() && other.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide) {
                CustomData.update(DataComponents.CUSTOM_DATA, folder,
                        tag -> tag.putInt("FolderColor", dye.getDyeColor().getTextureDiffuseColor()));
                if (!player.getAbilities().instabuild) other.shrink(1);
            }
            return InteractionResultHolder.sidedSuccess(folder, level.isClientSide);
        }
        if (player.isCrouching()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                open(serverPlayer, hand, 9, Component.translatable("container.openprinter.folder"));
            }
        } else if (level.isClientSide) {
            ClientRegistration.openFolderView(folder.copy());
        }
        return InteractionResultHolder.sidedSuccess(folder, level.isClientSide);
    }

    static void open(ServerPlayer player, InteractionHand hand, int size, Component title) {
        player.openMenu(new SimpleMenuProvider(
                (id, inventory, ignored) -> new PortableStorageMenu(id, inventory, hand, size), title),
                buffer -> {
                    buffer.writeEnum(hand);
                    buffer.writeVarInt(size);
                });
    }
}
