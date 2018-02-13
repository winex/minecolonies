package com.minecolonies.api.colony.requestsystem.data.job;

import com.minecolonies.api.colony.requestsystem.data.IDataStore;
import com.minecolonies.api.colony.requestsystem.token.IToken;

import java.util.LinkedList;

public interface IRequestSystemDeliveryManJobDataStore extends IRequestSystemJobDataStore
{

    LinkedList<IToken<?>> getQueue();

    boolean isReturning();

    void setReturning(final boolean returning);
}
