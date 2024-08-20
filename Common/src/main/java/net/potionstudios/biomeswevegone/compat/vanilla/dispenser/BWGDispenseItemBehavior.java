package net.potionstudios.biomeswevegone.compat.vanilla.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.potionstudios.biomeswevegone.world.entity.boats.BWGBoatEntity;
import net.potionstudios.biomeswevegone.world.item.BWGItems;
import org.jetbrains.annotations.NotNull;

/**
 * Dispense Item Behavior for BWG Blocks and Items.
 * @see net.minecraft.core.dispenser.BoatDispenseItemBehavior
 * @author Joseph T. McQuigg
 */
public class BWGDispenseItemBehavior {
	public static void registerDispenseItemBehavior() {
		for (BWGBoatEntity.Type type : BWGBoatEntity.Type.values()) {
			DispenserBlock.registerBehavior(type.getBoatItem().get(), new BWGBoatDispenseItemBehavior(type));
			DispenserBlock.registerBehavior(type.getChestBoatItem().get(), new BWGBoatDispenseItemBehavior(type, true));
		}

		DispenserBlock.registerBehavior(BWGItems.MAN_O_WAR_BUCKET.get(), new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			@Override
			protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack item) {
				DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) item.getItem();
				BlockPos blockPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
				Level level = blockSource.level();
				if (dispensibleContainerItem.emptyContents(null, level, blockPos, null)) {
					dispensibleContainerItem.checkExtraContent(null, level, item, blockPos);
					return new ItemStack(Items.BUCKET);
				} else return this.defaultDispenseItemBehavior.dispense(blockSource, item);
			}
		});
	}
}
