package de.dafuqs.thefabricexperience;

import de.dafuqs.thefabricexperience.api.fluid.*;
import de.dafuqs.thefabricexperience.impl.fluid.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;
import net.fabricmc.fabric.api.client.render.fluid.v1.*;
import net.fabricmc.fabric.api.event.client.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.fluid.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;

import java.util.function.*;

@Environment(EnvType.CLIENT)
public class TheFabricExperienceClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		setupFluidRendering(LiquidXP.LIQUID_XP, LiquidXP.FLOWING_LIQUID_XP, "liquid_xp", 0xFFFFFF);
		
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), LiquidXP.LIQUID_XP, LiquidXP.FLOWING_LIQUID_XP);
	}
	
	private static void setupFluidRendering(final Fluid still, final Fluid flowing, final String textureFluidId, final int color) {
		final Identifier stillSpriteId = new Identifier(TheFabricExperience.MOD_ID, "block/" + textureFluidId + "_still");
		final Identifier flowingSpriteId = new Identifier(TheFabricExperience.MOD_ID, "block/" + textureFluidId + "_flow");
		
		// If they're not already present, add the sprites to the block atlas
		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(stillSpriteId);
			registry.register(flowingSpriteId);
		});
		
		final Identifier fluidId = Registry.FLUID.getId(still);
		final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");
		final Sprite[] fluidSprites = {null, null};
		
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			
			/**
			 * Get the sprites from the block atlas when resources are reloaded
			 */
			@Override
			public void reload(ResourceManager manager) {
				final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
				fluidSprites[0] = atlas.apply(stillSpriteId);
				fluidSprites[1] = atlas.apply(flowingSpriteId);
			}
			
			@Override
			public Identifier getFabricId() {
				return listenerId;
			}
		});
		
		// The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
		final FluidRenderHandler renderHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return fluidSprites;
			}
			
			@Override
			public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
				return color;
			}
		};
		
		FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
	}
	
}
