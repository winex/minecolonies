package com.minecolonies.coremod.colony.jobs;

import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.data.job.IRequestSystemCraftingJobDataStore;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.api.util.constant.TypeConstants;
import com.minecolonies.coremod.client.render.RenderBipedCitizen;
import com.minecolonies.coremod.colony.CitizenData;
import com.minecolonies.coremod.entity.ai.basic.AbstractAISkeleton;
import com.minecolonies.coremod.entity.ai.citizen.sawmill.deliveryman.EntityAIWorkSawmill;
import com.minecolonies.coremod.sounds.DeliverymanSounds;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class of the deliveryman job.
 */
public class JobSawmill extends AbstractJob<IRequestSystemCraftingJobDataStore>
{
    private IToken<?> rsDataStoreToken;

    private IToken<?> currentRequestToken;
    private IRecipeStorage currentRecipeStorage;

    /**
     * Instantiates the job for the deliveryman.
     *
     * @param entity the citizen who becomes a deliveryman
     */
    public JobSawmill(final CitizenData entity)
    {
        super(entity, TypeConstants.REQUEST_SYSTEM_CRAFTING_JOB_DATA_STORE);
        setupRsDataStore();
    }

    private void setupRsDataStore()
    {
        rsDataStoreToken = this.getCitizen()
                             .getColony()
                             .getRequestManager()
                             .getDataStoreManager()
                             .get(
                               StandardFactoryController.getInstance().getNewInstance(TypeConstants.ITOKEN),
                               TypeConstants.REQUEST_SYSTEM_DELIVERY_MAN_JOB_DATA_STORE
                             )
                             .getId();
    }

    @Override
    public void readFromNBT(@NotNull final NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey(NbtTagConstants.TAG_RS_DMANJOB_DATASTORE))
        {
            rsDataStoreToken = StandardFactoryController.getInstance().deserialize(compound.getCompoundTag(NbtTagConstants.TAG_RS_DMANJOB_DATASTORE));
        }
        else
        {
            setupRsDataStore();
        }
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.Deliveryman";
    }

    @NotNull
    @Override
    public RenderBipedCitizen.Model getModel()
    {
        return RenderBipedCitizen.Model.CRAFTER;
    }

    @Override
    public void writeToNBT(@NotNull final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag(NbtTagConstants.TAG_RS_DMANJOB_DATASTORE, StandardFactoryController.getInstance().serialize(rsDataStoreToken));
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public AbstractAISkeleton<JobSawmill> generateAI()
    {
        return new EntityAIWorkSawmill(this);
    }

    @Override
    public SoundEvent getBedTimeSound()
    {
        if (getCitizen() != null)
        {
            return getCitizen().isFemale() ? DeliverymanSounds.Female.offToBed : null;
        }
        return null;
    }

    @Nullable
    @Override
    public SoundEvent getBadWeatherSound()
    {
        if (getCitizen() != null)
        {
            return getCitizen().isFemale() ? DeliverymanSounds.Female.badWeather : null;
        }
        return null;
    }

    @Nullable
    @Override
    public SoundEvent getMoveAwaySound()
    {
        if (getCitizen() != null)
        {
            return getCitizen().isFemale() ? DeliverymanSounds.Female.hostile : null;
        }
        return null;
    }

    public Map<IToken<?>, List<IRecipeStorage>> getOpenTasks()
    {
        return getDataStore().getOpenRequests();
    }

    public void addTask(@NotNull final IToken<?> token, @NotNull final IRecipeStorage recipeStorage)
    {
        getOpenTasks().putIfAbsent(token, new ArrayList<>());
        getOpenTasks().get(token).add(recipeStorage);
    }

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

    public void onTaskDeletion(@NotNull final IToken<?> token)
    {
        getOpenTasks().remove(token);
    }

    public IToken<?> getCurrentRequestToken()
    {
        return currentRequestToken;
    }

    public void setCurrentRequestToken(final IToken<?> currentRequestToken)
    {
        this.currentRequestToken = currentRequestToken;
    }

    public IRecipeStorage getCurrentRecipeStorage()
    {
        return currentRecipeStorage;
    }

    public void setCurrentRecipeStorage(final IRecipeStorage currentRecipeStorage)
    {
        this.currentRecipeStorage = currentRecipeStorage;
    }
}
