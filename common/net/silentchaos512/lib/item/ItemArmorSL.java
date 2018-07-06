/*
 * SilentLib - ItemArmorSL
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.registry.RecipeMaker;

import java.util.List;
import java.util.Map;

@Deprecated
public class ItemArmorSL extends ItemArmor implements IRegistryObject, IItemSL {

    protected String itemName;
    protected String modId;

    public ItemArmorSL(String modId, String itemName, ArmorMaterial material, EntityEquipmentSlot slot) {
        this(modId, itemName, material, 0, slot);
    }

    @Deprecated
    public ItemArmorSL(String modId, String itemName, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {

        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.modId = modId;
        this.itemName = itemName;
        setUnlocalizedName(itemName);
    }

    // =======================
    // IRegistryObject methods
    // =======================

    @Override
    public void addRecipes(RecipeMaker recipes) {

    }

    @Override
    public void addOreDict() {

    }

    @Override
    public String getName() {

        return itemName;
    }

    @Override
    public String getFullName() {

        return modId + ":" + getName();
    }

    @Override
    public String getModId() {

        return modId;
    }

    @Override
    public void getModels(Map<Integer, ModelResourceLocation> models) {

        models.put(0, new ModelResourceLocation(getFullName(), "inventory"));
    }

    @Override
    public boolean registerModels() {

        // Let SRegistry handle model registration by default. Override if necessary.
        return false;
    }

    // ==============================
    // Cross Compatibility (MC 10/11)
    // inspired by CompatLayer
    // ==============================

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn,
                                                    EnumHand hand) {

        return clOnItemRightClick(worldIn, playerIn, hand);
    }

    @Deprecated
    protected ActionResult<ItemStack> clOnItemRightClick(World worldIn, EntityPlayer playerIn,
                                                         EnumHand hand) {

        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {

        return clOnItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Deprecated
    protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos,
                                           EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {

        clGetSubItems(this, tab, subItems);
    }

    @SideOnly(Side.CLIENT)
    @Deprecated
    protected void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {

        super.getSubItems(tab, (NonNullList<ItemStack>) subItems);
    }

    // ===========================
    // Cross Compatibility (MC 12)
    // ===========================

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {

        clAddInformation(stack, world, list, flag == TooltipFlags.ADVANCED);
    }

    @Deprecated
    public void clAddInformation(ItemStack stack, World world, List<String> list, boolean advanced) {

    }
}
