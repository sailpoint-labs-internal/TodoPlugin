
package com.acme.todo.service;

import com.acme.todo.util.TodoQuery;
import com.acme.todo.util.TodoUtil;

import sailpoint.plugin.PluginBaseHelper;
import sailpoint.plugin.PluginContext;
import sailpoint.tools.GeneralException;
import sailpoint.tools.IOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service containing logic for flagged users.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class FlaggedUserService {

    /**
     * Class containing data needed to flag a user.
     */
    public static class CreateFlagData {

        /**
         * The id.
         */
        private String id;

        /**
         * The user id.
         */
        private String userId;

        /**
         * The username.
         */
        private String username;

        /**
         * The number of open todos.
         */
        private int numTodos;

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
         * Gets the username.
         *
         * @return The username.
         */
        public String getUsername() {
            return username;
        }

        /**
         * Sets the username.
         *
         * @param username The username.
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * Gets the number of open todos.
         *
         * @return The open todos.
         */
        public int getNumTodos() {
            return numTodos;
        }

        /**
         * Sets the number of open todos.
         *
         * @param numTodos The open todos.
         */
        public void setNumTodos(int numTodos) {
            this.numTodos = numTodos;
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
    public FlaggedUserService(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    /**
     * Gets all flagged users.
     *
     * @return The flagged users.
     * @throws GeneralException
     */
    public List<FlaggedUser> getFlaggedUsers() throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            List<FlaggedUser> flaggedUsers = new ArrayList<>();

            connection = pluginContext.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.FLAGGED_USERS);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                flaggedUsers.add(flaggedUserFromResult(resultSet));
            }

            return flaggedUsers;
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Gets the specified flagged user.
     *
     * @param id The id.
     * @return The flagged user.
     * @throws GeneralException
     */
    public FlaggedUser getFlaggedUser(String id) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            FlaggedUser flaggedUser = null;

            connection = pluginContext.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.FLAGGED_USER, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                flaggedUser = flaggedUserFromResult(resultSet);
            }

            return flaggedUser;
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Determines if the specified user has been flagged.
     *
     * @param userId The user id.
     * @return True if flagged, false otherwise.
     * @throws GeneralException
     */
    public boolean isUserFlagged(String userId) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            int count = 0;

            connection = pluginContext.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.IS_USER_FLAGGED, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt("total");
            }

            return count > 0;
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }

    }

    /**
     * Prunes the specified flagged user.
     *
     * @param flaggedUser The flagged user.
     * @throws GeneralException
     */
    public void pruneFlaggedUser(FlaggedUser flaggedUser) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = pluginContext.getConnection();

            statement = PluginBaseHelper.prepareStatement(connection, TodoQuery.PRUNE_FLAGGED_USER, flaggedUser.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Creates a new flagged user using the specified data.
     *
     * @param data The data.
     * @return The flagged user.
     * @throws GeneralException
     */
    public FlaggedUser flagUser(CreateFlagData data) throws GeneralException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = pluginContext.getConnection();

            String id = data.getId();
            String userId = data.getUserId();
            String username = data.getUsername();
            int numTodos = data.getNumTodos();

            statement = PluginBaseHelper.prepareStatement(
                connection, TodoQuery.FLAG_USER, id, userId, username, numTodos, TodoUtil.now()
            );

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GeneralException(e);
        } finally {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }

        return getFlaggedUser(data.getId());
    }

    /**
     * Creates a flagged user from the raw data set.
     *
     * @param resultSet The result set.
     * @return The flagged user.
     * @throws SQLException
     */
    private FlaggedUser flaggedUserFromResult(ResultSet resultSet) throws SQLException {
        FlaggedUser flaggedUser = new FlaggedUser();
        flaggedUser.setId(resultSet.getString("id"));
        flaggedUser.setUserId(resultSet.getString("user_id"));
        flaggedUser.setUsername(resultSet.getString("username"));
        flaggedUser.setNumTodos(resultSet.getInt("num_todos"));
        flaggedUser.setCreated(resultSet.getLong("created"));

        return flaggedUser;
    }

}

