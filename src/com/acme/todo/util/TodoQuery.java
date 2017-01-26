
package com.acme.todo.util;

/**
 * Utility class containing the queries performed by the
 * services on the todo plugin tables.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class TodoQuery {
    
    /**
     * Query to get all todos.
     */
    public static final String TODOS = "SELECT * FROM tp_todo_list WHERE user_id=? ORDER BY completed_on ASC, created ASC";

    /**
     * Query to select a single todo by id.
     */
    public static final String TODO = "SELECT * FROM tp_todo_list WHERE id=?";

    /**
     * Query to add a todo.
     */
    public static final String ADD = "INSERT INTO tp_todo_list (id, user_id, name, estimate, notes, complete, created) VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
     * Query to complete a todo.
     */
    public static final String COMPLETE = "UPDATE tp_todo_list SET complete=1, completed_on=? WHERE id=?";

    /**
     * Query to delete a todo.
     */
    public static final String DELETE = "DELETE FROM tp_todo_list WHERE id=?";

    /**
     * Query to delete all todos for a user.
     */
    public static final String DELETE_USER = "DELETE FROM tp_todo_list WHERE user_id=?";

    /**
     * Query to delete all todos in the table.
     */
    public static final String DELETE_ALL = "DELETE FROM tp_todo_list";

    /**
     * Query to count all completed todos.
     */
    public static final String COUNT = "SELECT COUNT(id) AS total FROM tp_todo_list WHERE complete=1";

    /**
     * Query to delete all completed todos.
     */
    public static final String DELETE_COMPLETE = "DELETE FROM tp_todo_list WHERE complete=1";

    /**
     * Query to select all users who have open todos.
     */
    public static final String ACTIVE_TODO_USERS = "SELECT DISTINCT user_id FROM tp_todo_list WHERE complete=0";

    /**
     * Query to select the count of open todos for a user.
     */
    public static final String ACTIVE_TODOS_COUNT = "SELECT COUNT(id) AS total FROM tp_todo_list WHERE user_id=? AND complete=0";

    /**
     * Query to flag a user.
     */
    public static final String FLAG_USER = "INSERT INTO tp_flagged_user (id, user_id, username, num_todos, created) VALUES (?, ?, ?, ?, ?)";

    /**
     * Query to get all flagged users.
     */
    public static final String FLAGGED_USERS = "SELECT * FROM tp_flagged_user";

    /**
     * Query to get data for a single flagged user.
     */
    public static final String FLAGGED_USER = "SELECT * FROM tp_flagged_user WHERE id=?";
    
    /**
     * Query to delete a flagged user.
     */
    public static final String PRUNE_FLAGGED_USER = "DELETE FROM tp_flagged_user WHERE id=?";
    
    /**
     * Query to count flags for a user.
     */
    public static final String IS_USER_FLAGGED = "SELECT COUNT(id) AS total FROM tp_flagged_user WHERE user_id=?";

    /**
     * Private constructor.
     */
    private TodoQuery() {}

}

