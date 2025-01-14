package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.blocks.blockentities.TrashcanBlockEntity;
import com.unlikepaladin.pfm.registry.Statistics;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Trashcan extends BlockWithEntity {
    public Trashcan(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(OPEN, false));
    }
    protected static final BooleanProperty OPEN = Properties.OPEN;

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public static final VoxelShape TRASHCAN = VoxelShapes.union(createCuboidShape(2, 0, 2, 13, 13, 13), createCuboidShape(1, 12, 1, 14, 16, 14), createCuboidShape(3.5, 16, 6.5,11.5, 18, 8.5));
    public static final VoxelShape TRASHCAN_OPEN = VoxelShapes.union(createCuboidShape(2, 0, 2, 13, 13, 13), createCuboidShape(10.5, 0, 0, 19.5, 13, 14));

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean open = state.get(OPEN);
        if(open) {
            return TRASHCAN_OPEN;
        }
        return TRASHCAN;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntity(pos, state);
    }
    @ExpectPlatform
    public static BlockEntity getBlockEntity(BlockPos pos, BlockState state) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void openScreen(PlayerEntity player, BlockState state, World world, BlockPos pos) {
        return;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TrashcanBlockEntity) {
            openScreen(player, state, world, pos);
            player.incrementStat(Statistics.TRASHCAN_OPENED);
        }
        return ActionResult.CONSUME;
    }

}
