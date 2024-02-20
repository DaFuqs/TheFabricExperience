package de.dafuqs.thefabricexperience.api.fluid;

import de.dafuqs.thefabricexperience.*;
import de.dafuqs.thefabricexperience.impl.fluid.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.tag.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;

public class LiquidXP {
	
	/**
	 * Liquid XP in fluid, block and bucket form
	 */
	public static final FlowableFluid LIQUID_XP = new LiquidXPFluid.Still();
	public static final FlowableFluid FLOWING_LIQUID_XP = new LiquidXPFluid.Flowing();
	
	public static final Block LIQUID_XP_FLUID_BLOCK = new LiquidXPFluidBlock(LIQUID_XP, FabricBlockSettings.copyOf(Blocks.WATER).mapColor(MapColor.EMERALD_GREEN));
	public static final Item LIQUID_XP_BUCKET = new BucketItem(LIQUID_XP, new FabricItemSettings().group(ItemGroup.MISC));
	
	/**
	 * A fluid tagged as being experience in liquid form
	 * TODO: how should a generic xp fluid tag be called?
	 */
	public static final TagKey<Fluid> EXPERIENCE = tagKeyOf("experience");
	
	/**
	 * Conversion rates for XP <=> LIQUID_XP
	 * Values are based being on an experience bottle storing 7 XP, while having a fluid capacity of 27000 droplets of liquid xp (~3857)
	 * We bump that up to a round 4500, so we have a neat round number in regard to the FluidConstants.
	 * See net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants for conversion rates from Droplets to other ratios
	 * TODO: that is a LOT of liquid for some XP. Cut it in 1/10?
	 */
	public static final long DROPLETS_FOR_ONE_XP =  4500;
	
	private static TagKey<Fluid> tagKeyOf(String id) {
		return TagKey.of(Registry.FLUID_KEY, new Identifier(TheFabricExperience.MOD_ID + ":" + id));
	}
	
}
