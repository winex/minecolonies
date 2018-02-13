package com.minecolonies.api.colony.requestsystem.data.job;

import com.minecolonies.api.colony.requestsystem.data.IDataStore;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IRecipeStorage;

import java.util.List;
import java.util.Map;

public interface IRequestSystemCraftingJobDataStore extends IRequestSystemJobDataStore
{

    Map<IToken<?>, List<IRecipeStorage>> getOpenRequests();
}
