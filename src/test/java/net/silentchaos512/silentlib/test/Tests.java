package net.silentchaos512.silentlib.test;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.silentchaos512.lib.util.DimPos;
import net.silentchaos512.lib.util.generator.TagGenerator;
import org.junit.Assert;
import org.junit.Test;

public class Tests {
    @Test
    public void testDimPos() {
        Assert.assertEquals(DimPos.of(0, 0, 0, 0), DimPos.ZERO);

        DimPos pos = DimPos.of(1, 2, 3, 0);
        CompoundNBT tag = new CompoundNBT();
        pos.write(tag);
        Assert.assertEquals(1, tag.getInt("posX"));
        Assert.assertEquals(2, tag.getInt("posY"));
        Assert.assertEquals(3, tag.getInt("posZ"));
        Assert.assertEquals(0, tag.getInt("dim"));

        DimPos pos1 = DimPos.read(tag);
        Assert.assertEquals(pos, pos1);
    }

    //@Test
    @SuppressWarnings("deprecation")
    public void testTagGen() {
        TagGenerator.item(new ResourceLocation("silentlib", "test_tag"), "silentlib:test_item");
        TagGenerator.item(new ResourceLocation("silentlib", "test_tag"), "#forge:ores/iron");
        TagGenerator.block(new ResourceLocation("forge", "ores/test"), "silentlib:test_block");
        TagGenerator.block(new ResourceLocation("forge", "ores/test"), "#silentlib:test_tag");
        TagGenerator.block(new ResourceLocation("forge", "ores/test"), Tags.Blocks.DIRT);
        TagGenerator.generateFiles();
    }
}
