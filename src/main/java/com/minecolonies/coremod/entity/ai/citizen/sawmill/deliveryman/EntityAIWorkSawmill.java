package com.minecolonies.coremod.entity.ai.citizen.sawmill.deliveryman;

import com.minecolonies.coremod.colony.jobs.JobSawmill;
import com.minecolonies.coremod.entity.ai.basic.AbstractEntityAICrafter;
import com.minecolonies.coremod.entity.ai.basic.AbstractEntityAIInteract;
import com.minecolonies.coremod.entity.ai.util.AITarget;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_STATUS_DECIDING;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_STATUS_GATHERING;
import static com.minecolonies.coremod.entity.ai.util.AIState.*;

/**
 * Crafts wood related block when needed.
 */
public class EntityAIWorkSawmill extends AbstractEntityAICrafter
{
    /**
     * Initialize the sawmill and add all his tasks.
     *
     * @param sawmill the job he has.
     */
    public EntityAIWorkSawmill(@NotNull final JobSawmill sawmill)
    {
        super(sawmill);
        super.registerTargets(
                /**
                 * Check if tasks should be executed.
                 */
                new AITarget(IDLE, START_WORKING)
        );
        worker.setSkillModifier(2 * worker.getCitizenData().getEndurance() + worker.getCitizenData().getCharisma());
    }
}
