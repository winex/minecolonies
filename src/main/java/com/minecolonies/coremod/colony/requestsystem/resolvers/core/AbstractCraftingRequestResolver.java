package com.minecolonies.coremod.colony.requestsystem.resolvers.core;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requestable.Stack;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IRecipeStorage;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.blockout.Log;
import com.minecolonies.coremod.colony.buildings.AbstractBuilding;
import com.minecolonies.coremod.colony.buildings.AbstractBuildingWorker;
import com.minecolonies.coremod.colony.requestsystem.requesters.IBuildingBasedRequester;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractCraftingRequestResolver extends AbstractRequestResolver<Stack> implements IBuildingBasedRequester
{

    private final boolean isPublicCrafter;
    private final boolean preCheckOnAttempt;

    public AbstractCraftingRequestResolver(
      @NotNull final ILocation location,
      @NotNull final IToken<?> token, final boolean isPublicCrafter, final boolean preCheckOnAttempt)
    {
        super(location, token);
        this.isPublicCrafter = isPublicCrafter;
        this.preCheckOnAttempt = preCheckOnAttempt;
    }

    @Override
    public TypeToken<? extends Stack> getRequestType()
    {
        return TypeToken.of(Stack.class);
    }

    @Override
    public Optional<IRequester> getBuilding(
      @NotNull final IRequestManager manager, @NotNull final IToken<?> token)
    {
        if (!manager.getColony().getWorld().isRemote)
        {
            return Optional.ofNullable(manager.getColony().getRequesterBuildingForPosition(getRequesterLocation().getInDimensionLocation()));
        }

        return Optional.empty();
    }

    @Override
    public boolean canResolve(
      @NotNull final IRequestManager manager, final IRequest<? extends Stack> requestToCheck)
    {
        if (!manager.getColony().getWorld().isRemote)
        {
            final ILocation requesterLocation = requestToCheck.getRequester().getRequesterLocation();
            if (isPublicCrafter || requesterLocation.equals(getRequesterLocation()))
            {
                final Optional<AbstractBuilding> building = getBuilding(manager, requestToCheck.getToken()).map(r -> (AbstractBuilding) r);
                return building.map(b -> canResolveForBuilding(manager, requestToCheck, b)).orElse(false);
            }
        }

        return false;
    }

    public boolean canResolveForBuilding(
      @NotNull final IRequestManager manager, @NotNull final IRequest<? extends Stack> request, @NotNull final AbstractBuilding building)
    {
        if (createsCraftingCycle(manager, request, request))
        {
            return false;
        }

        return building instanceof AbstractBuildingWorker && canBuildingCraftStack((AbstractBuildingWorker) building, request.getRequest().getStack())
                 && canRequestBeAssignedToWorker(manager, request, (AbstractBuildingWorker) building);
    }

    protected boolean canRequestBeAssignedToWorker(
      @NotNull final IRequestManager manager,
      @NotNull final IRequest<? extends Stack> request,
      @NotNull final AbstractBuildingWorker worker)
    {
        return true;
    }

    protected boolean createsCraftingCycle(@NotNull final IRequestManager manager, @NotNull final IRequest<?> request, @NotNull final IRequest<? extends Stack> target)
    {
        if (!request.equals(target) && request.getRequest().equals(target.getRequest()))
        {
            return true;
        }

        if (!request.hasParent())
        {
            return false;
        }

        return createsCraftingCycle(manager, manager.getRequestForToken(request.getParent()), target);
    }

    protected abstract boolean canBuildingCraftStack(@NotNull final AbstractBuildingWorker building, ItemStack stack);

    @Nullable
    @Override
    public List<IToken<?>> attemptResolve(
      @NotNull final IRequestManager manager, @NotNull final IRequest<? extends Stack> request)
    {
        final AbstractBuilding building = getBuilding(manager, request.getToken()).map(r -> (AbstractBuilding) r).get();
        final List<IToken<?>> requests = attemptResolveForBuilding(manager, request, building);

        if (requests != null)
        {
            return assignRequestToWorker(manager, request, requests);
        }

        return null;
    }

    @Nullable
    protected List<IToken<?>> assignRequestToWorker(
      @NotNull final IRequestManager manager,
      @NotNull final IRequest<? extends Stack> request,
      @NotNull final List<IToken<?>> currentChildRequests)
    {
        return currentChildRequests;
    }

    @Nullable
    public List<IToken<?>> attemptResolveForBuilding(
      @NotNull final IRequestManager manager, @NotNull final IRequest<? extends Stack> request, @NotNull final AbstractBuilding building)
    {
        final AbstractBuildingWorker buildingWorker = (AbstractBuildingWorker) building;
        return attemptResolveForBuildingAndStack(manager, buildingWorker, request.getRequest().getStack());
    }

    @Nullable
    protected List<IToken<?>> attemptResolveForBuildingAndStack(@NotNull final IRequestManager manager, @NotNull final AbstractBuildingWorker building, final ItemStack stack)
    {
        final IRecipeStorage fullfillableCrafting = building.getFirstFullFillableRecipe(stack);
        if (preCheckOnAttempt && fullfillableCrafting != null)
        {
            return ImmutableList.of();
        }

        final IRecipeStorage craftableCrafting = building.getFirstRecipe(stack);
        if (craftableCrafting == null)
        {
            return null;
        }

        return createRequestsForRecipe(manager, building, stack, craftableCrafting);
    }

    protected int calculateMaxCraftingCount(@NotNull final ItemStack outputStack, @NotNull final IRecipeStorage storage)
    {
        //Calculate the initial crafting count from the request and the storage output.
        int craftingCount = Math.max(ItemStackUtils.getSize(outputStack), ItemStackUtils.getSize(storage.getPrimaryOutput())) / ItemStackUtils.getSize(storage.getPrimaryOutput());

        //Now check if we excede an ingredients max stack size.
        for (final ItemStack ingredient : storage.getInput())
        {
            if (ItemStackUtils.isEmpty(ingredient))
            {
                continue;
            }

            //Calculate the input count for the ingredient.
            final int ingredientInputCount = ItemStackUtils.getSize(ingredient) * craftingCount;
            //Check if we are above the max stacksize.
            if (ingredientInputCount > ingredient.getMaxStackSize())
            {
                //Recalculate the crafting limit using the maxstacksize of the ingredient.
                craftingCount = Math.max(ingredient.getMaxStackSize(), ItemStackUtils.getSize(storage.getPrimaryOutput())) / ItemStackUtils.getSize(storage.getPrimaryOutput());
            }
        }

        return craftingCount;
    }

    @Nullable
    protected List<IToken<?>> createRequestsForRecipe(
      @NotNull final IRequestManager manager,
      @NotNull final AbstractBuildingWorker building,
      final ItemStack requestStack,
      @NotNull final IRecipeStorage storage)
    {
        final int craftingCount = calculateMaxCraftingCount(requestStack, storage);
        return storage.getCleanedInput().stream()
                 .filter(s -> !ItemStackUtils.isEmpty(s.getItemStack()))
                 .map(stack -> {
                     final ItemStack craftingHelperStack = stack.getItemStack().copy();
                     ItemStackUtils.setSize(craftingHelperStack, stack.getAmount() * craftingCount);

                     return createNewRequestForStack(manager, craftingHelperStack);
                 }).collect(Collectors.toList());
    }

    @Nullable
    protected IToken<?> createNewRequestForStack(@NotNull final IRequestManager manager, final ItemStack stack)
    {
        return manager.createRequest(this, new Stack(stack.copy()));
    }

    @Override
    public void resolve(
      @NotNull final IRequestManager manager, @NotNull final IRequest<? extends Stack> request)
    {
        final AbstractBuilding building = getBuilding(manager, request.getToken()).map(r -> (AbstractBuilding) r).get();
        resolveForBuilding(manager, request, building);
    }

    public void resolveForBuilding(@NotNull final IRequestManager manager, @NotNull final IRequest<? extends Stack> request, @NotNull final AbstractBuilding building)
    {
        final AbstractBuildingWorker buildingWorker = (AbstractBuildingWorker) building;
        final IRecipeStorage storage = buildingWorker.getFirstFullFillableRecipe(request.getRequest().getStack());

        if (storage == null)
        {
            Log.getLogger().error("Failed to craft a crafting recipe of: " + request.getRequest().getStack().toString() + ". Its ingredients are missing.");
            return;
        }

        final int craftingCount = calculateMaxCraftingCount(request.getRequest().getStack(), storage);

        performCraftingForBuilding(buildingWorker, manager, request.getToken(), storage, craftingCount);
        onCraftingCompleted(manager, request);
    }

    protected void performCraftingForBuilding(
      @NotNull final AbstractBuildingWorker worker,
      @NotNull final IRequestManager manager,
      @NotNull final IToken<?> requestToken,
      @NotNull final IRecipeStorage recipeStorage,
      final int craftingCount)
    {
        for (int i = 0; i < craftingCount; i++)
        {
            worker.fullFillRecipe(recipeStorage);
        }
    }

    protected void onCraftingCompleted(@NotNull final IRequestManager manager, @NotNull final IRequest<? extends Stack> request)
    {
        manager.updateRequestState(request.getToken(), RequestState.POST_PROCESSING);
    }
}
