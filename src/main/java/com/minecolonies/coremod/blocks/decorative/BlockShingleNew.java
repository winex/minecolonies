package com.minecolonies.coremod.blocks.decorative;

import com.minecolonies.api.capabilities.IShingleCapability;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.coremod.blocks.AbstractBlockMinecoloniesStairs;
import com.minecolonies.coremod.creativetab.ModCreativeTabs;
import com.minecolonies.coremod.items.ItemBlockShingle;
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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class BlockShingleNew extends AbstractBlockMinecoloniesStairs<BlockShingleNew> implements ITileEntityProvider
{
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);

    private ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[]{VARIANT}, new IUnlistedProperty[]{});

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

    /**
     * Prefix of the block.
     */
    public static final String BLOCK_PREFIX = "blockshinglenew";

    public BlockShingleNew(final IBlockState modelState, final String name)
    {
        super(modelState);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK));
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
        //TODO: implement
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
        properties.add(VARIANT);
        return new ExtendedBlockState(this, properties.toArray(new IProperty<?>[properties.size()]), new IUnlistedProperty[]{});
    }

    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos)
    {
        if (world.getTileEntity(pos) == null) return this.state.getBaseState();

        final TileEntityShingle tileEntity = (TileEntityShingle) world.getTileEntity(pos);

        BlockPlanks.EnumType woodtype = null;

        if (tileEntity != null)
        {
            woodtype = tileEntity.getWoodType();
        }

        if (woodtype == null)
        {
            woodtype = BlockPlanks.EnumType.OAK;
        }

        return state.withProperty(VARIANT, woodtype);
    }

    @Override
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack)
    {
        if (!stack.hasCapability(ModCapabilities.MOD_SHINGLE_CAPABILITY, null))
            throw new IllegalArgumentException("The given stack cannot make a shingle! It does not have the correct Capability!");

        final TileEntityShingle tileEntity = (TileEntityShingle) worldIn.getTileEntity(pos);

        if (tileEntity != null
              && stack.hasCapability(ModCapabilities.MOD_SHINGLE_CAPABILITY, null)
              && stack.getCapability(ModCapabilities.MOD_SHINGLE_CAPABILITY, null) != null)
        {
            final IShingleCapability capability = stack.getCapability(ModCapabilities.MOD_SHINGLE_CAPABILITY, null);

            if (capability != null)
            {
                tileEntity.setWoodType(capability.getWoodType());
            }
        }

        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void getDrops(final NonNullList<ItemStack> drops, final IBlockAccess world, final BlockPos pos, final IBlockState state, final int fortune)
    {
        drops.add(generateItemStackFromWorldPos(world, pos, state));
    }

    @Override
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player)
    {
        return generateItemStackFromWorldPos(world, pos, state);
    }

    private ItemStack generateItemStackFromWorldPos(IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntity worldEntity = world.getTileEntity(pos);

        if(!(worldEntity instanceof TileEntityShingle))
            return ItemStack.EMPTY;

        return new ItemStack(new ItemBlockShingle(this));
    }
}
