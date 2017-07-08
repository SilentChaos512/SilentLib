package net.silentchaos512.lib.client.model;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TRSRTransformation;

public class ModelHelperSL {

  public static OBJModel loadModel(ResourceLocation resource) {

    try {
      return (OBJModel) OBJLoader.INSTANCE.loadModel(resource);
    } catch (Exception e) {
      throw new ReportedException(new CrashReport("Error making the model for " + resource, e));
    }
  }

  public static IModel retexture(OBJModel model, String toReplace, String replaceWith) {

    return ((OBJModel) model.retexture(ImmutableMap.of(toReplace, replaceWith)))
        .process(ImmutableMap.of("flip-v", "true"));
  }

  public static IBakedModel bake(IModel model) {

    return model.bake(TRSRTransformation.identity(), Attributes.DEFAULT_BAKED_FORMAT,
        ModelLoader.defaultTextureGetter());
  }

  public static void renderModel(IBakedModel model, int color) {

    Tessellator tessellator = Tessellator.getInstance();
    VertexBuffer buffer = tessellator.getBuffer();
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

    List<BakedQuad> quads = model.getQuads(null, null, 0);
    quads.forEach(quad -> LightUtil.renderQuadColor(buffer, quad, color));

    tessellator.draw();
  }
}
