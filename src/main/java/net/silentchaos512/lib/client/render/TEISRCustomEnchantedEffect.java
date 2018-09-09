/*
 * Silent Lib -- TEISREnchantedGlowFix
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.client.render;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.client.model.ILayeredBakedModel;
import net.silentchaos512.lib.item.ICustomEnchantColor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Completely replaces the rendering of the enchanted glow effect. This mostly exists to fix the
 * "stacking effect" bug that has existed since 1.8, but can also be used to just change the effect
 * color. Implement {@link ICustomEnchantColor} on the item to use. The item model must be a {@link
 * ILayeredBakedModel} for this to work.
 * <p>This was originally part of Silent Gear, but was adapted for slightly more general use.</p>
 *
 * @author SilentChaos512
 * @since 3.0.4
 */
@ParametersAreNonnullByDefault
public class TEISRCustomEnchantedEffect extends TileEntityItemStackRenderer {
    public static final TEISRCustomEnchantedEffect INSTANCE = new TEISRCustomEnchantedEffect();

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    // Empty set stored for convenience (could pass nulls, but I don't like that)
    private static final ImmutableSet<EnumFacing> EMPTY = ImmutableSet.of();
    // The "front" and "back" of the item model. For the enchanted effect to display correctly, we
    // must render it on all faces for the first layer, then all faces EXCEPT THESE on all other
    // layers. I have no idea how I figured this out.
    private static final ImmutableSet<EnumFacing> STUPID_FACES = ImmutableSet.of(EnumFacing.SOUTH, EnumFacing.NORTH);

    private TEISRCustomEnchantedEffect() {
    }

    @Override
    public void renderByItem(ItemStack stack) {
        this.renderByItem(stack, 1f);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
        if (!(model instanceof ILayeredBakedModel)) {
            throw new IllegalArgumentException("Model must be a ILayeredBakedModel");
        }

        int effectColor = getEffectColor(stack);
        int layerCount = ((ILayeredBakedModel) model).getLayerCount();

        for (int i = 0; i < layerCount; ++i) {
            // if (i == 0) // Uncomment to test individual layers
            this.renderModelExcluding(model, -1, stack, i, EMPTY);
        }

        // Mostly fixes the enchanted glint effect. Don't even ask me why this works. It's not a
        // perfect fix, but works well enough.
        if (stack.hasEffect()) {
            // Need to render the effect on all faces of the first layer
            this.renderEffectExcluding(model, effectColor, 0, EMPTY);
            for (int i = 1; i < layerCount; ++i) {
                // Excluding the "front" and "back" magically fixes the duplication effect. I hate you.
                this.renderEffectExcluding(model, effectColor, i, STUPID_FACES);
            }
        }
    }

    private int getEffectColor(ItemStack stack) {
        int effectColor = 0x8040cc; // Vanilla effect color
        if (stack.getItem() instanceof ICustomEnchantColor) {
            ICustomEnchantColor item = (ICustomEnchantColor) stack.getItem();
            effectColor = item.getEffectColor(stack);
            if (item.shouldTruncateBrightness(stack)) {
                int r = effectColor >> 16 & 0xFF;
                int g = effectColor >> 8 & 0xFF;
                int b = effectColor & 0xFF;
                int maxBrightness = item.getEffectMaxBrightness(stack);

                int total = r + g + b;
                if (total > maxBrightness) {
                    float scale = (float) maxBrightness / total;
                    r = (int) (r * scale);
                    g = (int) (g * scale);
                    b = (int) (b * scale);

                    effectColor = (r << 16) + (g << 8) + b;
                }
            }
        }
        return effectColor;
    }

    // Copied from RenderItem
    private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
        boolean flag = color == -1 && !stack.isEmpty();
        int i = 0;

        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = quads.get(i);
            int k = color;

            if (flag && bakedquad.hasTintIndex()) {
                k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());

//                if (EntityRenderer.anaglyphEnable) {
//                    k = TextureUtil.anaglyphColor(k);
//                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }

    // Copied from RenderItem
    private void renderEffectExcluding(IBakedModel model, int effectColor, int layerIndex, Collection<EnumFacing> excludeFaces) {
        int color = effectColor | 0xFF000000;

        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        textureManager.bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);

        this.renderModelExcluding(model, color, layerIndex, excludeFaces);

        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);

        this.renderModelExcluding(model, color, layerIndex, excludeFaces);

        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    // Copied from RenderItem
    private void renderModelExcluding(IBakedModel model, int color, int layerIndex, Collection<EnumFacing> excludeFaces) {
        this.renderModelExcluding(model, color, ItemStack.EMPTY, layerIndex, excludeFaces);
    }

    // Copied from RenderItem
    private void renderModelExcluding(IBakedModel model, int color, ItemStack stack, int layerIndex, Collection<EnumFacing> excludeFaces) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        List<BakedQuad> quads = new ArrayList<>();
        if (layerIndex < 0) {
            // FIXME
            for (int i = 0; i < ((ILayeredBakedModel) model).getLayerCount(); ++i) {
                for (BakedQuad quad : model.getQuads(null, null, i)) {
                    if (!excludeFaces.contains(quad.getFace())) {
                        quads.add(quad);
                    }
                }
            }
        } else {
            for (BakedQuad quad : model.getQuads(null, null, layerIndex)) {
                if (!excludeFaces.contains(quad.getFace())) {
                    quads.add(quad);
                }
            }
        }

        this.renderQuads(bufferbuilder, quads, color, stack);
        tessellator.draw();
    }
}
