package com.minecolonies.coremod.colony.jobs;

import com.minecolonies.api.colony.requestsystem.data.job.IRequestSystemCraftingJobDataStore;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.util.constant.TypeConstants;
import com.minecolonies.coremod.colony.CitizenData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class of the deliveryman job.
 */
public abstract class AbstractJobCrafter extends AbstractJob<IRequestSystemCraftingJobDataStore>
{
    private IToken<?> currentRequestToken;
    private IRecipeStorage currentRecipeStorage;

    /**
     * Instantiates the job for the deliveryman.
     *
     * @param entity the citizen who becomes a deliveryman
     */
    public AbstractJobCrafter(final CitizenData entity)
    {
        super(entity, TypeConstants.REQUEST_SYSTEM_CRAFTING_JOB_DATA_STORE);
    }

    /**
     * Getter for the open tasks.
     * @return a map of all open tasks.
     */
    public Map<IToken<?>, List<IRecipeStorage>> getOpenTasks()
    {
        return getDataStore().getOpenRequests();
    }

    /**
     * Add a new task to the job.
     * @param token the token.
     * @param recipeStorage the recipe.
     */
    public void addTask(@NotNull final IToken<?> token, @NotNull final IRecipeStorage recipeStorage)
    {
        getOpenTasks().putIfAbsent(token, new ArrayList<>());
        getOpenTasks().get(token).add(recipeStorage);
    }

    /**
     * Finish a certain task.
     * @param token it's token.
     * @param recipeStorage the recipe.
     */
    public void finishTask(@NotNull final IToken<?> token, @NotNull final IRecipeStorage recipeStorage)
    {
        if (!getOpenTasks().containsKey(token))
        {
            return;
        }

        if (getOpenTasks().get(token).isEmpty())
        {
            getOpenTasks().remove(token);
            return;
        }

        getOpenTasks().get(token).remove(recipeStorage);
        if (getOpenTasks().get(token).isEmpty())
        {
            getOpenTasks().remove(token);

            getColony().getRequestManager().updateRequestState(token, RequestState.POST_PROCESSING);
            setCurrentRequestToken(null);
        }

        setCurrentRecipeStorage(null);
    }

    /**
     * Called on deletion of the task.
     * @param token the deleted task token.
     */
    public void onTaskDeletion(@NotNull final IToken<?> token)
    {
        getOpenTasks().remove(token);
    }

    /**
     * Get the token of the current request.
     * @return the token.
     */
    public IToken<?> getCurrentRequestToken()
    {
        return currentRequestToken;
    }

    /**
     * Set the current request.
     * @param currentRequestToken the token to set.
     */
    public void setCurrentRequestToken(final IToken<?> currentRequestToken)
    {
        this.currentRequestToken = currentRequestToken;
    }

    /**
     * Get the IRecipeStorage of the current request.
     * @return the IRecipeStorage
     */
    public IRecipeStorage getCurrentRecipeStorage()
    {
        return currentRecipeStorage;
    }

    /**
     * Set the IRecipeStorage of the current request.
     * @param currentRecipeStorage the IRecipeStorage.
     */
    public void setCurrentRecipeStorage(final IRecipeStorage currentRecipeStorage)
    {
        this.currentRecipeStorage = currentRecipeStorage;
    }
}
