
package com.acme.todo.policy;

import com.acme.todo.service.TodoService;
import com.acme.todo.util.TodoUtil;

import sailpoint.api.SailPointContext;
import sailpoint.object.Identity;
import sailpoint.object.Policy;
import sailpoint.object.PolicyViolation;
import sailpoint.plugin.PluginsUtil;
import sailpoint.policy.BasePluginPolicyExecutor;
import sailpoint.tools.GeneralException;
import sailpoint.tools.IOUtil;
import sailpoint.tools.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Policy executor implementation that checks to see if any
 * users have over the configurable amount of active todos.
 * If so, generates a policy violation.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class TodoPolicyExecutor extends BasePluginPolicyExecutor {

    /**
     * The max active todos argument key.
     */
    private static final String ARG_MAX_ACTIVE_TODOS = "maxActiveTodos";
    
    /**
     * The key used to store the num active value in the violation.
     */
    private static final String ARG_NUM_ACTIVE = "numActive";

    /**
     * The policy violation renderer.
     */
    private static final String RENDERER = "ui/policy/renderer.xhtml";

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
    public List<PolicyViolation> evaluate(SailPointContext context, Policy policy, Identity id)
        throws GeneralException {

        List<PolicyViolation> violations = new ArrayList<>();

        // dont bother if configured to allow any number
        int maxActiveTodos = policy.getInt(ARG_MAX_ACTIVE_TODOS);
        if (maxActiveTodos <= 0) {
            return violations;
        }

        TodoService todoService = new TodoService(this);

        int numActive = todoService.getActiveTodosForUser(id.getId());
        if (numActive > maxActiveTodos) {
            violations.add(createViolation(context, policy, id, numActive));
        }

        return violations;
    }

    /**
     * Creates a policy violation for the identity.
     *
     * @param context The context.
     * @param policy The policy.
     * @param identity The identity.
     * @param numActive The numer of active todos for the identity.
     * @return The violation.
     */
    private PolicyViolation createViolation(SailPointContext context, Policy policy,
                                            Identity identity, int numActive) {
        PolicyViolation violation = new PolicyViolation();
        violation.setStatus(PolicyViolation.Status.Open);
        violation.setIdentity(identity);
        violation.setPolicy(policy);
        violation.setAlertable(true);
        violation.setOwner(policy.getViolationOwnerForIdentity(context, identity));
        violation.setConstraintName("Maximum active todo threshold exceeded");

        // uncomment when custom renderers are supported for plugin policy executors
        //violation.setRenderer(PluginsUtil.getPluginFileIncludeUrl(TodoService.PLUGIN_NAME, RENDERER));
        violation.setArgument(ARG_NUM_ACTIVE, numActive);

        return formatViolation(context, identity, policy, null, violation);
    }

}

