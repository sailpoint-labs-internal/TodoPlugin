
package com.acme.todo.task;

import com.acme.todo.service.TodoService;
import com.acme.todo.util.TodoUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.TaskResult;
import sailpoint.object.TaskSchedule;
import sailpoint.task.BasePluginTaskExecutor;
import sailpoint.tools.IOUtil;

/**
 * Task executor implementation that removes all completed todos.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class TodoTaskExecutor extends BasePluginTaskExecutor {

    /**
     * Key used to store the number of todos removed in the task result.
     */
    private static final String ATT_NUM_DELETED = "numTodosDeleted";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName() {
        return TodoUtil.PLUGIN_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(SailPointContext context, TaskSchedule schedule,
                        TaskResult result, Attributes<String, Object> args) throws Exception {

        int numDeleted = getTodoService().deleteCompletedTodos();

        result.put(ATT_NUM_DELETED, numDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean terminate() {
        return true;
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

