
package com.acme.todo.service;

/**
 * Class containing data for a flagged user.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class FlaggedUser {

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
     * The number of active todos.
     */
    private int numTodos;

    /**
     * The created date.
     */
    private long created;

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
     * @return The number of todos.
     */
    public int getNumTodos() {
        return numTodos;
    }

    /**
     * Sets the number of open todos.
     *
     * @param numTodos The number of todos.
     */
    public void setNumTodos(int numTodos) {
        this.numTodos = numTodos;
    }

    /**
     * Gets the created timestamp.
     *
     * @return The timestamp.
     */
    public long getCreated() {
        return created;
    }

    /**
     * Sets the created timestamp.
     *
     * @param created The timestamp.
     */
    public void setCreated(long created) {
        this.created = created;
    }

}

