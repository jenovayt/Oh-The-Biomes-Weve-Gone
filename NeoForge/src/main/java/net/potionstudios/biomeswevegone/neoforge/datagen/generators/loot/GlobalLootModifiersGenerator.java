package net.potionstudios.biomeswevegone.neoforge.datagen.generators.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.potionstudios.biomeswevegone.BiomesWeveGone;
import net.potionstudios.biomeswevegone.neoforge.loot.AddItemModifier;
import net.potionstudios.biomeswevegone.world.level.block.BWGBlocks;

public class GlobalLootModifiersGenerator extends GlobalLootModifierProvider {
    public GlobalLootModifiersGenerator(PackOutput output) {
        super(output, BiomesWeveGone.MOD_ID);
    }

    @Override
    protected void start() {
        add("bwg_flowers_from_sniffer_dig", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(BuiltInLootTables.SNIFFER_DIGGING).build()}, BWGBlocks.BLACK_ROSE.getBlock().asItem(),
                BWGBlocks.PROTEA_FLOWER.getBlock().asItem(), BWGBlocks.SILVER_VASE_FLOWER.getBlock().asItem()));
    }
}
