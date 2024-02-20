package de.dafuqs.thefabricexperience.impl.fluid;

import de.dafuqs.thefabricexperience.api.fluid.*;
import net.fabricmc.api.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.particle.*;
import net.minecraft.sound.*;
import net.minecraft.state.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

import java.util.*;

public abstract class LiquidXPFluid extends FlowableFluid {
	
	@Override
	protected boolean isInfinite() {
		return false;
	}
	
	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
		final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world, pos, blockEntity);
	}
	
	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return false;
	}
	
	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}
	
	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
	}
	
	@Override
	public Fluid getStill() {
		return LiquidXP.LIQUID_XP;
	}
	
	@Override
	public Fluid getFlowing() {
		return LiquidXP.FLOWING_LIQUID_XP;
	}
	
	@Override
	public Item getBucketItem() {
		return LiquidXP.LIQUID_XP_BUCKET;
	}
	
	@Override
	protected BlockState toBlockState(FluidState fluidState) {
		return LiquidXP.LIQUID_XP_FLUID_BLOCK.getDefaultState().with(Properties.LEVEL_15, FlowableFluid.getBlockStateLevel(fluidState));
	}
	
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == LiquidXP.LIQUID_XP || fluid == LiquidXP.FLOWING_LIQUID_XP;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
	
	}
	
	@Override
	protected int getFlowSpeed(WorldView worldView) {
		return 2;
	}
	
	@Override
	protected int getLevelDecreasePerBlock(WorldView worldView) {
		return 2;
	}
	
	@Override
	public int getTickRate(WorldView worldView) {
		return 5;
	}
	
	@Override
	public ParticleEffect getParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}
	
	public static class Flowing extends LiquidXPFluid {
		
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}
		
		@Override
		public int getLevel(FluidState fluidState) {
			return fluidState.get(LEVEL);
		}
		
		@Override
		public boolean isStill(FluidState fluidState) {
			return false;
		}
		
	}
	
	public static class Still extends LiquidXPFluid {
		
		@Override
		public int getLevel(FluidState fluidState) {
			return 8;
		}
		
		@Override
		public boolean isStill(FluidState fluidState) {
			return true;
		}
		
	}
}