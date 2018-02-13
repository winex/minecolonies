package com.minecolonies.coremod.colony.requestsystem.data.job;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.data.job.IRequestSystemDeliveryManJobDataStore;
import com.minecolonies.api.colony.requestsystem.factory.FactoryVoidInput;
import com.minecolonies.api.colony.requestsystem.factory.IFactory;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.api.util.constant.TypeConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

public class StandardRequestSystemDeliveryManJobDataStore extends StandardRequestSystemJobDataStore implements IRequestSystemDeliveryManJobDataStore
{

    private final LinkedList<IToken<?>> queue;
    private boolean returning;

    public StandardRequestSystemDeliveryManJobDataStore(
      final IToken<?> id,
      final Set<IToken<?>> asyncRequestTokens,
      final LinkedList<IToken<?>> queue,
      final boolean returning)
    {
        super(id, asyncRequestTokens);
        this.queue = queue;
        this.returning = returning;
    }

    public StandardRequestSystemDeliveryManJobDataStore()
    {
        super();
        this.queue = new LinkedList<>();
        this.returning = false;
    }

    @Override
    public LinkedList<IToken<?>> getQueue()
    {
        return queue;
    }

    @Override
    public boolean isReturning()
    {
        return returning;
    }

    @Override
    public void setReturning(final boolean returning)
    {
        this.returning = returning;
    }

    public static class Factory implements IFactory<FactoryVoidInput, StandardRequestSystemDeliveryManJobDataStore>
    {

        @NotNull
        @Override
        public TypeToken<? extends StandardRequestSystemDeliveryManJobDataStore> getFactoryOutputType()
        {
            return TypeToken.of(StandardRequestSystemDeliveryManJobDataStore.class);
        }

        @NotNull
        @Override
        public TypeToken<? extends FactoryVoidInput> getFactoryInputType()
        {
            return TypeConstants.FACTORYVOIDINPUT;
        }

        @NotNull
        @Override
        public StandardRequestSystemDeliveryManJobDataStore getNewInstance(
          @NotNull final IFactoryController factoryController, @NotNull final FactoryVoidInput factoryVoidInput, @NotNull final Object... context) throws IllegalArgumentException
        {
            return new StandardRequestSystemDeliveryManJobDataStore();
        }

        @NotNull
        @Override
        public NBTTagCompound serialize(
          @NotNull final IFactoryController controller, @NotNull final StandardRequestSystemDeliveryManJobDataStore standardRequestSystemDeliveryManJobDataStore)
        {
            final NBTTagCompound compound = new NBTTagCompound();

            compound.setTag(TAG_TOKEN, controller.serialize(standardRequestSystemDeliveryManJobDataStore.getId()));
            compound.setTag(TAG_RS_JOB_ASYNC, standardRequestSystemDeliveryManJobDataStore.getAsyncRequestTokens().stream().map(controller::serialize).collect(NBTUtils.toNBTTagList()));
            compound.setTag(TAG_RS_JOB_DMAN_TASKS, standardRequestSystemDeliveryManJobDataStore.queue.stream().map(controller::serialize).collect(NBTUtils.toNBTTagList()));
            compound.setBoolean(TAG_RS_JOB_DMAN_RETURNING, standardRequestSystemDeliveryManJobDataStore.returning);

            return compound;
        }

        @NotNull
        @Override
        public StandardRequestSystemDeliveryManJobDataStore deserialize(@NotNull final IFactoryController controller, @NotNull final NBTTagCompound nbt) throws Throwable
        {
            if (!nbt.hasKey(TAG_RS_JOB_DMAN_RETURNING))
            {
                return new StandardRequestSystemDeliveryManJobDataStore();
            }

            final IToken<?> token = controller.deserialize(nbt.getCompoundTag(TAG_TOKEN));
            final Set<IToken<?>> asyncRequests = NBTUtils.streamCompound(nbt.getTagList(TAG_RS_JOB_ASYNC, Constants.NBT.TAG_COMPOUND))
                                                   .map(nbtTagCompound -> (IToken<?>) controller.deserialize(nbtTagCompound))
                                                   .collect(Collectors.toSet());
            final LinkedList<IToken<?>> queue = NBTUtils.streamCompound(nbt.getTagList(TAG_LIST, Constants.NBT.TAG_COMPOUND))
                                          .map(nbtTagCompound -> (IToken<?>) controller.deserialize(nbtTagCompound))
                                          .collect(Collectors.toCollection(LinkedList<IToken<?>>::new));
            final boolean returning = nbt.getBoolean(TAG_VALUE);

            return new StandardRequestSystemDeliveryManJobDataStore(token, asyncRequests, queue, returning);
        }
    }
}
