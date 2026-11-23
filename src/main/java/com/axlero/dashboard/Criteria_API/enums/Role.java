package com.axlero.dashboard.Criteria_API.enums;

/**
 * Platform-level role (openapi: Role). It is NOT stored in the database and is not used for workspace
 * permissions; those come from WorkspaceMember.role (WorkspaceRole). Every account is USER for now.
 */
public enum Role {
    USER,
    ADMIN
}