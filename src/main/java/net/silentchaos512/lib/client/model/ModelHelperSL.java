package net.silentchaos512.lib.client.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;

public class ModelHelperSL {
    private static final Random RANDOM = new Random();

    public static OBJModel loadModel(ResourceLocation resource) {
        try {
            return (OBJModel) OBJLoader.INSTANCE.loadModel(resource);
        } catch (Exception e) {
            throw new ReportedException(new CrashReport("Error making the model for " + resource, e));
        }
    }

    public static IModel retexture(OBJModel model, String toReplace, String replaceWith) {
        return model.retexture(ImmutableMap.of(toReplace, replaceWith)).process(ImmutableMap.of("flip-v", "true"));
    }

    // FIXME
//    public static IBakedModel bake(IModel model) {
//        return model.bake(TRSRTransformation.identity(), Attributes.DEFAULT_BAKED_FORMAT, ModelLoader.defaultTextureGetter());
//    }

    public static void renderModel(IBakedModel model, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

        List<BakedQuad> quads = model.getQuads(null, null, RANDOM);
        quads.forEach(quad -> LightUtil.renderQuadColor(buffer, quad, color));

        tessellator.draw();
    }
}
