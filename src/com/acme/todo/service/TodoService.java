
package com.acme.todo.service;

import com.acme.todo.util.TodoQuery;
import com.acme.todo.util.TodoUtil;

import sailpoint.plugin.PluginBaseHelper;
import sailpoint.plugin.PluginContext;
import sailpoint.tools.GeneralException;
import sailpoint.tools.IOUtil;
import sailpoint.tools.ObjectNotFoundException;
import sailpoint.tools.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service containing logic for todos.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class TodoService {

    /**
     * Plugin configuration setting key for default todo name.
     */
    private static final String SETTING_DEFAULT_NAME = "defaultName";
    
    /**
     * Plugin configuration setting key for default estimate.
     */
    private static final String SETTING_DEFAULT_TIME = "defaultTime";

    /**
     * Class containing data needed to create a todo.
     */
    public static class CreateTodoData {

        /**
         * The id.
         */
        private String id;

        /**
         * The user id.
         */
        private String userId;

        /**
         * The name.
         */
        private String name;
        
        /**
         * The estimate.
         */
        private int estimate;

        /**
         * The notes.
         */
        private String notes;

        /**
         * Gets the id.
         *
         * @return The id.
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the id.
         *
         * @param id The id.
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Gets the user id.
         *
         * @return The user id.
         */
        public String getUserId() {
            return userId;
        }

        /**
         * Sets the user id.
         *
         * @param userId The user id.
         */
        public void setUserId(String userId) {
            this.userId = userId;
        }

        /**
         * Gets the name.
         *
         * @return The name.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name.
         *
         * @param name The name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the estimate.
         *
         * @return The estimate.
         */
        public int getEstimate() {
            return estimate;
        }

        /**
         * Sets the estimate.
         *
         * @param estimate The estimate.
         */
        public void setEstimate(int estimate) {
            this.estimate = estimate;
        }

        /**
         * Gets the notes.
         *
         * @return The notes.
         */
        public String getNotes() {
            return notes;
        }

        /**
         * Sets the notes.
         *
         * @param notes The notes.
         */
        public void setNotes(String notes) {
            this.notes = notes;
        }

    } 

    /**
     * The plugin context.
     */
    private PluginContext pluginContext;

    /**
     * Constructor.
     *
     * @param pluginContext The plugin context.
     */
    public TodoService(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    /**
     * Gets all todos for a user.
     *
     * @param userId The user id.
     * @return The todos.
     * @throws GeneralException
     */
    public List<Todo> getTodosForUser(String userId) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = pluginContext.getConnection();

            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.TODOS, userId);
            ResultSet resultSet = statement.executeQuery();

            List<Todo> todos = new ArrayList<>();
            while (resultSet.next()) {
                todos.add(todoFromResult(resultSet));
            }

            return todos;
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Gets the specified todo.
     *
     * @param todoId The todo id.
     * @return The todo.
     * @throws GeneralException
     */
    public Todo getTodo(String todoId) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = pluginContext.getConnection();

            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.TODO, todoId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return todoFromResult(resultSet);
            } else {
                throw new ObjectNotFoundException();
            }
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Creates a new todo with the specified data.
     *
     * @param data The todo data.
     * @return The todo.
     * @throws GeneralException
     */
    public Todo createTodo(CreateTodoData data) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = pluginContext.getConnection();

            String name = data.getName();
            if (Util.isNullOrEmpty(name)) {
                name = getTodoDefaultName();
            }

            int estimate = data.getEstimate();
            if (estimate <= 0) {
                estimate = getTodoDefaultEstimate();
            }

            statement = PluginBaseHelper.prepareStatement(
                connection, TodoQuery.ADD, data.getId(), data.getUserId(),
                name, estimate, data.getNotes(), false, TodoUtil.now()
            );

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }

        return getTodo(data.getId());
    }

    /**
     * Completes the specified todo.
     *
     * @param todo The todo.
     * @throws GeneralException
     */
    public void completeTodo(Todo todo) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = pluginContext.getConnection();

            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.COMPLETE, TodoUtil.now(), todo.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Deletes the specified todo.
     *
     * @param todo The todo.
     * @throws GeneralException
     */
    public void deleteTodo(Todo todo) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = pluginContext.getConnection();

            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.DELETE, todo.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Deletes all todos for a user.
     *
     * @param userId The user id.
     * @throws GeneralException
     */
    public void deleteUserTodos(String userId) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = pluginContext.getConnection();

            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.DELETE_USER, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Deletes all todos in the system.
     *
     * @throws GeneralException
     */
    public void deleteAllTodos() throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = pluginContext.getConnection();

            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.DELETE_ALL);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Deletes all completed todos in the system.
     *
     * @return The number of todos deleted.
     * @throws GeneralException
     */
    public int deleteCompletedTodos() throws GeneralException {
        Connection connection = null;

        try {
            connection = pluginContext.getConnection();

            int numComplete = countCompletedTodos(connection);
            if (numComplete > 0) {
                deleteCompletedTodos(connection);
            }

            return numComplete;
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Gets all user ids which have open todos.
     *
     * @return The user ids.
     * @throws GeneralException
     */
    public List<String> getUsersWithOpenTodos() throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            List<String> userIds = new ArrayList<>();

            connection = pluginContext.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.ACTIVE_TODO_USERS);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userIds.add(resultSet.getString("user_id"));
            }

            return userIds;
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Gets the count of active todos for the specified user.
     *
     * @param userId The user id.
     * @return The count.
     * @throws GeneralException
     */
    public int getActiveTodosForUser(String userId) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            int numActive = 0;

            connection = pluginContext.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.ACTIVE_TODOS_COUNT, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                numActive = resultSet.getInt("total");
            }

            return numActive;
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    } 
    
    /**
     * Count completed todos in the system.
     *
     * @param connection The connection.
     * @return The count.
     * @throws GeneralException
     */
    private int countCompletedTodos(Connection connection) throws SQLException {
        PreparedStatement statement = null;

        try {
            int count = 0;
            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.COUNT);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }

            return count;
        } finally {
            IOUtil.closeQuietly(statement);
        }
    }

    /**
     * Deletes all completed todos.
     *
     * @param connection The connection.
     * @throws GeneralException
     */
    private void deleteCompletedTodos(Connection connection) throws SQLException {
        PreparedStatement statement = null;

        try {
            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.DELETE_COMPLETE);
            statement.executeUpdate();
        } finally {
            IOUtil.closeQuietly(statement);
        }
    }

    /**
     * Creates a Todo object from a raw result set.
     *
     * @param resultSet The result set.
     * @return The todo.
     * @throws SQLException
     */
    private Todo todoFromResult(ResultSet resultSet) throws SQLException {
        Todo todo = new Todo();
        todo.setId(resultSet.getString("id"));
        todo.setUserId(resultSet.getString("user_id"));
        todo.setName(resultSet.getString("name"));
        todo.setEstimate(resultSet.getInt("estimate"));
        todo.setNotes(resultSet.getString("notes"));
        todo.setComplete(resultSet.getBoolean("complete"));
        todo.setCreated(resultSet.getLong("created"));
        todo.setCompletedOn(resultSet.getLong("completed_on"));

        return todo;
    } 
    
    /**
     * Gets the configured default todo name.
     *
     * @return The default name.
     */
    private String getTodoDefaultName() {
        return pluginContext.getSettingString(SETTING_DEFAULT_NAME);
    }

    /**
     * Gets the configured default todo estimate.
     *
     * @return The estimate.
     */
    private int getTodoDefaultEstimate() {
        return pluginContext.getSettingInt(SETTING_DEFAULT_TIME);
    }

}

