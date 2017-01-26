
/**
 * Create the module.
 */
var todoModule = angular.module('TodoList', ['ui.bootstrap']);

/**
 * Controller for the todo list.
 */
todoModule.controller('TodoController', function(todoService, pageConfigService, $q, $uibModal) {

    var me = this,
        promises;

    /**
     * @property Array The todos for a user.
     */
    me.todos = [];

    /**
     * @property string The todo name entered by the user.
     */
    me.name = undefined;

    /**
     * @property int The time estimate entired by the user.
     */
    me.time = undefined;

    /**
     * @property string The notes entered by the user.
     */
    me.notes = undefined;

    /**
     * @property Object The page config.
     */
    me.pageConfig = {};

    /**
     * Fetches the todos from the server.
     *
     * @return Promise A promise that resolves with the array of todos.
     */
    function fetchTodos() {
        return todoService.getTodos();
    }

    /**
     * Fetches the page config from the server.
     *
     * @return Promise A promise that resolves with the page config object.
     */
    function fetchPageConfig() {
        return pageConfigService.getPageConfig();
    }

    /**
     * Gets the todos from the server and sets them on the controller.
     */
    me.getTodos = function() {
        fetchTodos().then(function(objects) {
            me.todos = objects;
        });
    };

    /**
     * Adds a new todo using the data entered by the user.
     */
    me.addTodo = function() {
        todoService.addTodo(me.name, me.time, me.notes).then(function(todo) {
            me.getTodos();
            me.reset();
        });
    };

    /**
     * Completes the specified todo.
     *
     * @param Object The todo.
     */
    me.completeTodo = function(todo) {
        todoService.completeTodo(todo.id).then(me.getTodos);
    };

    /**
     * Deletes the specified todo.
     *
     * @param Object The todo.
     */
    me.deleteTodo = function(todo) {
        todoService.deleteTodo(todo.id).then(me.getTodos);
    };

    /**
     * Resets all of the form fields.
     */
    me.reset = function() {
        me.name = undefined;
        me.time = undefined;
        me.notes = undefined;
    };

    /**
     * Shows the notes modal dialog for the specified todo.
     *
     * @param Object The todo.
     */
    me.viewNotes = function(todo) {
        $uibModal.open({
            animation: false,
            controller: 'TodoNotesCtrl as ctrl',
            templateUrl: PluginHelper.getPluginFileUrl('TodoPlugin', 'ui/html/modal-template.html'),
            resolve: {
                notes: function() {
                    return todo.notes;
                }
            }
        });
    };

    /**
     * Shows the flagged users modal dialog.
     */
    me.viewFlaggedUsers = function() {
        $uibModal.open({
            animation: false,
            controller: 'FlaggedUsersCtrl as ctrl',
            templateUrl: PluginHelper.getPluginFileUrl('TodoPlugin', 'ui/html/flagged-template.html')
        });
    };

    promises = {
        todos: fetchTodos(),
        pageConfig: fetchPageConfig()
    };

    // load the page config and the todos
    $q.all(promises).then(function(result) {
        me.todos = result.todos;
        me.pageConfig = result.pageConfig;
    });

});

/**
 * The controller for the notes modal dialog.
 */
todoModule.controller('TodoNotesCtrl', function($uibModalInstance, notes) {

    var me = this;

    /**
     * @property string The notes to display.
     */
    me.notes = notes;

    /**
     * Closes the dialog.
     */
    me.close = function() {
        $uibModalInstance.close();
    };

});

/**
 * The controller for the flagged users modal dialog.
 */
todoModule.controller('FlaggedUsersCtrl', function($uibModalInstance, flaggedUserService) {

    var me = this;

    /**
     * @property Array The flagged users.
     */
    me.flaggedUsers = [];

    /**
     * Closes the dialog.
     */
    me.close = function() {
        $uibModalInstance.close();
    };

    // load the flagged users
    flaggedUserService.getFlaggedUsers().then(function(flaggedUsers) {
        me.flaggedUsers = flaggedUsers;
    });

});

/**
 * Service that handles functionality around todos.
 */
todoModule.service('todoService', function($http) {

    var config = {
        headers: {
            'X-XSRF-TOKEN': PluginHelper.getCsrfToken()
        }
    };

    return {

        /**
         * Gets todos for the logged in user.
         *
         * @return Promise A promise that resolves with an array of todos.
         */
        getTodos: function() {
            var TODOS_URL = PluginHelper.getPluginRestUrl('TodoPlugin/todos');

            return $http.get(TODOS_URL, config).then(function(response) {
                return response.data.objects;
            });
        },

        /**
         * Gets the specified todo.
         *
         * @param string The todo id.
         * @return Promise A promise that resolves with a todo.
         */
        getTodo: function(id) {
            var TODO_URL = PluginHelper.getPluginRestUrl('TodoPlugin/todos/' + id);

            return $http.get(TODO_URL, config).then(function(response) {
                return response.data;
            });
        },

        /**
         * Creates a new todo.
         *
         * @param string The name.
         * @param string The estimate.
         * @param string The notes.
         * @return Promise A promise that resolves with a todo.
         */
        addTodo: function(name, time, notes) {
            var TODOS_URL = PluginHelper.getPluginRestUrl('TodoPlugin/todos'),
                payload = {
                    name: name,
                    time: time,
                    notes: notes
                };

            return $http.post(TODOS_URL, payload, config).then(function(response) {
                return response.data;
            });
        },

        /**
         * Marks the specified todo as complete.
         *
         * @param string The todo id.
         * @return Promise A promise that resolves when successful.
         */
        completeTodo: function(id) {
            var TODO_URL = PluginHelper.getPluginRestUrl('TodoPlugin/todos/' + id);

            return $http.post(TODO_URL, null, config).then(function(result) {
                return {};
            });
        },
        
        /**
         * Deletes the specified todo.
         *
         * @param string The todo id.
         * @return Promsise A promise that resolves when successful.
         */
        deleteTodo: function(id) {
            var TODO_URL = PluginHelper.getPluginRestUrl('TodoPlugin/todos/' + id);

            return $http.delete(TODO_URL, config).then(function(result) {
                return {};
            });
        }

    };

});

/**
 * Service that handles functionality around flagged users.
 */
todoModule.service('flaggedUserService', function($http) {

    var config = {
        headers: {
            'X-XSRF-TOKEN': PluginHelper.getCsrfToken()
        }
    };

    return {

        /**
         * Gets all flagged users.
         *
         * @return Promise A promise resolving with an array of flagged users.
         */
        getFlaggedUsers: function() {
            var FLAGGED_USERS_URL = PluginHelper.getPluginRestUrl('TodoPlugin/flaggedUsers');
            
            return $http.get(FLAGGED_USERS_URL, config).then(function(response) {
                return response.data.objects;
            });
        }

    };

});

/**
 * Services that handles functionality around the page configuration.
 */
todoModule.service('pageConfigService', function($http) {

    var config = {
        headers: {
            'X-XSRF-TOKEN': PluginHelper.getCsrfToken()
        }
    };

    return {

        /**
         * Gets the page configuration.
         *
         * @return Promise A promise resolving with a page configuration object.
         */
        getPageConfig: function() {
            var PAGE_CONFIG_URL = PluginHelper.getPluginRestUrl('TodoPlugin/pageConfig');
            
            return $http.get(PAGE_CONFIG_URL, config).then(function(response) {
                return response.data;
            });
        }

    };

});

