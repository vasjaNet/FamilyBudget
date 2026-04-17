-- Migration: Add user_role to user_tenant table
-- Adds role_id column to track the role a user has within a tenant

-- Add role_id column to user_tenant table
ALTER TABLE user_tenant
    ADD COLUMN role_id UUID REFERENCES roles(id);

-- Add index for efficient role-based queries
CREATE INDEX idx_user_tenant_role ON user_tenant(role_id);