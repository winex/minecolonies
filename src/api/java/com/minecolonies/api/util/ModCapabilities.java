package com.minecolonies.api.util;

import com.minecolonies.api.capabilities.IShingleCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ModCapabilities
{
    @CapabilityInject(IShingleCapability.class)
    public static Capability<IShingleCapability> MOD_SHINGLE_CAPABILITY;

    static {
        CapabilityManager.INSTANCE.register(IShingleCapability.class, new IShingleCapability.Storage(), IShingleCapability.Impl.class);
    }

}
