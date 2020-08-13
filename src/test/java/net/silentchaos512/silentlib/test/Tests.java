package net.silentchaos512.silentlib.test;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.lib.util.DimPos;
import org.junit.Assert;
import org.junit.Test;

public class Tests {
    @Test
    public void testDimPos() {
        Assert.assertEquals(DimPos.of(BlockPos.ZERO, World.field_234918_g_), DimPos.ZERO);

        DimPos pos = DimPos.of(new BlockPos(1, 2, 3), World.field_234918_g_);
        CompoundNBT tag = new CompoundNBT();
        pos.write(tag);
        Assert.assertEquals(1, tag.getInt("posX"));
        Assert.assertEquals(2, tag.getInt("posY"));
        Assert.assertEquals(3, tag.getInt("posZ"));
        Assert.assertEquals(World.field_234918_g_.func_240901_a_().toString(), tag.getInt("dim"));

        DimPos pos1 = DimPos.read(tag);
        Assert.assertEquals(pos, pos1);
    }
}
