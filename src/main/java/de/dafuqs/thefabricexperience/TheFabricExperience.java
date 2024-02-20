package de.dafuqs.thefabricexperience;

import de.dafuqs.thefabricexperience.api.fluid.*;
import de.dafuqs.thefabricexperience.api.storage.*;
import de.dafuqs.thefabricexperience.api.storage.base.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.minecraft.block.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;

public class TheFabricExperience implements ModInitializer {

	public static final String MOD_ID = "thefabricexperience";
	
	@Override
	public void onInitialize() {
		registerFluid("liquid_xp", LiquidXP.LIQUID_XP);
		registerFluid("flowing_liquid_xp", LiquidXP.FLOWING_LIQUID_XP);
		
		registerBlock("liquid_xp", LiquidXP.LIQUID_XP_FLUID_BLOCK);
		registerItem("liquid_xp_bucket", LiquidXP.LIQUID_XP_BUCKET);
		
		ExperienceStorage.ITEM.registerForItems((stack, ctx) -> new SimpleExperienceStorage(FluidConstants.BOTTLE, 0, Integer.MAX_VALUE), Items.EXPERIENCE_BOTTLE);
		ExperienceStorage.ITEM.registerForItems((stack, ctx) -> new SimpleExperienceStorage(FluidConstants.BUCKET, 0, Integer.MAX_VALUE), LiquidXP.LIQUID_XP_BUCKET);
		
		// TODO: register Items.EXPERIENCE_BOTTLE as a provider for xp fluid
		// TODO: register Empty Bottle as a target for xp fluid
		// TODO: register liquid_xp_bucket as a provider for xp fluid (if fabric does not take care of that already)
	}
	
	private static void registerFluid(String name, Fluid fluid) {
		Registry.register(Registry.FLUID, new Identifier(TheFabricExperience.MOD_ID, name), fluid);
	}
	
	private static void registerBlock(String name, Block block) {
		Registry.register(Registry.BLOCK, new Identifier(TheFabricExperience.MOD_ID, name), block);
	}
	
	private static void registerItem(String name, Item item) {
		Registry.register(Registry.ITEM, new Identifier(TheFabricExperience.MOD_ID, name), item);
	}
	
}
