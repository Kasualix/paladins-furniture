package com.unlikepaladin.pfm.blocks;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;


public class KitchenCounter extends HorizontalFacingBlock {
    private float height = 0.36f;
    private final Block baseBlock;
    public static final EnumProperty<CounterShape> SHAPE = EnumProperty.of("shape", CounterShape.class);

    private final BlockState baseBlockState;
    public KitchenCounter(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
        this.baseBlockState = this.getDefaultState();
        this.baseBlock = baseBlockState.getBlock();
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(SHAPE);
    }
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        BlockState blockState = this.getDefaultState().with(FACING, ctx.getPlayerFacing());
        return blockState.with(SHAPE, getShape(blockState, world, blockPos));
    }
    private static CounterShape getShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction3 = null;
        Object direction2;
        Direction direction = state.get(FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        boolean right = isCounter(world, pos, state.get(FACING).rotateYCounterclockwise(), state.get(FACING));
        boolean left = isCounter(world, pos, state.get(FACING).rotateYClockwise(), state.get(FACING));

        if (isCounter(blockState) && ((Direction)(direction2 = blockState.get(FACING))).getAxis() != state.get(FACING).getAxis() && isDifferentOrientation(state, world, pos, ((Direction)direction2).getOpposite())) {
            if (direction2 == direction.rotateYCounterclockwise()) {
                return CounterShape.OUTER_LEFT;
            }
            return CounterShape.OUTER_RIGHT;
        }
        direction2 = world.getBlockState(pos.offset(direction.getOpposite()));
        boolean innerCorner = isCounter((BlockState)direction2) && (direction3 = (Direction) ((State)direction2).get(FACING)).getAxis() != state.get(FACING).getAxis() && isDifferentOrientation(state, world, pos, direction3);
        if (innerCorner) {
            if (direction3 == direction.rotateYCounterclockwise()) {
                return CounterShape.INNER_LEFT;
            }
            return CounterShape.INNER_RIGHT;
        }
        if (left && right) {
            return CounterShape.STRAIGHT;
        }
        else if (left) {
            return CounterShape.LEFT_EDGE;
        }
        else if (right) {
            return CounterShape.RIGHT_EDGE;
        }
        return CounterShape.STRAIGHT;
    }


    private static boolean isCounter(BlockView world, BlockPos pos, Direction direction, Direction tableDirection)
    {
        BlockState state = world.getBlockState(pos.offset(direction));
        if(state.getBlock() instanceof KitchenCounter)
        {
            Direction sourceDirection = state.get(FACING);
            return state.getBlock() instanceof KitchenCounter;
        }
        return false;
    }

    private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !KitchenCounter.isCounter(blockState); //|| blockState.get(FACING) != state.get(FACING);
    }
    public static boolean isCounter(BlockState state) {
        return state.getBlock() instanceof KitchenCounter;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        return direction.getAxis().isHorizontal() ? state.with(SHAPE, getShape(state, world, pos)) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }




    @SuppressWarnings("deprecated")


    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!state.isOf(state.getBlock())) {
            this.baseBlockState.neighborUpdate(world, pos, Blocks.AIR, pos, false);
            this.baseBlock.onBlockAdded(this.baseBlockState, world, pos, oldState, false);
        }
    }
    /**
     * Method to rotate VoxelShapes from this random Forge Forums thread: https://forums.minecraftforge.net/topic/74979-1144-rotate-voxel-shapes/
     */
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }


    protected static final VoxelShape STRAIGHT = VoxelShapes.union(createCuboidShape(0, 0, 0,16, 1, 12), createCuboidShape(0, 1, 0,16, 14, 13), createCuboidShape(0, 14, 0,16, 16, 16));
    protected static final VoxelShape INNER_CORNER = VoxelShapes.union(createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(0, 1, 0,16, 14, 13),createCuboidShape(3, 1, 13,16, 14, 16));
    protected static final VoxelShape OUTER_CORNER = VoxelShapes.union(createCuboidShape(0, 14, 0,16, 16, 16),createCuboidShape(0, 1, 0,13, 14, 13),createCuboidShape(0, 0, 0,12, 1, 12));
    protected static final VoxelShape LEFT_EDGE = VoxelShapes.union(createCuboidShape(2, 0, 0,16, 1, 12), createCuboidShape(2, 1, 0,16, 14, 13), createCuboidShape(0, 0, 0,2, 14, 16),createCuboidShape(0, 14, 0,16, 16, 16));
    protected static final VoxelShape RIGHT_EDGE = VoxelShapes.union(createCuboidShape(0, 0, 0,14, 1, 12), createCuboidShape(0, 1, 0,14, 14, 13), createCuboidShape(14, 0, 0,16, 14, 16),createCuboidShape(0, 14, 0,16, 16, 16));

    @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(FACING);
        CounterShape shape = state.get(SHAPE);
        switch(shape) {
            case STRAIGHT:
                if(dir.equals(Direction.NORTH))
                    return STRAIGHT;
                else if (dir.equals(Direction.SOUTH))
                    return rotateShape(Direction.NORTH, Direction.SOUTH, STRAIGHT);
                else if (dir.equals(Direction.EAST))
                    return rotateShape(Direction.NORTH, Direction.EAST, STRAIGHT);
                else
                    return rotateShape(Direction.NORTH, Direction.WEST, STRAIGHT);
            case INNER_LEFT:
                if(dir.equals(Direction.NORTH))
                    return rotateShape(Direction.NORTH, Direction.WEST, INNER_CORNER);
                else if (dir.equals(Direction.SOUTH))
                    return rotateShape(Direction.NORTH, Direction.EAST, INNER_CORNER);
                else if (dir.equals(Direction.EAST))
                    return INNER_CORNER;
                else
                    return rotateShape(Direction.NORTH, Direction.SOUTH, INNER_CORNER);

            case INNER_RIGHT:
                if(dir.equals(Direction.NORTH))
                    return INNER_CORNER;
                else if (dir.equals(Direction.SOUTH))
                    return rotateShape(Direction.NORTH, Direction.SOUTH, INNER_CORNER);
                else if (dir.equals(Direction.EAST))
                    return rotateShape(Direction.NORTH, Direction.EAST, INNER_CORNER);
                else
                    return rotateShape(Direction.NORTH, Direction.WEST, INNER_CORNER);
            case OUTER_LEFT:
                if(dir.equals(Direction.NORTH))
                    return OUTER_CORNER;
                else if (dir.equals(Direction.SOUTH))
                    return rotateShape(Direction.NORTH, Direction.SOUTH, OUTER_CORNER);
                else if (dir.equals(Direction.EAST))
                    return rotateShape(Direction.NORTH, Direction.EAST, OUTER_CORNER);
                else
                    return rotateShape(Direction.NORTH, Direction.WEST, OUTER_CORNER);
            case OUTER_RIGHT:
                if(dir.equals(Direction.NORTH))
                    return rotateShape(Direction.NORTH, Direction.EAST, OUTER_CORNER);
                else if (dir.equals(Direction.SOUTH))
                    return rotateShape(Direction.NORTH, Direction.WEST, OUTER_CORNER);
                else if (dir.equals(Direction.EAST))
                    return rotateShape(Direction.NORTH, Direction.SOUTH, OUTER_CORNER);
                else
                    return OUTER_CORNER;
            case LEFT_EDGE:
                if(dir.equals(Direction.NORTH))
                    return LEFT_EDGE;
                else if (dir.equals(Direction.SOUTH))
                    return rotateShape(Direction.NORTH, Direction.SOUTH, LEFT_EDGE);
                else if (dir.equals(Direction.EAST))
                    return rotateShape(Direction.NORTH, Direction.EAST, LEFT_EDGE);
                else
                    return rotateShape(Direction.NORTH, Direction.WEST, LEFT_EDGE);
            case RIGHT_EDGE:
                if(dir.equals(Direction.NORTH))
                    return RIGHT_EDGE;
                else if (dir.equals(Direction.SOUTH))
                    return rotateShape(Direction.NORTH, Direction.SOUTH, RIGHT_EDGE);
                else if (dir.equals(Direction.EAST))
                    return rotateShape(Direction.NORTH, Direction.EAST, RIGHT_EDGE);
                else
                    return rotateShape(Direction.NORTH, Direction.WEST, RIGHT_EDGE);
            default:
                return STRAIGHT;
        }
    }


}

enum CounterShape implements StringIdentifiable
{
    STRAIGHT("straight"),
    INNER_LEFT("inner_left"),
    INNER_RIGHT("inner_right"),
    OUTER_LEFT("outer_left"),
    OUTER_RIGHT("outer_right"),
    LEFT_EDGE("left_edge"),
    RIGHT_EDGE("right_edge");

    private final String name;

    CounterShape(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
