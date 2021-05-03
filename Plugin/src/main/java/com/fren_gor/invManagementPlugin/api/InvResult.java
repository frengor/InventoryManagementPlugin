package com.fren_gor.invManagementPlugin.api;

/**
 * Represents the result of an inventory modification.
 */
public enum InvResult {
    /**
     * Inventory has been successfully modified.
     */
    MODIFIED,
    /**
     * There wasn't enough space in the inventory for the items.
     */
    NOT_ENOUGH_SPACE,
    /**
     * Inventory hasn't been modified.<br><br>
     * Reasons for this can be either:<br>
     * <ul>
     *     <li>The inputted item was null.</li>
     *     <li>An {@link Exception} has been thrown. In this case, an error is print on the console.</li>
     * </ul>
     */
    NOT_MODIFIED;
}
