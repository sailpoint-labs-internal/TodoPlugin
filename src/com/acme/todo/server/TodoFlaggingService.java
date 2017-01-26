
package com.acme.todo.server;

import com.acme.todo.service.FlaggedUser;
import com.acme.todo.service.FlaggedUserService;
import com.acme.todo.service.FlaggedUserService.CreateFlagData;
import com.acme.todo.service.TodoService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sailpoint.api.SailPointContext;
import sailpoint.object.Identity;
import sailpoint.server.BasePluginService;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;

import java.util.List;

/**
 * Service executor implementation that periodically checks to see if
 * any users have exceeded the configurable threshold for open todos
 * and if so flags them.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class TodoFlaggingService extends BasePluginService {

    /**
     * The log.
     */
    private static final Log LOG = LogFactory.getLog(TodoFlaggingService.class);

    /**
     * The configurable plugin setting for the maximum open todos.
     */
    private static final String SETTING_MAX_UNTIL_FLAGGED = "maxUntilFlagged";

    /**
     * The configured maximum open todo value.
     */
    private int maxUntilFlagged;

    /**
     * The flagged user service.
     */
    private FlaggedUserService flaggedUserService;

    /**
     * The todo service.
     */
    private TodoService todoService;

    /**
     * Constructor.
     */
    public TodoFlaggingService() {
        todoService = new TodoService(this);
        flaggedUserService = new FlaggedUserService(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName() {
        return "TodoPlugin";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(SailPointContext context) throws GeneralException {
        maxUntilFlagged = getSettingInt(SETTING_MAX_UNTIL_FLAGGED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(SailPointContext context) throws GeneralException {
        pruneFlagged();
        flagUsers(context);
    }

    /**
     * Prunes any flagged users who should no longer be flagged.
     *
     * @throws GeneralException
     */
    public void pruneFlagged() throws GeneralException {
        List<FlaggedUser> flaggedUsers = flaggedUserService.getFlaggedUsers();
        for (FlaggedUser flaggedUser : Util.iterate(flaggedUsers)) {
            int activeTodos = todoService.getActiveTodosForUser(flaggedUser.getUserId());
            if (activeTodos <= maxUntilFlagged) {
                flaggedUserService.pruneFlaggedUser(flaggedUser);
            }
        }
    }

    /**
     * Flags any users that have exceeded the maximum open todo count.
     *
     * @param context The context.
     * @throws GeneralException
     */
    public void flagUsers(SailPointContext context) throws GeneralException {
        if (maxUntilFlagged <= 0) {
            return;
        }

        List<String> userIds = todoService.getUsersWithOpenTodos();
        for (String userId : Util.iterate(userIds)) {
            int numTodos = todoService.getActiveTodosForUser(userId);
            if (numTodos > maxUntilFlagged) {
                String identityName = userId;

                Identity identity = context.getObjectById(Identity.class, userId);
                if (identity != null) {
                    identityName = identity.getDisplayableName();
                }

                if (!flaggedUserService.isUserFlagged(userId)) {
                    CreateFlagData data = new CreateFlagData();
                    data.setId(Util.uuid());
                    data.setUserId(userId);
                    data.setUsername(identityName);
                    data.setNumTodos(numTodos);

                    flaggedUserService.flagUser(data);
                }
            }
        }
    }

}

