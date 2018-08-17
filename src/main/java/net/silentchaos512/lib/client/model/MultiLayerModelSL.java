package net.silentchaos512.lib.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class MultiLayerModelSL extends PerspectiveMapWrapper {

  protected static ModelManager modelManager = null;

  protected final IBakedModel baseModel;
  protected ItemStack stack;

  public MultiLayerModelSL(IBakedModel parent) {

    super(parent, getTransforms(new TRSRTransformation(new Matrix4f())));
    this.baseModel = parent;
  }

  public ModelResourceLocation getModelForLayer(int layer) {

    return null;
  }

  public int getLayerCount() {

    return 0;
  }

  public IBakedModel handleItemState(ItemStack stack) {

    this.stack = stack;
    return this;
  }

  @Override
  public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {

    if (stack.isEmpty()) {
      return new ArrayList<>();
    }

    if (modelManager == null) {
      modelManager = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager();
    }

    List<BakedQuad> quads = new ArrayList<>();
    ModelResourceLocation location;
    IBakedModel model;

    for (int layer = 0; layer < getLayerCount(); ++layer) {
      location = getModelForLayer(layer);
      if (location != null) {
        model = modelManager.getModel(location);
        if (model != null) {
          quads.addAll(model.getQuads(state, side, rand));
        }
      }
    }

    return quads;
  }
}
