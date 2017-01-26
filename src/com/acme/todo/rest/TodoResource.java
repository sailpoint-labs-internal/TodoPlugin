
package com.acme.todo.rest;

import com.acme.todo.service.Todo;
import com.acme.todo.service.TodoService;
import com.acme.todo.service.TodoService.CreateTodoData;
import com.acme.todo.util.TodoUtil;

import sailpoint.authorization.AllowAllAuthorizer;
import sailpoint.integration.ListResult;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.AllowAll;
import sailpoint.rest.plugin.Deferred;
import sailpoint.rest.plugin.RequiredRight;
import sailpoint.rest.plugin.SystemAdmin;
import sailpoint.tools.GeneralException;
import sailpoint.tools.Util;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import java.util.List;
import java.util.Map;

/**
 * The REST resource for CRUD operations on todos.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
@Path("TodoPlugin")
@Produces("application/json")
@Consumes("application/json")
public class TodoResource extends BasePluginResource {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName() {
        return TodoUtil.PLUGIN_NAME;
    }

    /**
     * Gets all todos for the currently logged in user.
     *
     * @return The ListResult containing the todos.
     * @throws GeneralException
     */
    @GET
    @Path("todos")
    @AllowAll
    public ListResult getTodos() throws GeneralException {
        TodoService todoService = getTodoService();
        List<Todo> todos = todoService.getTodosForUser(getLoggedInUserId());

        return new ListResult(todos, todos.size());
    }

    /**
     * Gets a todo.
     *
     * @param id The todo id.
     * @return The todo.
     * @throws GeneralException
     */
    @GET
    @Path("todos/{id}")
    @Deferred
    public Todo getTodo(@PathParam("id") String id) throws GeneralException {
        Todo todo = getTodoService().getTodo(id);

        authorize(new TodoAuthorizer(todo));

        return todo;
    }

    /**
     * Adds a todo for the logged in user.
     *
     * @param data The todo data.
     * @return The todo.
     * @throws GeneralException
     */
    @POST
    @Path("todos")
    @AllowAll
    public Todo addTodo(Map<String, String> data) throws GeneralException {
        return getTodoService().createTodo(getCreateTodoData(data));
    }

    /**
     * Completes the specified todo.
     *
     * @param id The todo id.
     * @throws GeneralException
     */
    @POST
    @Path("todos/{id}")
    @Deferred
    public void completeTodo(@PathParam("id") String id) throws GeneralException {
        TodoService todoService = getTodoService();
        Todo todo = todoService.getTodo(id);

        authorize(new TodoAuthorizer(todo));

        todoService.completeTodo(todo);
    }

    /**
     * Deletes the specified todo.
     *
     * @param id The todo id.
     * @throws GeneralException
     */
    @DELETE
    @Path("todos/{id}")
    @Deferred
    public void deleteTodo(@PathParam("id") String id) throws GeneralException {
        TodoService todoService = getTodoService();
        Todo todo = todoService.getTodo(id);

        authorize(new TodoAuthorizer(todo));

        todoService.deleteTodo(todo);
    }

    /**
     * Deletes all todos for a specified user.
     *
     * @param userId The user id.
     * @throws GeneralException
     */
    @DELETE
    @Path("todos/clear/{userId}")
    @RequiredRight("ViewIdentity")
    public void deleteAllUserTodos(@PathParam("userId") String userId) throws GeneralException {
        getTodoService().deleteUserTodos(userId);
    }

    /**
     * Delets all todos in the system.
     *
     * @throws GeneralException
     */
    @DELETE
    @Path("todos")
    @SystemAdmin
    public void deleteAllTodos() throws GeneralException {
        getTodoService().deleteAllTodos(); 
    }

    /**
     * Fills out a CreateTodoData object from the data in the map.
     *
     * @param data The data.
     * @return The todo create data.
     * @throws GeneralException
     */
    private CreateTodoData getCreateTodoData(Map<String, String> data) throws GeneralException {
        String id = Util.uuid();

        String name = data.get("name");
        int estimate = Util.otoi(data.get("time"));
        String notes = data.get("notes");

        CreateTodoData todoData = new CreateTodoData();
        todoData.setId(id);
        todoData.setUserId(getLoggedInUserId());
        todoData.setName(name);
        todoData.setEstimate(estimate);
        todoData.setNotes(notes);

        return todoData;
    }

    /**
     * Gets the id of the currently logged in user.
     *
     * @return The user id.
     * @throws GeneralException
     */
    private String getLoggedInUserId() throws GeneralException {
        return getLoggedInUser().getId();
    }

    /**
     * Gets an instance of the TodoService.
     *
     * @return The service.
     */
    private TodoService getTodoService() {
        return new TodoService(this);
    }

}

