package com.minecolonies.api.colony.requestsystem.data.job;

import com.minecolonies.api.colony.requestsystem.data.IDataStore;
import com.minecolonies.api.colony.requestsystem.token.IToken;

import java.util.Set;

public interface IRequestSystemJobDataStore extends IDataStore
{
    Set<IToken<?>> getAsyncRequestTokens();
}
