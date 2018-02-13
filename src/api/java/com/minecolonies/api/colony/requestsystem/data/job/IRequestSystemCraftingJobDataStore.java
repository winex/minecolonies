package com.minecolonies.api.colony.requestsystem.data.job;

import com.minecolonies.api.colony.requestsystem.data.IDataStore;
import com.minecolonies.api.colony.requestsystem.token.IToken;

import java.util.List;

public interface IRequestSystemCraftingJobDataStore extends IRequestSystemJobDataStore
{

    List<IToken<?>> getOpenRequests();
}
