package com.minecolonies.coremod.recipes;

import com.google.gson.JsonObject;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.coremod.blocks.decorative.BlockShingleNew;
import com.minecolonies.coremod.blocks.decorative.BlockShingleNew.EnumFace;
import com.minecolonies.coremod.items.ModItems;
import net.minecraft.block.BlockPlanks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

public class BlockShingleDyeRecipeFactory implements IRecipeFactory
{

    @Override
    public IRecipe parse(final JsonContext jsonContext, final JsonObject jsonObject)
    {
        ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(jsonContext, jsonObject);

        final Integer faceMeta = JsonUtils.getInt(jsonObject, "face_meta", 0);

        return new BlockShingleDyeRecipe(new ResourceLocation(Constants.MOD_ID, "blockshinglenew_dye"), recipe.getRecipeOutput(), recipe, faceMeta);
    }

    public static class BlockShingleDyeRecipe extends ShapelessOreRecipe
    {

        final int faceMeta;

        public BlockShingleDyeRecipe(final ResourceLocation group, final ItemStack result, final ShapelessOreRecipe recipe, final int faceMeta)
        {
            super(group, recipe.getIngredients(), result);
            this.faceMeta = faceMeta;
        }

        @Nonnull
        @Override
        public ItemStack getRecipeOutput()
        {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(NbtTagConstants.TAG_WOOD_TYPE, BlockPlanks.EnumType.OAK.getMetadata());
            compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, EnumFace.CLAY.getMetadata());
            final ItemStack stack = this.output.copy();
            stack.setTagCompound(compound);
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack getCraftingResult(@Nonnull final InventoryCrafting var1)
        {
            final NBTTagCompound compound = new NBTTagCompound();

            for (int i = 0; i < var1.getSizeInventory(); ++i)
            {
                ItemStack stack = var1.getStackInSlot(i);
                if (!stack.isEmpty()
                      && stack.getTagCompound() != null
                      && (stack.getItem().equals(ModItems.itemBlockShinglesNewBottom)
                            || stack.getItem().equals(ModItems.itemBlockShinglesNewTop)))
                {
                    compound.setInteger(NbtTagConstants.TAG_WOOD_TYPE, stack.getTagCompound().getInteger(NbtTagConstants.TAG_WOOD_TYPE));
                    final EnumFace currentFace = EnumFace.byMetadata(stack.getTagCompound().getInteger(NbtTagConstants.TAG_FACE_TYPE));
                    if (currentFace.getType().equals(BlockShingleNew.TYPE_CLAY))
                    {
                        compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, this.faceMeta);
                    }
                    else if (currentFace.getType().equals(BlockShingleNew.TYPE_SLATE))
                    {
                        final EnumFace face = EnumFace.byType(this.faceMeta, BlockShingleNew.TYPE_SLATE);
                        if (face != null)
                        {
                            compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, face.getMetadata());
                        }
                        else
                        {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (!compound.hasKey(NbtTagConstants.TAG_FACE_TYPE) || !compound.hasKey(NbtTagConstants.TAG_WOOD_TYPE))
            {
                return ItemStack.EMPTY;
            }

            final ItemStack actualOutput = this.output.copy();
            actualOutput.setTagCompound(compound);

            return actualOutput;
        }
    }
}
