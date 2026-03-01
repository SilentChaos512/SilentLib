package net.silentchaos512.lib.data.client;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.silentchaos512.lib.SilentLib;

import java.util.Optional;

public class LibModelTemplates {
    public static final ModelTemplate CROP_CUTOUT = new ModelTemplate(
            Optional.of(SilentLib.getId("block/crop_cutout")),
            Optional.empty(),
            TextureSlot.CROP
    );
    public static final ModelTemplate CROSS_CUTOUT = new ModelTemplate(
            Optional.of(SilentLib.getId("block/cross_cutout")),
            Optional.empty(),
            TextureSlot.CROSS
    );
    public static final ModelTemplate FLOWER_POT_CROSS_CUTOUT = new ModelTemplate(
            Optional.of(SilentLib.getId("block/flower_pot_cross_cutout")),
            Optional.empty(),
            TextureSlot.PLANT
    );
}
