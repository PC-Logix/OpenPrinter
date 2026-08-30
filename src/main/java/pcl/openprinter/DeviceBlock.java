package pcl.openprinter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public final class DeviceBlock extends Block implements EntityBlock {
    public enum Kind { PRINTER, SHREDDER, FILE_CABINET, BRIEFCASE }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape BRIEFCASE_SHAPE = Block.box(1, 0, 6, 15, 3, 16);
    private final Kind kind;

    public DeviceBlock(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public Kind kind() {
        return kind;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return Block.CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction placementFacing = context.getHorizontalDirection();
        return defaultBlockState().setValue(FACING,
                kind == Kind.PRINTER ? placementFacing : placementFacing.getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (kind == Kind.BRIEFCASE && level.getBlockEntity(pos) instanceof StorageBlockEntity storage) {
            storage.loadFromPortable(stack, level.registryAccess());
        }
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof InventoryDevice device) {
            serverPlayer.openMenu(device, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (kind != Kind.BRIEFCASE && !state.is(newState.getBlock()) && !level.isClientSide
                && level.getBlockEntity(pos) instanceof InventoryDevice device) {
            device.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (kind == Kind.BRIEFCASE) {
            ItemStack briefcase = new ItemStack(OpenPrinter.BRIEFCASE.get());
            if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof StorageBlockEntity storage) {
                storage.saveToPortable(briefcase, params.getLevel().registryAccess());
            }
            return List.of(briefcase);
        }
        return super.getDrops(state, params);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return kind == Kind.BRIEFCASE ? BRIEFCASE_SHAPE : Shapes.block();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return switch (kind) {
            case PRINTER -> new PrinterBlockEntity(pos, state);
            case SHREDDER -> new ShredderBlockEntity(pos, state);
            case FILE_CABINET -> new StorageBlockEntity(pos, state, 30);
            case BRIEFCASE -> new StorageBlockEntity(pos, state, 18);
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (kind == Kind.PRINTER && type == OpenPrinter.PRINTER_BE.get()) {
            return (world, pos, currentState, blockEntity) -> ((PrinterBlockEntity) blockEntity).serverTick();
        }
        if (kind == Kind.SHREDDER && type == OpenPrinter.SHREDDER_BE.get()) {
            return (world, pos, currentState, blockEntity) -> ((ShredderBlockEntity) blockEntity).serverTick();
        }
        return null;
    }
}
