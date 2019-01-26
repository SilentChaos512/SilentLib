package net.silentchaos512.lib.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class LayeredBakedModel implements ILayeredBakedModel {
    protected final IModel parent;
    protected final ImmutableList<ImmutableList<BakedQuad>> quads;
    protected final VertexFormat format;
    private ImmutableList<BakedQuad> allQuadsCache;

    public LayeredBakedModel(IModel parent, ImmutableList<ImmutableList<BakedQuad>> immutableList, VertexFormat format) {
        this.quads = immutableList;
        this.format = format;
        this.parent = parent;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand) {
        return ImmutableList.of();
    }

    // FIXME index paramater is gone...
//    @Override
//    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long indexNotRand) {
//        if (side == null) {
//            int index = (int) indexNotRand;
//            // Check invalid index
//            if (index < 0) {
//                // Return everything in one? Better than nothing.
//                if (allQuadsCache == null) {
//                    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
//                    for (List<BakedQuad> list : quads) {
//                        for (BakedQuad quad : list) {
//                            builder.add(quad);
//                        }
//                    }
//
//                    allQuadsCache = builder.build();
//                }
//
//                return allQuadsCache;
//            } else if (index < quads.size()) {
//                return this.quads.get(index);
//            }
//        }
//        return ImmutableList.of();
//    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public int getLayerCount() {
        return this.quads.size();
    }

    public IModel getParent() {
        return this.parent;
    }

    public VertexFormat getVertexFormat() {
        return this.format;
    }
}
