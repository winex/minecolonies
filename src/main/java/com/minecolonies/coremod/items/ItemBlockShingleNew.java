package com.minecolonies.coremod.items;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.coremod.blocks.decorative.BlockShingleNew;
import net.minecraft.block.BlockPlanks;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.UniversalBucket;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

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

            return new TextComponentTranslation("tile." + Constants.MOD_ID + ".blockshingle_" + woodType.getName() + ".name").getUnformattedComponentText();
        }

        return super.getItemStackDisplayName(stack);
    }
}
