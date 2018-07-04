package com.minecolonies.coremod.blocks.decorative;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.coremod.blocks.AbstractBlockMinecoloniesStairs;
import com.minecolonies.coremod.creativetab.ModCreativeTabs;
import com.minecolonies.coremod.tileentities.TileEntityShingle;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class BlockShingleNew extends AbstractBlockMinecoloniesStairs<BlockShingleNew> implements ITileEntityProvider
{
    public static final PropertyEnum<BlockPlanks.EnumType> WOOD_TYPE = PropertyEnum.create("wood_type", BlockPlanks.EnumType.class);
    public static final PropertyEnum<EnumFace> FACE_TYPE = PropertyEnum.create("face_type", EnumFace.class);

    /**
     * The hardness this block has.
     */
    private static final float BLOCK_HARDNESS = 3F;

    /**
     * The resistance this block has.
     */
    private static final float RESISTANCE = 1F;

    /**
     * Light opacity of the block.
     */
    private static final int LIGHT_OPACITY = 255;

    private BlockPlanks.EnumType currentWood = BlockPlanks.EnumType.OAK;
    private EnumFace currentFace = EnumFace.CLAY;

    /**
     * Prefix of the block.
     */
    public static final String BLOCK_PREFIX = "blockshinglenew";

    public BlockShingleNew(final IBlockState modelState, final String name)
    {
        super(modelState);
        this.setDefaultState(this.blockState.getBaseState()
                               .withProperty(WOOD_TYPE, BlockPlanks.EnumType.OAK)
                               .withProperty(FACE_TYPE, EnumFace.CLAY));
        init(name);
    }

    private void init(final String name)
    {
        setRegistryName(name);
        setUnlocalizedName(String.format("%s.%s", Constants.MOD_ID.toLowerCase(Locale.US), name));
        setCreativeTab(ModCreativeTabs.MINECOLONIES);
        setHardness(BLOCK_HARDNESS);
        setResistance(RESISTANCE);
        this.useNeighborBrightness = true;
        this.setLightOpacity(LIGHT_OPACITY);
    }

    @Override
    public void getSubBlocks(final CreativeTabs itemIn, final NonNullList<ItemStack> items)
    {
        for (final BlockPlanks.EnumType woodType : BlockPlanks.EnumType.values())
        {
            for (final EnumFace faceType : EnumFace.values())
            {
                ItemStack stack = new ItemStack(Item.getItemFromBlock(this));

                final NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger(NbtTagConstants.TAG_WOOD_TYPE, woodType.getMetadata());
                compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, faceType.getMetadata());

                stack.setTagCompound(compound);

                items.add(stack);
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(final World world, final int i)
    {
        return new TileEntityShingle();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        Collection<IProperty<?>> properties = new ArrayList<>(super.createBlockState().getProperties());
        properties.add(WOOD_TYPE);
        properties.add(FACE_TYPE);
        return new ExtendedBlockState(this, properties.toArray(new IProperty<?>[properties.size()]), new IUnlistedProperty[]{});
    }

    @Override
    public IBlockState getActualState(final IBlockState state, final IBlockAccess world, final BlockPos pos)
    {
        final IBlockState currentState = super.getActualState(state, world, pos);

        if (world.getTileEntity(pos) == null) return state;

        final TileEntityShingle tileEntity = (TileEntityShingle) world.getTileEntity(pos);

        BlockPlanks.EnumType woodtype = null;
        EnumFace faceType = null;

        if (tileEntity != null)
        {
            woodtype = tileEntity.getWoodType();
            faceType = tileEntity.getFaceType();
        }

        if (woodtype == null)
        {
            woodtype = BlockPlanks.EnumType.OAK;
        }
        if (faceType == null)
        {
            faceType = EnumFace.CLAY;
        }

        this.currentWood = woodtype;
        this.currentFace = faceType;

        return currentState.withProperty(WOOD_TYPE, woodtype).withProperty(FACE_TYPE, faceType); //TODO: Change that <<<
    }

    @Override
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack)
    {
        final NBTTagCompound compound = stack.getTagCompound();

        if (compound != null)
        {
            final BlockPlanks.EnumType woodType = BlockPlanks.EnumType.byMetadata(compound.getInteger(NbtTagConstants.TAG_WOOD_TYPE));
            final EnumFace faceType = EnumFace.byMetadata(compound.getInteger(NbtTagConstants.TAG_FACE_TYPE));

            TileEntityShingle tileEntity = (TileEntityShingle) worldIn.getTileEntity(pos);

            if (tileEntity != null)
            {
                tileEntity.setWoodType(woodType);
                tileEntity.setFaceType(faceType);
                tileEntity.markDirty();
            }
        }

        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public List<ItemStack> getDrops(final IBlockAccess world, final BlockPos pos, final IBlockState state, final int fortune)
    {
        final List<ItemStack> list = new ArrayList<>();
        list.add(generateItemStackFromWorldPos(state));
        return list;
    }

    @Override
    public void getDrops(@NotNull final NonNullList<ItemStack> drops, final IBlockAccess world, final BlockPos pos, final IBlockState state, final int fortune)
    {
        drops.add(generateItemStackFromWorldPos(state));
    }

    @NotNull
    @Override
    public ItemStack getPickBlock(@NotNull final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player)
    {
        return generateItemStackFromWorldPos(state);
    }

    private ItemStack generateItemStackFromWorldPos(IBlockState state)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger(NbtTagConstants.TAG_WOOD_TYPE, currentWood.getMetadata());
        compound.setInteger(NbtTagConstants.TAG_FACE_TYPE, currentFace.getMetadata());

        final ItemStack stack = new ItemStack(Item.getItemFromBlock(state.getBlock()));
        stack.setTagCompound(compound);

        return stack;
    }

    public enum EnumFace implements IStringSerializable
    {
        //Simple
        CLAY(0, "clay"),

        // Dye colors
        BLACK(1, "black"),
        BLUE(2, "blue"),
        BROWN(3, "brown"),
        CYAN(4, "cyan"),
        DARK_GREY(5, "dark_grey"),
        GREEN(6, "green"),
        LIGHT_BLUE(7, "light_blue"),
        LIME(8, "lime"),
        MAGENTA(9, "magenta"),
        ORANGE(10, "orange"),
        PINK(11, "pink"),
        PURPLE(12, "purple"),
        RED(13, "red"),
        WHITE(14, "white"),
        YELLOW(15, "yellow"),

        // Slate variants
        BLUE_SLATE(16, "blue_slate"),
        GREEN_SLATE(17, "green_slate"),
        GREY_SLATE(18, "grey_slate"),
        MOSS_SLATE(19, "moss_slate"),
        PURPLE_SLATE(20, "purple_slate")
        ;

        private static final EnumFace[] META_LOOKUP = new EnumFace[values().length];
        private final int meta;
        private final String name;

        EnumFace(int metaIn, String nameIn) {
            this.meta = metaIn;
            this.name = nameIn;
        }

        public int getMetadata() {
            return this.meta;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static EnumFace byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName() {
            return this.name;
        }

        static {
            EnumFace[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2) {
                EnumFace enumFace = var0[var2];
                META_LOOKUP[enumFace.getMetadata()] = enumFace;
            }

        }
    }
}
