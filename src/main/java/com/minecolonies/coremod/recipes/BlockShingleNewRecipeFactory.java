package com.minecolonies.coremod.recipes;

import com.google.gson.JsonObject;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.coremod.blocks.decorative.BlockShingleNew;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

public class BlockShingleNewRecipeFactory implements IRecipeFactory
{
    @Override
    public IRecipe parse(final JsonContext jsonContext, final JsonObject jsonObject)
    {
        ShapedOreRecipe recipe = ShapedOreRecipe.factory(jsonContext, jsonObject);

        ShapedPrimer primer = new ShapedPrimer();
        primer.width = recipe.getRecipeWidth();
        primer.height = recipe.getRecipeHeight();
        primer.mirrored = JsonUtils.getBoolean(jsonObject, "mirrored", true);
        primer.input = recipe.getIngredients();

        return new BlockShingleNewRecipe(new ResourceLocation(Constants.MOD_ID, "blockshinglenew"), recipe.getRecipeOutput(), primer);
    }

    public static class BlockShingleNewRecipe extends ShapedOreRecipe
    {
        public BlockShingleNewRecipe(final ResourceLocation group, final ItemStack result, final ShapedPrimer primer)
        {
            super(group, result, primer);
        }

        @Nonnull
        @Override
        public ItemStack getRecipeOutput()
        {
            final NBTTagCompound compound = new NBTTagCompound();

            compound.setInteger(NbtTagConstants.TAG_WOOD_TYPE, BlockPlanks.EnumType.OAK.getMetadata());
            compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, BlockShingleNew.EnumFace.CLAY.getMetadata());

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

                if (!stack.isEmpty())
                {
                    if (stack.getItem().equals(ItemBlock.getItemFromBlock(Blocks.PLANKS)))
                    {
                        final BlockPlanks.EnumType woodType = BlockPlanks.EnumType.byMetadata(stack.getMetadata());
                        compound.setInteger(NbtTagConstants.TAG_WOOD_TYPE, woodType.getMetadata());
                    }
                    else if (stack.getItem().equals(Items.BRICK))
                    {
                        compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, BlockShingleNew.EnumFace.CLAY.getMetadata());
                    }
                    else if (stack.getItem().equals(ItemBlock.getItemFromBlock(Blocks.STONE)))
                    {
                        compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, BlockShingleNew.EnumFace.SLATE.getMetadata());
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
