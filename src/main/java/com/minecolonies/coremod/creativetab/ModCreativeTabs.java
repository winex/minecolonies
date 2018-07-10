package com.minecolonies.coremod.creativetab;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.coremod.blocks.ModBlocks;
import com.minecolonies.coremod.blocks.decorative.BlockShingleNew;
import com.minecolonies.coremod.items.ModItems;
import com.sun.javafx.css.FontFace;
import gigaherz.common.state.client.ItemStateMapper;
import net.minecraft.block.BlockPlanks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Class used to handle the creativeTab of minecolonies.
 */
public final class ModCreativeTabs
{
    public static final CreativeTabs MINECOLONIES = new CreativeTabs(Constants.MOD_ID)
    {

        @Override
        public ItemStack getTabIconItem()
        {
            this.setBackgroundImageName("minecolonies_background.png");
            return new ItemStack(ModBlocks.blockHutTownHall);
        }

        @Override
        public boolean hasSearchBar()
        {
            return true;
        }
    };

    public static final CreativeTabs SHINGLES = new CreativeTabs(Constants.MOD_ID + ".shingles")
    {
        @Override
        public ItemStack getTabIconItem()
        {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(NbtTagConstants.TAG_WOOD_TYPE, BlockPlanks.EnumType.OAK.getMetadata());
            compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, BlockShingleNew.EnumFace.CLAY.getMetadata());

            final ItemStack stack = new ItemStack(ModItems.itemBlockShinglesNewTop);

            stack.setTagCompound(compound);

            return stack;
        }

        @Override
        public boolean hasSearchBar()
        {
            return true;
        }
    };

    /**
     * Private constructor to hide the implicit one.
     */
    private ModCreativeTabs()
    {
        /*
         * Intentionally left empty.
         */
    }
}
