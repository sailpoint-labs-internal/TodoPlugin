
package com.acme.todo.rest;

import com.acme.todo.service.FlaggedUser;
import com.acme.todo.service.FlaggedUserService;
import com.acme.todo.util.TodoUtil;

import sailpoint.integration.ListResult;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.RequiredRight;
import sailpoint.tools.GeneralException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.List;

/**
 * The REST resource used for interacting with flagged users.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
@Path("TodoPlugin")
@Produces("application/json")
@Consumes("application/json")
@RequiredRight("ViewFlaggedUsers")
public class FlaggedUserResource extends BasePluginResource {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName() {
        return TodoUtil.PLUGIN_NAME;
    }

    /**
     * Gets all flagged users.
     *
     * @return The ListResult containing flagged users.
     * @throws GeneralException
     */
    @GET
    @Path("flaggedUsers")
    public ListResult getFlaggedUsers() throws GeneralException {
        List<FlaggedUser> flaggedUsers = getFlaggedUserService().getFlaggedUsers();

        return new ListResult(flaggedUsers, flaggedUsers.size());
    }

    /**
     * Gets an instance of the FlaggedUserService.
     *
     * @return The service,
     */
    private FlaggedUserService getFlaggedUserService() {
        return new FlaggedUserService(this);
    }

}

