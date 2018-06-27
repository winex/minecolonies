package com.minecolonies.coremod.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockPlanks.EnumType;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

public class TileEntityShingle extends TileEntity
{
    private EnumType woodType;

    public EnumType getWoodType()
    {
        return woodType;
    }

    public void setWoodType(final EnumType woodType)
    {
        this.woodType = woodType;
    }

    public TileEntityShingle()
    {
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger(TAG_WOOD_TYPE, woodType.getMetadata());
        return compound;
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.woodType = EnumType.byMetadata(compound.getInteger(TAG_WOOD_TYPE));
    }
}
