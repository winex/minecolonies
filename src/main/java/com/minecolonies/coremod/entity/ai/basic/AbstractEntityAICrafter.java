package com.minecolonies.coremod.entity.ai.basic;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requestable.Stack;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.coremod.colony.jobs.AbstractJobCrafter;
import com.minecolonies.coremod.entity.ai.util.AIState;
import com.minecolonies.coremod.entity.ai.util.AITarget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_STATUS_DECIDING;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_STATUS_GATHERING;
import static com.minecolonies.coremod.entity.ai.util.AIState.*;

/**
 * Crafts wood related block when needed.
 */
public abstract class AbstractEntityAICrafter<J extends AbstractJobCrafter> extends AbstractEntityAIInteract<AbstractJobCrafter>
{

    /**
     * The standard delay after each terminated action.
     */
    protected static final int STANDARD_DELAY = 5;

    /**
     * What he currently might be needing.
     */
    protected List<Predicate<ItemStack>> needsCurrently = new ArrayList<>();

    /**
     * The current position the worker should walk to.
     */
    protected BlockPos walkTo = null;

    /**
     * Initialize the sawmill and add all his tasks.
     *
     * @param sawmill the job he has.
     */
    public AbstractEntityAICrafter(@NotNull final AbstractJobCrafter crafter)
    {
        super(crafter);
        super.registerTargets(
          /**
           * Check if tasks should be executed.
           */
          new AITarget(IDLE, START_WORKING),
          new AITarget(START_WORKING, this::startWorking),
          new AITarget(COLLECT_ITEMS, this::collectItems),
          new AITarget(CRAFT, this::craft)
        );
        worker.setCanPickUpLoot(true);
    }

    public AIState startWorking()
    {
        worker.setLatestStatus(new TextComponentTranslation(COM_MINECOLONIES_COREMOD_STATUS_DECIDING));

        if (job.getOpenTasks().isEmpty())
        {
            return START_WORKING;
        }

        if (ifNotAtBuildingWalkTo())
        {
            return START_WORKING;
        }

        if (job.getCurrentRequestToken() != null)
        {
            return COLLECT_ITEMS;
        }

        job.setCurrentRequestToken(job.getOpenTasks().keySet().stream().filter(t -> !job.getOpenTasks().get(t).isEmpty()).findFirst().orElseThrow(null));
        job.setCurrentRecipeStorage(job.getOpenTasks().get(job.getCurrentRequestToken()).get(0));

        return COLLECT_ITEMS;
    }

    public AIState collectItems()
    {
        worker.setLatestStatus(new TextComponentTranslation(COM_MINECOLONIES_COREMOD_STATUS_GATHERING));

        if (job.getCurrentRecipeStorage() == null && (job.getCurrentRequestToken() == null || job.getOpenTasks().get(job.getCurrentRequestToken()).isEmpty()))
        {
            job.setCurrentRequestToken(null);
            job.setCurrentRecipeStorage(null);
            return START_WORKING;
        }

        job.setCurrentRecipeStorage(job.getOpenTasks().get(job.getCurrentRequestToken()).get(0));
        final IRecipeStorage storage = job.getCurrentRecipeStorage();
        if (needsCurrently.isEmpty())
        {
            needsCurrently.addAll(storage.getCleanedInput().stream().map(Stack::new).collect(Collectors.toList()));
        }

        if (walkTo == null && ifNotAtBuildingWalkTo())
        {
            return getState();
        }

        if (!needsCurrently.isEmpty())
        {
            final Predicate<ItemStack> targetPredicate = needsCurrently.stream()
                    .filter(stackPredicate -> !InventoryUtils.hasItemInProvider(worker.getCitizenData(), stackPredicate)).findFirst().orElse(null);
            if (targetPredicate != null)
            {
                if (walkTo == null)
                {
                    final BlockPos pos = getOwnBuilding().getTileEntity().getPositionOfChestWithItemStack(targetPredicate);
                    if (pos == null)
                    {
                        worker.getColony().getRequestManager().updateRequestState(job.getCurrentRequestToken(), RequestState.CANCELLED);
                        return START_WORKING;
                    }
                    walkTo = pos;
                }

                if (walkToBlock(walkTo))
                {
                    setDelay(2);
                    return getState();
                }

                tryTransferFromPosToWorker(walkTo, targetPredicate);
                if (!InventoryUtils.hasItemInProvider(worker.getCitizenData(), targetPredicate))
                {
                    worker.getColony().getRequestManager().updateRequestState(job.getCurrentRequestToken(), RequestState.CANCELLED);
                    return START_WORKING;
                }

                needsCurrently.remove(targetPredicate);
                walkTo = null;

                if (needsCurrently.isEmpty())
                {
                    return CRAFT;
                }
            }
            else
            {
                needsCurrently.clear();
                return CRAFT;
            }
        }

        setDelay(STANDARD_DELAY);
        return START_WORKING;
    }

    public AIState craft()
    {
        if (ifNotAtBuildingWalkTo())
        {
            return getState();
        }

        if (needsCurrently.stream().anyMatch(stackPredicate -> !InventoryUtils.hasItemInProvider(worker.getCitizenData(), stackPredicate)))
        {
            worker.getColony().getRequestManager().updateRequestState(job.getCurrentRequestToken(), RequestState.CANCELLED);
            return START_WORKING;
        }

        job.getCurrentRecipeStorage().fullfillRecipe(ImmutableList.of(new InvWrapper(worker.getInventoryCitizen())));
        needsCurrently.clear();

        incrementActionsDone();

        return START_WORKING;
    }

    @Override
    protected int getActionsDoneUntilDumping()
    {
        return 1;
    }

    @NotNull
    @Override
    protected AIState dumpInventory()
    {
        final AIState state = super.dumpInventory();

        if (state == IDLE)
        {
            job.finishTask(job.getCurrentRequestToken(), job.getCurrentRecipeStorage());
        }

        return state;
    }
}
