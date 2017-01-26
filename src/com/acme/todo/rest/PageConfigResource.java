
package com.acme.todo.rest;

import com.acme.todo.util.TodoUtil;

import sailpoint.object.Capability;
import sailpoint.object.Identity;
import sailpoint.object.Identity.CapabilityManager;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.AllowAll;
import sailpoint.tools.GeneralException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * The REST resource which exposes the todo list page configuration
 * based on the currently logged in user.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
@Path("TodoPlugin")
@Produces("application/json")
@Consumes("application/json")
public class PageConfigResource extends BasePluginResource {

    /**
     * The name of the right needed to view the flagged users.
     */
    private static final String RIGHT_VIEW_FLAGGED_USERS = "ViewFlaggedUsers";

    /**
     * Plugin configuration key storing whether or not todos can be cleared.
     */
    private static final String SETTING_CAN_DELETE = "canDelete";
    
    /**
     * Class holding the page configuration data.
     */
    private static class PageConfig {

        /**
         * Flag indicating whether or not to show the flagged users button.
         */
        private boolean showFlagged;

        /**
         * Flag indicating whether or not to show the clean button.
         */
        private boolean showClear;

        /**
         * Determines if the flagged users button should be visible.
         *
         * @return True if visible, false otherwise.
         */
        public boolean isShowFlagged() {
            return showFlagged;
        }

        /**
         * Sets whether or not the flagged users button should be visible.
         *
         * @param showFlagged True to show button, false otherwise.
         */
        public void setShowFlagged(boolean showFlagged) {
            this.showFlagged = showFlagged;
        }

        /**
         * Determines if the clear button should be visible.
         *
         * @return True if visible, false otherwise.
         */
        public boolean isShowClear() {
            return showClear;
        }

        /**
         * Sets whether or not the clear button should be visible.
         *
         * @param showClear True to show button, false otherwise.
         */
        public void setShowClear(boolean showClear) {
            this.showClear = showClear;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName() {
        return TodoUtil.PLUGIN_NAME;
    }

    /**
     * Gets the todo list page configuration for the current user.
     *
     * @return The page configuration.
     * @throws GeneralException
     */
    @GET
    @Path("pageConfig")
    @AllowAll
    public PageConfig getPageConfig() throws GeneralException {
        Identity identity = getLoggedInUser();
        if (identity == null) {
            throw new GeneralException("Unable to load identity for logged in user.");
        }
        
        CapabilityManager capabilityManager = identity.getCapabilityManager();
        boolean showFlagged = capabilityManager.hasCapability(Capability.SYSTEM_ADMINISTRATOR) ||
                              capabilityManager.hasRight(RIGHT_VIEW_FLAGGED_USERS);

        PageConfig pageConfig = new PageConfig();
        pageConfig.setShowFlagged(showFlagged);
        pageConfig.setShowClear(isTodoRemovable());

        return pageConfig;
    }

    /**
     * Determines if todos can be cleared based on the plugin
     * configuration value.
     *
     * @return True if removable, false otherwise.
     */
    private boolean isTodoRemovable() {
        return getSettingBool(SETTING_CAN_DELETE);
    }

}

