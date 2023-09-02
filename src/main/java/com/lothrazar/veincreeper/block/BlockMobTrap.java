package com.lothrazar.veincreeper.block;

import java.util.function.Predicate;
import com.lothrazar.library.block.EntityBlockFlib;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.recipe.TrapRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class BlockMobTrap extends EntityBlockFlib implements SimpleWaterloggedBlock {

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<AttachFace> ATTACH_FACE = BlockStateProperties.ATTACH_FACE;
  protected static final VoxelShape AABB_CEILING_X = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_CEILING_Z = Block.box(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
  protected static final VoxelShape AABB_FLOOR_X = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
  protected static final VoxelShape AABB_FLOOR_Z = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
  protected static final VoxelShape AABB_NORTH = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
  protected static final VoxelShape AABB_WEST = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_EAST = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
  boolean sneakPlayerAvoid = true;
  private boolean requiresRedstoneSignal = false;

  public BlockMobTrap(Properties prop) {
    super(prop.strength(10, 1200));
    this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(ATTACH_FACE, AttachFace.WALL));
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileMobTrap(pos, state);
  }

  @Override
  @SuppressWarnings("deprecation")
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  public static final Predicate<Entity> DYE_FINDER = (p) -> {
    return p.isAlive() && p instanceof ItemEntity
        && ((ItemEntity) p).getItem().getCount() > 0
    //        && ((ItemEntity) p).getItem().getItem() instanceof DyeItem
    ;
  };

  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    if (sneakPlayerAvoid && entity instanceof Player && ((Player) entity).isCrouching()) {
      return;
    }
    //are you alive instanceof LivingEntity alive
    if (this.requiresRedstoneSignal && !level.hasNeighborSignal(pos)) {
      return; //i need signal to work, and there aint one
    }
    //else i dont need it. (or i do and i has it))
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (!level.isClientSide &&
        blockEntity instanceof TileMobTrap) {
      var caps = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
      if (caps == null) {
        return;
      }
      ItemStack dyeFound = caps.getStackInSlot(0);
      if (dyeFound.isEmpty()) {
        return;
      }
      for (TrapRecipe recipe : level.getRecipeManager().getAllRecipesFor(CreeperRegistry.TRAP_RECIPE.get())) {
        if (recipe.matches(level, dyeFound, entity)) {
          VeinCreeperMod.LOGGER.info(dyeFound + "Found  match " + entity + " vs recipe" + recipe.toString());
          //give result
          recipe.spawnEntityResult((ServerLevel) level, pos, entity); //pay cost 
          //          dyeFound.shrink(1);
          caps.extractItem(0, 1, false);
          if (caps.getStackInSlot(0).isEmpty()) {
            level.markAndNotifyBlock(pos, level.getChunkAt(pos), state, state, UPDATE_ALL_IMMEDIATE, UPDATE_ALL);
          }
        }
      }
    }
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    Direction direction = state.getValue(HORIZONTAL_FACING);
    switch (state.getValue(ATTACH_FACE)) {
      case FLOOR:
        if (direction.getAxis() == Direction.Axis.X) {
          return AABB_FLOOR_X;
        }
        return AABB_FLOOR_Z;
      case WALL:
        switch (direction) {
          case EAST:
            return AABB_EAST;
          case WEST:
            return AABB_WEST;
          case SOUTH:
            return AABB_SOUTH;
          case NORTH:
          default:
            return AABB_NORTH;
        }
      case CEILING:
      default:
        if (direction.getAxis() == Direction.Axis.X) {
          return AABB_CEILING_X;
        }
        else {
          return AABB_CEILING_Z;
        }
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    for (Direction direction : context.getNearestLookingDirections()) {
      BlockState blockstate;
      if (direction.getAxis() == Direction.Axis.Y) {
        blockstate = this.defaultBlockState().setValue(ATTACH_FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
      }
      else {
        blockstate = this.defaultBlockState().setValue(ATTACH_FACE, AttachFace.WALL).setValue(HORIZONTAL_FACING, direction.getOpposite());
      }
      if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
        return blockstate.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
      }
    }
    return null;
  }

  @Override
  public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
    return true;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(HORIZONTAL_FACING).add(ATTACH_FACE).add(WATERLOGGED);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      BlockEntity tileentity = worldIn.getBlockEntity(pos);
      if (tileentity instanceof TileMobTrap grinder) {
        var cap = tileentity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        for (int i = 0; i < cap.getSlots(); ++i) {
          Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), cap.getStackInSlot(i));
        }
        worldIn.updateNeighbourForOutputSignal(pos, this);
      }
      super.onRemove(state, worldIn, pos, newState, isMoving);
    }
  }
}
