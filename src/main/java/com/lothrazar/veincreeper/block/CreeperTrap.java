package com.lothrazar.veincreeper.block;

import java.util.function.Predicate;
import com.lothrazar.library.block.BlockFlib;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CreeperTrap extends BlockFlib implements SimpleWaterloggedBlock {

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

  public CreeperTrap(Properties prop) {
    super(prop);
    this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
  }

  boolean sneakPlayerAvoid = true;

  @Override
  @SuppressWarnings("deprecation")
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  public static final Predicate<Entity> DYE_FINDER = (p) -> {
    return p.isAlive() && p instanceof ItemEntity
        && ((ItemEntity) p).getItem().getItem() instanceof DyeItem;
  };

  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    if (sneakPlayerAvoid && entity instanceof Player && ((Player) entity).isCrouching()) {
      return;
    }
    //are you alive instanceof LivingEntity alive
    System.out.println("d   level.getSignal(pos, Direction.DOWN) y" + level.hasNeighborSignal(pos));
    if (!level.isClientSide &&
        level.hasNeighborSignal(pos) &&
        //        level.getSignal(pos, Direction.DOWN) > 0 && // TODO offset direction
        entity instanceof Creeper alive) {
      final BlockPos target = new BlockPos(pos);
      final int r = 1; // TODO radius controls in GUI
      AABB ab = new AABB(target.getX() + r, target.getY(), target.getZ() + r,
          target.getX() - r, target.getY() + 1, target.getZ() - r);
      //
      //
      ItemEntity dyeFound = null;
      for (Entity entityItemDye : level.getEntities((Entity) null, ab, DYE_FINDER)) {
        System.out.println("dye found for entity" + entityItemDye);
        final ItemEntity itemEntity = (ItemEntity) entityItemDye;
        DyeItem dye = (DyeItem) itemEntity.getItem().getItem();
        if (dye.getDyeColor() == DyeColor.RED) {
          //match recipe on color
          dyeFound = itemEntity;
          break;
        }
      }
      if (dyeFound != null) {
        System.out.println("hax found for entity" + dyeFound);
        var creeper = CreeperRegistry.CREEPERS.get("diamond_creeper");
        var c = new VeinCreeper(creeper.getEntityType(), level);
        c.setPos(pos.getCenter());
        level.addFreshEntity(c);
        //does the recipe match? yes? ok
        alive.remove(Entity.RemovalReason.KILLED); // .kill();
        dyeFound.getItem().shrink(1);
        level.destroyBlock(target, true);
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
}
