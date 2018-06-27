package com.minecolonies.coremod.items;

import com.minecolonies.api.capabilities.CapabilityDispatcher;
import com.minecolonies.api.capabilities.IShingleCapability;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ItemBlockShingle extends ItemBlock
{

    public ItemBlockShingle(Block block)
    {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }

    @Override
    public String getItemStackDisplayName(final ItemStack stack)
    {
        if (!stack.hasCapability(ModCapabilities.MOD_SHINGLE_CAPABILITY, null))
        {
            return super.getItemStackDisplayName(stack);
        }

        IShingleCapability capability = stack.getCapability(ModCapabilities.MOD_SHINGLE_CAPABILITY, null);
        if (capability != null)
        {
            return new TextComponentTranslation(this.getUnlocalizedName() + ".name") + " " + capability.getWoodType().getName();
        }

        return super.getItemStackDisplayName(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final NBTTagCompound nbt)
    {
        if (stack.getItem() == null)
            return null;

        CapabilityDispatcher internetParentDispatcher = new CapabilityDispatcher();
        internetParentDispatcher.registerNewInstance(ModCapabilities.MOD_SHINGLE_CAPABILITY);

        if (nbt != null)
        {
            NBTTagCompound parentCompound =
              nbt.getCompoundTag(new ResourceLocation(Constants.MOD_ID, Constants.CAPABILITY_DEFAULT).toString());
            internetParentDispatcher.deserializeNBT(parentCompound);
        }

        return internetParentDispatcher;
    }
}
