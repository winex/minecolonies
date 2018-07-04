package com.minecolonies.coremod.tileentities;

import com.minecolonies.coremod.blocks.decorative.BlockShingleNew;
import net.minecraft.block.BlockPlanks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

public class TileEntityShingle extends TileEntity
{
    private BlockPlanks.EnumType woodType;
    private BlockShingleNew.EnumFace faceType;

    public BlockPlanks.EnumType getWoodType()
    {
        return woodType;
    }

    public BlockShingleNew.EnumFace getFaceType()
    {
        return faceType;
    }

    public void setWoodType(final BlockPlanks.EnumType woodType)
    {
        this.woodType = woodType;
    }

    public void setFaceType(final BlockShingleNew.EnumFace faceType)
    {
        this.faceType = faceType;
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger(TAG_WOOD_TYPE, woodType.getMetadata());
        compound.setInteger(TAG_FACE_TYPE, faceType.getMetadata());
        return compound;
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.woodType = BlockPlanks.EnumType.byMetadata(compound.getInteger(TAG_WOOD_TYPE));
        this.faceType = BlockShingleNew.EnumFace.byMetadata(compound.getInteger(TAG_FACE_TYPE));
    }

    @NotNull
    @Override
    public NBTTagCompound getUpdateTag()
    {
        final NBTTagCompound compound = super.getUpdateTag();
        compound.setInteger(TAG_WOOD_TYPE, woodType.getMetadata());
        compound.setInteger(TAG_FACE_TYPE, faceType.getMetadata());
        return compound;
    }
}
