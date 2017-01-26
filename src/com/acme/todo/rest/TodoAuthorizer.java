
package com.acme.todo.rest;

import com.acme.todo.service.Todo;

import sailpoint.authorization.Authorizer;
import sailpoint.authorization.UnauthorizedAccessException;
import sailpoint.tools.GeneralException;
import sailpoint.web.UserContext;

/**
 * Authorizer which checks to see if the currently logged in user
 * has access to the specified todo.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
class TodoAuthorizer implements Authorizer {
    
    /**
     * The todo to check.
     */
    private Todo todo;

    /**
     * Constructor.
     *
     * @param todo The todo.
     */
    public TodoAuthorizer(Todo todo) {
        this.todo = todo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void authorize(UserContext userContext) throws GeneralException {
        if (!userContext.getLoggedInUser().getId().equals(todo.getUserId())) {
            throw new UnauthorizedAccessException("User does not have access to the todo");
        }
	}

}

