package com.minecolonies.coremod.colony.buildings;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.requestsystem.resolver.IRequestResolver;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.constant.TypeConstants;
import com.minecolonies.coremod.colony.Colony;
import com.minecolonies.coremod.colony.ColonyView;
import com.minecolonies.coremod.colony.requestsystem.resolvers.PublicWorkerCraftingRequestResolver;
import net.minecraft.util.math.BlockPos;

/**
 * Class of the sawmill building.
 */
public abstract class AbstractBuildingCrafter extends AbstractBuildingWorker
{
    /**
     * Extra amount of recipes the crafters can learn.
     */
    private static final int EXTRA_RECIPE_MULTIPLIER = 10;

    /**
     * Instantiates a new sawmill building.
     *
     * @param c the colony.
     * @param l the location
     */
    public AbstractBuildingCrafter(final Colony c, final BlockPos l)
    {
        super(c, l);
    }

    @Override
    public ImmutableCollection<IRequestResolver<?>> getResolvers()
    {
        final ImmutableCollection<IRequestResolver<?>> supers = super.getResolvers();
        final ImmutableList.Builder<IRequestResolver<?>> builder = ImmutableList.builder();

        builder.addAll(supers);
        builder.add(new PublicWorkerCraftingRequestResolver(getRequester().getRequesterLocation(),
                                                 getColony().getRequestManager().getFactoryController().getNewInstance(TypeConstants.ITOKEN)));

        return builder.build();
    }

    @Override
    public boolean canRecipeBeAdded(final IToken token)
    {
        return AbstractBuildingCrafter.canBuildingCanLearnMoreRecipes (getBuildingLevel(), super.getRecipes().size());
    }

    @Override
    public boolean canCraftComplexRecipes()
    {
        return true;
    }

    /**
     * BuildingDeliveryman View.
     */
    public static class View extends AbstractBuildingWorker.View
    {

        /**
         * Instantiate the deliveryman view.
         *
         * @param c the colonyview to put it in
         * @param l the positon
         */
        public View(final ColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        /**
         * Check if an additional recipe can be added.
         * @return true if so.
         */
        public boolean canRecipeBeAdded()
        {
            return AbstractBuildingCrafter.canBuildingCanLearnMoreRecipes (getBuildingLevel(), super.getRecipes().size());
        }
    }

    /**
     * Check if an additional recipe can be added.
     * @param learnedRecipes the learned recipes.
     * @param buildingLevel the building level.
     * @return true if so.
     */
    public static boolean canBuildingCanLearnMoreRecipes(final int buildingLevel, final int learnedRecipes)
    {
        return (Math.pow(2, buildingLevel) * EXTRA_RECIPE_MULTIPLIER) >= (learnedRecipes + 1);
    }
}
