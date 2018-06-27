package com.minecolonies.coremod.items;

import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.coremod.blocks.decorative.BlockShingleNew;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockPlanks.EnumType;

public class ItemBlockShingle extends ItemBlock
{

    private EnumType woodType;

    public ItemBlockShingle(BlockShingleNew block)
    {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }

    @Override
    public String getItemStackDisplayName(final ItemStack stack)
    {

        NBTTagCompound compound = stack.getTagCompound();

        if (compound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
            compound = stack.getTagCompound();
        }

        if (compound != null
          && compound.hasKey(NbtTagConstants.TAG_WOOD_TYPE))
        {
            return new TextComponentTranslation(this.getUnlocalizedName() + ".name")
                     + " "
                     + EnumType.byMetadata(compound.getInteger(NbtTagConstants.TAG_WOOD_TYPE)).getName();
        }

        return super.getItemStackDisplayName(stack);
    }
}
