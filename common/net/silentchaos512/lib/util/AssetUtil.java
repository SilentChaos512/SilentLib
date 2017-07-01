/*
 * Class partially copied from Actually Additions by Ellpeck.
 */

package net.silentchaos512.lib.util;

import org.lwjgl.opengl.GL11;

import io.netty.util.internal.StringUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AssetUtil {

  @SideOnly(Side.CLIENT)
  public static void renderStackToGui(ItemStack stack, int x, int y, float scale) {

    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.enableDepth();
    GlStateManager.enableRescaleNormal();
    GlStateManager.translate(x, y, 0);
    GlStateManager.scale(scale, scale, scale);

    Minecraft mc = Minecraft.getMinecraft();
    boolean flagBefore = mc.fontRenderer.getUnicodeFlag();
    mc.fontRenderer.setUnicodeFlag(false);
    Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
    Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, 0,
        0, null);
    mc.fontRenderer.setUnicodeFlag(flagBefore);

    RenderHelper.disableStandardItemLighting();
    GlStateManager.popMatrix();
  }

  // Copied from Gui.class and changed
  @SideOnly(Side.CLIENT)
  public static void drawHorizontalGradientRect(int left, int top, int right, int bottom,
      int startColor, int endColor, float zLevel) {

    float f = (float) (startColor >> 24 & 255) / 255.0F;
    float f1 = (float) (startColor >> 16 & 255) / 255.0F;
    float f2 = (float) (startColor >> 8 & 255) / 255.0F;
    float f3 = (float) (startColor & 255) / 255.0F;
    float f4 = (float) (endColor >> 24 & 255) / 255.0F;
    float f5 = (float) (endColor >> 16 & 255) / 255.0F;
    float f6 = (float) (endColor >> 8 & 255) / 255.0F;
    float f7 = (float) (endColor & 255) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    VertexBuffer renderer = tessellator.getBuffer();
    renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    renderer.pos((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
    renderer.pos((double) left, (double) bottom, (double) zLevel).color(f1, f2, f3, f).endVertex();
    renderer.pos((double) right, (double) bottom, (double) zLevel).color(f5, f6, f7, f4)
        .endVertex();
    renderer.pos((double) right, (double) top, (double) zLevel).color(f5, f6, f7, f4).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }
}
