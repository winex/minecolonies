package com.minecolonies.coremod.items;

import com.minecolonies.api.util.LanguageHandler;
import com.minecolonies.coremod.MineColonies;
import com.minecolonies.coremod.client.gui.WindowBuildTool;
import com.minecolonies.coremod.client.gui.WindowMinecoloniesBook;
import com.minecolonies.coremod.creativetab.ModCreativeTabs;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Caliper Item class. Calculates distances, areas, and volumes.
 */
public class ItemBook extends AbstractItemMinecolonies
{
    /**
     * Caliper constructor. Sets max stack to 1, like other tools.
     */
    public ItemBook()
    {
        super("MinecoloniesBook");

        super.setCreativeTab(ModCreativeTabs.MINECOLONIES);
        maxStackSize = 1;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final EntityPlayer playerIn, final EnumHand hand)
    {
        final ItemStack stack = playerIn.getHeldItem(hand);

        if (worldIn.isRemote)
        {
            @Nullable final WindowMinecoloniesBook window = new WindowMinecoloniesBook();
            window.open();
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
