package net.silentchaos512.lib.client.model;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.util.StackHelper;

@SideOnly(Side.CLIENT)
public class MultiLayerModelSL implements IPerspectiveAwareModel {

  protected static ModelManager modelManager = null;

  protected final IBakedModel baseModel;
  protected ItemStack stack;

  public MultiLayerModelSL(IBakedModel parent) {

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

    if (StackHelper.isEmpty(stack)) {
      return new ArrayList<>();
    }

    if (modelManager == null) {
      modelManager = Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
          .getModelManager();
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

  @Override
  public boolean isAmbientOcclusion() {

    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isGui3d() {

    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isBuiltInRenderer() {

    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ItemOverrideList getOverrides() {

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {

    // TODO Auto-generated method stub
    return null;
  }
}
