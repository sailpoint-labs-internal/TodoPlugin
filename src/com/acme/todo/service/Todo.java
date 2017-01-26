
package com.acme.todo.service;

/**
 * Class containing data for a todo.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class Todo {

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
     * The completion flag.
     */
    private boolean complete;

    /**
     * The created timestamp.
     */
    private long created;

    /**
     * The completed on timestamp.
     */
    private long completedOn;

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

    /**
     * Determines if the todo has been completed.
     *
     * @return True if complete, false otherwise.
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Sets the completion flag.
     *
     * @param complete True if complete, false otherwise.
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
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

    /**
     * Gets the completed on timestamp.
     *
     * @return The timestamp.
     */
    public long getCompletedOn() {
        return completedOn;
    }

    /**
     * Sets the completed on timestamp.
     *
     * @param completedOn The timestamp.
     */
    public void setCompletedOn(long completedOn) {
        this.completedOn = completedOn;
    }

}
    
