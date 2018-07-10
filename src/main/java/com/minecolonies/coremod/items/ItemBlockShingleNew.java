package com.minecolonies.coremod.items;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.coremod.blocks.decorative.BlockShingleNew;
import net.minecraft.block.BlockPlanks;
import net.minecraft.item.*;
import net.minecraft.util.text.TextComponentTranslation;
import org.jetbrains.annotations.NotNull;

public class ItemBlockShingleNew extends ItemBlock
{
    public ItemBlockShingleNew(final BlockShingleNew block)
    {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }

    @NotNull
    @Override
    public String getItemStackDisplayName(@NotNull final ItemStack stack)
    {
        if (stack.getTagCompound() == null)
            return "";

        if (stack.getTagCompound().hasKey(NbtTagConstants.TAG_WOOD_TYPE))
        {
            final BlockPlanks.EnumType woodType = BlockPlanks.EnumType.byMetadata(stack.getTagCompound().getInteger(NbtTagConstants.TAG_WOOD_TYPE));
            final BlockShingleNew.EnumFace faceType = BlockShingleNew.EnumFace.byMetadata(stack.getTagCompound().getInteger(NbtTagConstants.TAG_FACE_TYPE));

            final String faceName = new TextComponentTranslation(Constants.MOD_ID + ".blockshingle_face_" + faceType.getName() + ".name").getUnformattedComponentText();
            final String woodName = new TextComponentTranslation(Constants.MOD_ID + ".blockshingle_wood_" + woodType.getName() + ".name").getUnformattedComponentText();
            final String shingleName = new TextComponentTranslation(Constants.MOD_ID + ".blockshingle.name").getUnformattedComponentText();
            return faceName + " " + woodName + " " + shingleName;
        }

        return super.getItemStackDisplayName(stack);
    }
}
