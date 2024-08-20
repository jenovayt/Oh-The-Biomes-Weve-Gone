package net.potionstudios.biomeswevegone.neoforge;

import com.google.auto.service.AutoService;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.potionstudios.biomeswevegone.BiomesWeveGone;
import net.potionstudios.biomeswevegone.PlatformHandler;
import net.potionstudios.biomeswevegone.world.level.block.BWGBlocks;
import net.potionstudios.biomeswevegone.world.level.block.custom.BWGFarmLandBlock;
import net.potionstudios.biomeswevegone.world.level.block.wood.BWGWood;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@AutoService(PlatformHandler.class)
public class NeoForgePlatformHandler implements PlatformHandler{

	@Override
	public Platform getPlatform() {
		return Platform.NEOFORGE;
	}

	@Override
	public Path configPath() {
		return FMLPaths.CONFIGDIR.get().resolve(BiomesWeveGone.MOD_ID);
	}

	@Override
	public <E extends Entity> Supplier<EntityType<E>> registerEntity(String id, EntityType.EntityFactory<E> factory, MobCategory category, float width, float height) {
		return register(BuiltInRegistries.ENTITY_TYPE, id, () -> EntityType.Builder.of(factory, category).sized(width, height).build(BiomesWeveGone.id(id).toString()));
	}

	@Override
	public <E extends Entity> Supplier<EntityType<E>> registerEntity(String id, EntityType.EntityFactory<E> factory, MobCategory category, float width, float height, int trackingRange) {
		return register(BuiltInRegistries.ENTITY_TYPE, id, () -> EntityType.Builder.of(factory, category).sized(width, height).clientTrackingRange(trackingRange).build(BiomesWeveGone.id(id).toString()));
	}

	@Override
	public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String key, Supplier<BlockEntityType.Builder<T>> builder) {
		return register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, () -> builder.get().build(Util.fetchChoiceType(References.BLOCK_ENTITY, key)));
	}

	@Override
	public Supplier<SpawnEggItem> createSpawnEgg(Supplier<EntityType<? extends Mob>> entity, int backgroundColor, int highlightColor) {
		return () -> new DeferredSpawnEggItem(entity, backgroundColor, highlightColor, new Item.Properties());
	}

	@Override
	public Supplier<FlowerPotBlock> createPottedBlock(Supplier<? extends Block> block) {
		return () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, block, BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT));
	}

	@Override
	public Supplier<BWGFarmLandBlock> bwgFarmLandBlock(Supplier<Block> dirt) {
		return () -> new net.potionstudios.biomeswevegone.neoforge.world.level.block.BWGFarmLandBlock(dirt);
	}

	@Override
	public WoodType createWoodType(String id, @NotNull BlockSetType setType) {
		return WoodType.register(new WoodType(BiomesWeveGone.MOD_ID + ":" + id, setType));
	}

	@Override
	public Supplier<SimpleParticleType> registerCreateParticle(String name) {
		return register(BuiltInRegistries.PARTICLE_TYPE, name, () -> new SimpleParticleType(false));
	}

	@SafeVarargs
	@Override
	public final Supplier<CreativeModeTab> createCreativeTab(String name, Supplier<ItemStack> icon, ArrayList<Supplier<? extends Item>>... items) {
		return register(BuiltInRegistries.CREATIVE_MODE_TAB, name, () -> CreativeModeTab.builder()
				.title(Component.translatable("itemGroup." + BiomesWeveGone.MOD_ID + "." + name))
				.icon(icon)
				.displayItems((context, entries) -> {
					for (ArrayList<Supplier<? extends Item>> item : items)
						item.forEach((item1) -> entries.accept(item1.get()));
				})
				.withSearchBar()
				.build());
	}

	private static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = DeferredRegister.create(Registries.BLOCK_PREDICATE_TYPE, BiomesWeveGone.MOD_ID);

	@Override
	public <P extends BlockPredicate> Supplier<BlockPredicateType<P>> registerBlockPredicate(String id, Supplier<MapCodec<P>> codec) {
		return BLOCK_PREDICATE_TYPE.register(id, () -> codec::get);
	}

	public static final Map<ResourceKey<?>, DeferredRegister> CACHED = new Reference2ObjectOpenHashMap<>();

	@Override
	public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value) {
		return CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), BiomesWeveGone.MOD_ID)).register(name, value);
	}

	@Override
	public <T> Supplier<Holder.Reference<T>> registerForHolder(Registry<T> registry, String name, Supplier<T> value) {
		DeferredHolder<?, ?> registryObject = CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), BiomesWeveGone.MOD_ID)).register(name, value);
		return () -> (Holder.Reference<T>) registryObject.getDelegate();
	}

	public static void registerPottedPlants() {
		BWGBlocks.BLOCKS.forEach(entry -> {
			if (entry.get() instanceof FlowerPotBlock)
				((FlowerPotBlock) Blocks.FLOWER_POT)
						.addPlant(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(((FlowerPotBlock) entry.get()).getPotted())), entry);
		});
		BWGWood.WOOD.forEach(entry -> {
			if (entry.get() instanceof FlowerPotBlock)
				((FlowerPotBlock) Blocks.FLOWER_POT)
						.addPlant(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(((FlowerPotBlock) entry.get()).getPotted())), entry);
		});
	}

	public static void register(IEventBus bus) {
		CACHED.values().forEach(deferredRegister -> deferredRegister.register(bus));
		BLOCK_PREDICATE_TYPE.register(bus);
	}
}
