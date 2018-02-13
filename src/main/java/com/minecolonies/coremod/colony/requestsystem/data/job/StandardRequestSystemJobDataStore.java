package com.minecolonies.coremod.colony.requestsystem.data.job;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.data.job.IRequestSystemJobDataStore;
import com.minecolonies.api.colony.requestsystem.factory.FactoryVoidInput;
import com.minecolonies.api.colony.requestsystem.factory.IFactory;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.api.util.constant.TypeConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

public class StandardRequestSystemJobDataStore implements IRequestSystemJobDataStore
{
    private IToken<?> id;
    private final Set<IToken<?>> asyncRequestTokens;

    public StandardRequestSystemJobDataStore(final IToken<?> id, final Set<IToken<?>> asyncRequestTokens) {
        this.id = id;
        this.asyncRequestTokens = asyncRequestTokens;
    }

    public StandardRequestSystemJobDataStore()
    {
        this(
          StandardFactoryController.getInstance().getNewInstance(TypeConstants.ITOKEN),
          new HashSet<>()
        );
    }

    @Override
    public Set<IToken<?>> getAsyncRequestTokens()
    {
        return asyncRequestTokens;
    }

    @Override
    public IToken<?> getId()
    {
        return id;
    }

    @Override
    public void setId(final IToken<?> id)
    {
        this.id = id;
    }

    public static class Factory implements IFactory<FactoryVoidInput, StandardRequestSystemJobDataStore>
    {

        @NotNull
        @Override
        public TypeToken<? extends StandardRequestSystemJobDataStore> getFactoryOutputType()
        {
            return TypeToken.of(StandardRequestSystemJobDataStore.class);
        }

        @NotNull
        @Override
        public TypeToken<? extends FactoryVoidInput> getFactoryInputType()
        {
            return TypeConstants.FACTORYVOIDINPUT;
        }

        @NotNull
        @Override
        public StandardRequestSystemJobDataStore getNewInstance(
          @NotNull final IFactoryController factoryController, @NotNull final FactoryVoidInput factoryVoidInput, @NotNull final Object... context) throws IllegalArgumentException
        {
            return new StandardRequestSystemJobDataStore();
        }

        @NotNull
        @Override
        public NBTTagCompound serialize(
          @NotNull final IFactoryController controller, @NotNull final StandardRequestSystemJobDataStore standardRequestSystemJobDataStore)
        {
            final NBTTagCompound compound = new NBTTagCompound();

            compound.setTag(TAG_TOKEN, controller.serialize(standardRequestSystemJobDataStore.id));
            compound.setTag(TAG_RS_JOB_ASYNC, standardRequestSystemJobDataStore.getAsyncRequestTokens().stream().map(controller::serialize).collect(NBTUtils.toNBTTagList()));

            return compound;
        }

        @NotNull
        @Override
        public StandardRequestSystemJobDataStore deserialize(@NotNull final IFactoryController controller, @NotNull final NBTTagCompound nbt) throws Throwable
        {
            if (!nbt.hasKey(TAG_TOKEN))
            {
                return new StandardRequestSystemJobDataStore();
            }

            final IToken<?> token = controller.deserialize(nbt.getCompoundTag(TAG_TOKEN));
            final Set<IToken<?>> asyncRequests = NBTUtils.streamCompound(nbt.getTagList(TAG_RS_JOB_ASYNC, Constants.NBT.TAG_COMPOUND))
                                                   .map(nbtTagCompound -> (IToken<?>) controller.deserialize(nbtTagCompound))
                                                   .collect(Collectors.toSet());

            return new StandardRequestSystemJobDataStore(token, asyncRequests);
        }
    }
}
