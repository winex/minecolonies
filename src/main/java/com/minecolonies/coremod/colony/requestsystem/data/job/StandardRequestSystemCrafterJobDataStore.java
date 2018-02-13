package com.minecolonies.coremod.colony.requestsystem.data.job;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.data.job.IRequestSystemCraftingJobDataStore;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

public class StandardRequestSystemCrafterJobDataStore extends StandardRequestSystemJobDataStore implements IRequestSystemCraftingJobDataStore
{

    private final List<IToken<?>> openRequests;

    public StandardRequestSystemCrafterJobDataStore(
      final IToken<?> id,
      final Set<IToken<?>> asyncRequestTokens,
      final List<IToken<?>> openRequests)
    {
        super(id, asyncRequestTokens);
        this.openRequests = openRequests;
    }

    public StandardRequestSystemCrafterJobDataStore()
    {
        super();
        this.openRequests = new LinkedList<>();
    }

    @Override
    public List<IToken<?>> getOpenRequests()
    {
        return openRequests;
    }

    public static class Factory implements IFactory<FactoryVoidInput, StandardRequestSystemCrafterJobDataStore>
    {

        @NotNull
        @Override
        public TypeToken<? extends StandardRequestSystemCrafterJobDataStore> getFactoryOutputType()
        {
            return TypeToken.of(StandardRequestSystemCrafterJobDataStore.class);
        }

        @NotNull
        @Override
        public TypeToken<? extends FactoryVoidInput> getFactoryInputType()
        {
            return TypeConstants.FACTORYVOIDINPUT;
        }

        @NotNull
        @Override
        public StandardRequestSystemCrafterJobDataStore getNewInstance(
          @NotNull final IFactoryController factoryController, @NotNull final FactoryVoidInput factoryVoidInput, @NotNull final Object... context) throws IllegalArgumentException
        {
            return new StandardRequestSystemCrafterJobDataStore();
        }

        @NotNull
        @Override
        public NBTTagCompound serialize(
          @NotNull final IFactoryController controller, @NotNull final StandardRequestSystemCrafterJobDataStore standardRequestSystemDeliveryManJobDataStore)
        {
            final NBTTagCompound compound = new NBTTagCompound();

            compound.setTag(TAG_TOKEN, controller.serialize(standardRequestSystemDeliveryManJobDataStore.getId()));
            compound.setTag(TAG_RS_JOB_ASYNC, standardRequestSystemDeliveryManJobDataStore.getAsyncRequestTokens().stream().map(controller::serialize).collect(NBTUtils.toNBTTagList()));
            compound.setTag(TAG_RS_JOB_CRAFTER_TASKS, standardRequestSystemDeliveryManJobDataStore.openRequests.stream().map(controller::serialize).collect(NBTUtils.toNBTTagList()));

            return compound;
        }

        @NotNull
        @Override
        public StandardRequestSystemCrafterJobDataStore deserialize(@NotNull final IFactoryController controller, @NotNull final NBTTagCompound nbt) throws Throwable
        {
            if (!nbt.hasKey(TAG_RS_JOB_CRAFTER_TASKS))
            {
                return new StandardRequestSystemCrafterJobDataStore();
            }

            final IToken<?> token = controller.deserialize(nbt.getCompoundTag(TAG_TOKEN));
            final Set<IToken<?>> asyncRequests = NBTUtils.streamCompound(nbt.getTagList(TAG_RS_JOB_ASYNC, Constants.NBT.TAG_COMPOUND))
                                                   .map(nbtTagCompound -> (IToken<?>) controller.deserialize(nbtTagCompound))
                                                   .collect(Collectors.toSet());
            final LinkedList<IToken<?>> openRequests = NBTUtils.streamCompound(nbt.getTagList(TAG_LIST, Constants.NBT.TAG_COMPOUND))
                                          .map(nbtTagCompound -> (IToken<?>) controller.deserialize(nbtTagCompound))
                                          .collect(Collectors.toCollection(LinkedList<IToken<?>>::new));

            return new StandardRequestSystemCrafterJobDataStore(token, asyncRequests, openRequests);
        }
    }
}
