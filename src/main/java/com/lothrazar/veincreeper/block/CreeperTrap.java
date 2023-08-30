package com.lothrazar.veincreeper.block;

import java.util.function.Predicate;
import com.lothrazar.library.block.EntityBlockFlib;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.recipe.TrapRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
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
import net.minecraftforge.registries.ForgeRegistries;

public class CreeperTrap extends EntityBlockFlib implements SimpleWaterloggedBlock {

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
  protected static final VoxelShape AABB_CEILING_X_OFF = Block.box(0.0D, 14.0D, 0.0D,
      16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_CEILING_Z_OFF = Block.box(5.0D, 14.0D, 6.0D,
      11.0D, 16.0D, 10.0D);
  protected static final VoxelShape AABB_FLOOR_X_OFF = Block.box(0.0D, 0.0D, 0.0D,
      16.0D, 2.0D, 16.0D);
  protected static final VoxelShape AABB_FLOOR_Z_OFF = Block.box(0.0D, 0.0D, 0.0D,
      16.0D, 2.0D, 16.0D);
  protected static final VoxelShape AABB_NORTH_OFF = Block.box(0.0D, 0.0D, 14.0D,
      16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_SOUTH_OFF = Block.box(0.0D, 0.0D, 0.0D,
      16.0D, 16.0D, 2.0D);
  protected static final VoxelShape AABB_WEST_OFF = Block.box(14.0D, 0.0D, 0.0D,
      16.0D, 16.0D, 16.0D);
  protected static final VoxelShape AABB_EAST_OFF = Block.box(0.0D, 0.0D, 0.0D,
      2.0D, 16.0D, 16.0D);
  boolean sneakPlayerAvoid = true;

  public CreeperTrap(Properties prop) {
    super(prop);
    this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileCreeperTrap(pos, state);
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
    if (!level.isClientSide &&
        level.hasNeighborSignal(pos) &&
        level.getBlockEntity(pos) instanceof TileCreeperTrap &&
        entity instanceof Creeper alive) {
      var caps = level.getBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
      //
      ItemStack dyeFound = caps.getStackInSlot(0);
      for (TrapRecipe recipe : level.getRecipeManager().getAllRecipesFor(CreeperRegistry.RECIPE_TRAP.get())) {
        // 
        System.out.println(dyeFound + "Test match " + entity + " vs recipe");
        if (recipe.matches(level, dyeFound, entity)) {
          //gogogo 
          //          c.setPos(pos.getCenter());
          //          level.addFreshEntity(c);
          //does the recipe match? yes? ok
          alive.remove(Entity.RemovalReason.KILLED); // .kill();
          dyeFound.shrink(1);
          //is it single use?
          //          level.destroyBlock(pos, true);
          //
          var trapped = ForgeRegistries.ENTITY_TYPES.getValue(recipe.getTransformedEntity());
          //
          trapped.spawn((ServerLevel) level, pos, MobSpawnType.CONVERSION);
        }
      }
    }
    if (entity instanceof Player) {}
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    Direction direction = state.getValue(HORIZONTAL_FACING);
    switch (state.getValue(FACE)) {
      case FLOOR:
        if (direction.getAxis() == Direction.Axis.X) {
          return AABB_FLOOR_X_OFF;
        }
        return AABB_FLOOR_Z_OFF;
      case WALL:
        switch (direction) {
          case EAST:
            return AABB_EAST_OFF;
          case WEST:
            return AABB_WEST_OFF;
          case SOUTH:
            return AABB_SOUTH_OFF;
          case NORTH:
          default:
            return AABB_NORTH_OFF;
        }
      case CEILING:
      default:
        if (direction.getAxis() == Direction.Axis.X) {
          return AABB_CEILING_X_OFF;
        }
        else {
          return AABB_CEILING_Z_OFF;
        }
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    for (Direction direction : context.getNearestLookingDirections()) {
      BlockState blockstate;
      if (direction.getAxis() == Direction.Axis.Y) {
        blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
      }
      else {
        blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(HORIZONTAL_FACING, direction.getOpposite());
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
    builder.add(HORIZONTAL_FACING).add(FACE).add(WATERLOGGED);
  }

  @Override
  public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      BlockEntity tileentity = worldIn.getBlockEntity(pos);
      if (tileentity instanceof TileCreeperTrap grinder) {
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
