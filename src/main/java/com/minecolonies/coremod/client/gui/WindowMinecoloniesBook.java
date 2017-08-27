package com.minecolonies.coremod.client.gui;

import com.minecolonies.api.util.LanguageHandler;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.blockout.Pane;
import com.minecolonies.blockout.controls.Button;
import com.minecolonies.blockout.controls.Label;
import com.minecolonies.blockout.views.ScrollingList;
import com.minecolonies.blockout.views.SwitchView;
import com.minecolonies.coremod.MineColonies;
import com.minecolonies.coremod.network.messages.MinerSetLevelMessage;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Window for the miner hut.
 */
public class WindowMinecoloniesBook extends AbstractWindowSkeleton
{
    private static final String BUTTON_PREVPAGE     = "prevPage";
    private static final String BUTTON_NEXTPAGE     = "nextPage";
    private static final String VIEW_PAGES          = "pages";
    private static final String BOOK_RESOURCE       = ":gui/windowminecoloniesbook.xml";

    private final   Button     buttonPrevPage;
    private final   Button     buttonNextPage;

    /**
     * Constructor for the skeleton class of the windows.
     *
     * @param resource Resource location string.
     */
    public WindowMinecoloniesBook()
    {
        super(Constants.MOD_ID + BOOK_RESOURCE);
        buttonNextPage = findPaneOfTypeByID(BUTTON_NEXTPAGE, Button.class);
        buttonPrevPage = findPaneOfTypeByID(BUTTON_PREVPAGE, Button.class);
    }

    @Override
    public void onButtonClicked(@NotNull final Button button)
    {
        switch (button.getID())
        {
            case BUTTON_PREVPAGE:
                if(findPaneOfTypeByID(VIEW_PAGES, SwitchView.class) != null)
                {
                    findPaneOfTypeByID(VIEW_PAGES, SwitchView.class).previousView();
                }
                break;
            case BUTTON_NEXTPAGE:
                if(findPaneOfTypeByID(VIEW_PAGES, SwitchView.class) != null)
                {
                    findPaneOfTypeByID(VIEW_PAGES, SwitchView.class).nextView();
                }
                break;
            default:
                super.onButtonClicked(button);
                break;
        }
    }
}

