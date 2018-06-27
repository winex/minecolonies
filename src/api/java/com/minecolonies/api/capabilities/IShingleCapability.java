package com.minecolonies.api.capabilities;

import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IShingleCapability
{

    @Nonnull
    EnumType getWoodType();

    IShingleCapability setWoodType(@Nonnull EnumType woodType);

    class Impl implements IShingleCapability
    {
        @Nonnull
        private EnumType woodType;

        public Impl()
        {
            this.woodType = EnumType.OAK;
        }

        @Nonnull
        @Override
        public EnumType getWoodType()
        {
            return this.woodType;
        }

        @Override
        public IShingleCapability setWoodType(@Nonnull final EnumType woodType)
        {
            this.woodType = woodType;
            return this;
        }

        @Override
        public String toString()
        {
            return "Shingle{"
                     + "woodType="
                     + woodType
                     + "}";
        }
    }

    class Storage implements Capability.IStorage<IShingleCapability>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(final Capability<IShingleCapability> capability, final IShingleCapability iShingleCapability, final EnumFacing enumFacing)
        {
            final NBTTagCompound compound = new NBTTagCompound();

            compound.setInteger(TAG_WOOD_TYPE, iShingleCapability.getWoodType().getMetadata());

            return compound;
        }

        @Override
        public void readNBT(final Capability<IShingleCapability> capability, final IShingleCapability iShingleCapability, final EnumFacing enumFacing, final NBTBase nbtBase)
        {
            final NBTTagCompound compound = (NBTTagCompound) nbtBase;
            iShingleCapability.setWoodType(EnumType.byMetadata(compound.getInteger(TAG_WOOD_TYPE)));
        }
    }

}
