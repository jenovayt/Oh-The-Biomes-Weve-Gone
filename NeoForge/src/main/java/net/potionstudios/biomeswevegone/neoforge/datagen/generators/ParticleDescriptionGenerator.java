package net.potionstudios.biomeswevegone.neoforge.datagen.generators;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;
import net.potionstudios.biomeswevegone.BiomesWeveGone;
import net.potionstudios.biomeswevegone.client.particle.BWGParticles;

/**
 * Generates the particles description file
 * @see ParticleDescriptionProvider
 * @author Joseph T. McQuigg
 */
public class ParticleDescriptionGenerator extends ParticleDescriptionProvider {
	/**
	 * Creates an instance of the data provider.
	 *
	 * @param output     the expected root directory the data generator outputs to
	 * @param fileHelper the helper used to validate a texture's existence
	 */
	public ParticleDescriptionGenerator(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, fileHelper);
	}

	@Override
	protected void addDescriptions() {
		sprite(BWGParticles.FIREFLY.get(), BiomesWeveGone.id("firefly"));
		sprite(BWGParticles.BOREALIS_GLINT.get(), BiomesWeveGone.id("borealis_glint"));
		sprite(BWGParticles.IRONWOOD_LEAVES.get(), BiomesWeveGone.id("ironwood_leaves"));
		spriteSet(BWGParticles.RED_MAPLE_LEAVES.get(), BiomesWeveGone.id("red_maple"), 4, false);
		spriteSet(BWGParticles.SILVER_MAPLE_LEAVES.get(), BiomesWeveGone.id("silver_maple"), 4, false);
		spriteSet(BWGParticles.YELLOW_SAKURA_LEAVES.get(), BiomesWeveGone.id("yellow_sakura"), 8, false);
		spriteSet(BWGParticles.WHITE_SAKURA_LEAVES.get(), BiomesWeveGone.id("white_sakura"), 8, false);
		spriteSet(BWGParticles.WITCH_HAZEL_LEAVES.get(), BiomesWeveGone.id("witch_hazel"), 6, false);
	}
}
