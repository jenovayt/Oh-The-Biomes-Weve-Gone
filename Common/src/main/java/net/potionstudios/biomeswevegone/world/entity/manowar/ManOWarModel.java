package net.potionstudios.biomeswevegone.world.entity.manowar;

import net.minecraft.resources.ResourceLocation;
import net.potionstudios.biomeswevegone.BiomesWeveGone;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * ManOWar Model
 * @see GeoModel
 * @author YaBoiChips
 */
public class ManOWarModel<T extends GeoAnimatable> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T object) {
        return BiomesWeveGone.id("geo/man_o_war.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T object) {
        return ManOWarRenderer.TEXTURES.get(((ManOWar) object).getColor());
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return BiomesWeveGone.id("animations/man_o_war.animation.json");
    }
}