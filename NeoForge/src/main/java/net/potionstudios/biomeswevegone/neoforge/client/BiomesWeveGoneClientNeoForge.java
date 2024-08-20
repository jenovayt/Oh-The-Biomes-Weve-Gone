package net.potionstudios.biomeswevegone.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.potionstudios.biomeswevegone.BiomesWeveGone;
import net.potionstudios.biomeswevegone.client.BiomesWeveGoneClient;
import net.potionstudios.biomeswevegone.client.particle.BWGParticles;
import net.potionstudios.biomeswevegone.client.particle.particles.FallingLeafParticle;
import net.potionstudios.biomeswevegone.world.level.block.BWGBlocks;
import net.potionstudios.biomeswevegone.world.level.block.wood.BWGWood;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class is used to initialize the Forge client side of the mod.
 * @see FMLClientSetupEvent
 * @see BiomesWeveGoneClient
 * @author Joseph T. McQuigg
 */
@Mod(value = BiomesWeveGone.MOD_ID, dist = Dist.CLIENT)
public class BiomesWeveGoneClientNeoForge {

    /**
     * Constructor for the client side of the NeoForge mod.
     * @param eventBus The event bus to register the client side of the mod to.
     */
    public BiomesWeveGoneClientNeoForge(IEventBus eventBus) {
        init(eventBus);
    }

    /**
     * Initializes the client side of the Forge mod.
     * @param eventBus The event bus to register the client side of the mod to.
     */
    public static void init(IEventBus eventBus) {
        eventBus.addListener(BiomesWeveGoneClientNeoForge::neoForgeClientSetup);
        eventBus.addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) event -> BiomesWeveGoneClient.registerEntityRenderers(event::registerEntityRenderer));
        eventBus.addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) event -> BiomesWeveGoneClient.registerBlockEntityRenderers(event::registerBlockEntityRenderer));
        eventBus.addListener(BiomesWeveGoneClientNeoForge::registerParticles);
        eventBus.addListener((Consumer<EntityRenderersEvent.RegisterLayerDefinitions>) event -> BiomesWeveGoneClient.registerLayerDefinitions(event::registerLayerDefinition));
        eventBus.addListener(BiomesWeveGoneClientNeoForge::registerColorChangingBlocks);
        eventBus.addListener(BiomesWeveGoneClientNeoForge::registerItemColorHandlers);
    }

    /**
     * Sets up the client side of the mod.
     * @param event The event to set up the client side of the mod.
     * @see FMLClientSetupEvent
     */
    private static void neoForgeClientSetup(final FMLClientSetupEvent event) {
        BiomesWeveGoneClient.onInitialize(Minecraft.getInstance());
    }

    /**
     * Registers the custom particles for the mod.
     * @param event The event to register the particles to.
     * @see RegisterParticleProvidersEvent
     */
    private static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BWGParticles.FIREFLY.get(), FallingLeafParticle.Provider::new);
        event.registerSpriteSet(BWGParticles.BOREALIS_GLINT.get(), FallingLeafParticle.Provider::new);
        event.registerSpriteSet(BWGParticles.WITCH_HAZEL_LEAVES.get(), FallingLeafParticle.Provider::new);
        event.registerSpriteSet(BWGParticles.WHITE_SAKURA_LEAVES.get(), FallingLeafParticle.Provider::new);
        event.registerSpriteSet(BWGParticles.YELLOW_SAKURA_LEAVES.get(), FallingLeafParticle.Provider::new);
        event.registerSpriteSet(BWGParticles.RED_MAPLE_LEAVES.get(), FallingLeafParticle.Provider::new);
        event.registerSpriteSet(BWGParticles.SILVER_MAPLE_LEAVES.get(), FallingLeafParticle.Provider::new);
        event.registerSpriteSet(BWGParticles.IRONWOOD_LEAVES.get(), FallingLeafParticle.Provider::new);
    }

    /**
     * Registers the blocks that change color based on the biome.
     * @param event The event to register the blocks to.
     * @see RegisterColorHandlersEvent.Block
     */
    private static void registerColorChangingBlocks(final RegisterColorHandlersEvent.Block event) {
        event.register((state, view, pos, tintIndex) -> view != null && pos != null ? BiomeColors.getAverageGrassColor(view, pos) : GrassColor.getDefaultColor(), BWGBlocks.FLOWER_PATCH.get(), BWGBlocks.TINY_LILY_PADS.get(), BWGBlocks.FLOWERING_TINY_LILY_PADS.get(), BWGBlocks.OVERGROWN_DACITE.get(), BWGBlocks.OVERGROWN_STONE.get(), BWGBlocks.LUSH_GRASS_BLOCK.get(), BWGBlocks.WHITE_SAKURA_PETALS.get(), BWGBlocks.YELLOW_SAKURA_PETALS.get());
        event.register((state, view, pos, tintIndex) -> view != null && pos != null ? BiomeColors.getAverageFoliageColor(view, pos) : FoliageColor.get(0.5D, 1.0D), BWGBlocks.CLOVER_PATCH.get(), BWGBlocks.LEAF_PILE.get(), BWGBlocks.POISON_IVY.get(), BWGWood.MAHOGANY.leaves(),
                BWGWood.WILLOW.leaves(), BWGWood.MAPLE.leaves(), BWGWood.YUCCA_LEAVES.get(), BWGWood.FLOWERING_YUCCA_LEAVES.get(), BWGWood.RIPE_YUCCA_LEAVES.get(), BWGWood.CYPRESS.leaves());
        event.register((state, view, pos, tintIndex) -> BiomesWeveGoneClient.getBorealisIceColor(Objects.requireNonNullElse(pos, BlockPos.ZERO)), BWGBlocks.BOREALIS_ICE.get(), BWGBlocks.PACKED_BOREALIS_ICE.get());
        event.register((state, view, pos, tintIndex) -> view != null && pos != null ? BiomeColors.getAverageWaterColor(view, pos) : -1, BWGBlocks.CARVED_BARREL_CACTUS.get());
    }

    /**
     * Registers the item color handlers for the mod.
     * @param event The event to register the item color handlers to.
     * @see RegisterColorHandlersEvent.Item
     */
    private static void registerItemColorHandlers(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            return event.getBlockColors().getColor(block.defaultBlockState(), null, null, tintIndex);
        }, BWGBlocks.TINY_LILY_PADS.get(), BWGBlocks.FLOWERING_TINY_LILY_PADS.get(), BWGBlocks.CLOVER_PATCH.get(), BWGBlocks.LEAF_PILE.get(), BWGBlocks.POISON_IVY.get()
                , BWGWood.MAHOGANY.leaves(), BWGWood.WILLOW.leaves(), BWGWood.MAPLE.leaves(), BWGWood.YUCCA_LEAVES.get(), BWGWood.FLOWERING_YUCCA_LEAVES.get(), BWGWood.RIPE_YUCCA_LEAVES.get(), BWGWood.CYPRESS.leaves(), BWGBlocks.LUSH_GRASS_BLOCK.get()
                , BWGBlocks.OVERGROWN_DACITE.get(), BWGBlocks.OVERGROWN_STONE.get());
    }

}
