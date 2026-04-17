-- Migration: Migrate Family module to Tenant structure
-- This migration:
-- 1. Creates family-specific roles in the RBAC system
-- 2. Migrates family records to tenants (type = 'FAMILY')
-- 3. Migrates family_member records to user_tenant with role mapping
-- 4. Drops the legacy family tables

-- ============================================
-- Step 1: Create family-specific roles
-- ============================================

INSERT INTO roles (id, name, description, created_by, updated_by)
VALUES
    (gen_random_uuid(), 'FAMILY_OWNER', 'Full control over family tenant - can manage members and all resources', 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'FAMILY_MEMBER', 'Full access to family resources - can view and edit', 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'FAMILY_VIEWER', 'Read-only access to family resources - view only', 'SYSTEM', 'SYSTEM')
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- Step 2: Migrate family records to tenants
-- ============================================

INSERT INTO tenants (id, name, description, type, created_at, updated_at, created_by, updated_by)
SELECT
    f.id,
    f.name,
    f.description,
    'FAMILY'::VARCHAR(50),
    f.created_at,
    f.updated_at,
    f.created_by,
    f.updated_by
FROM family f
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- Step 3: Migrate family_member records to user_tenant
-- ============================================

-- Map FAMILY_OWNER role (was OWNER)
INSERT INTO user_tenant (id, user_id, tenant_id, role_id, created_at, updated_at, created_by, updated_by)
SELECT
    fm.id,
    fm.user_id,
    fm.family_id AS tenant_id,
    r.id AS role_id,
    fm.created_at,
    fm.created_at AS updated_at,
    fm.created_by,
    'SYSTEM' AS updated_by
FROM family_member fm
JOIN roles r ON r.name = 'FAMILY_OWNER'
WHERE fm.role = 'OWNER'
ON CONFLICT (id) DO NOTHING;

-- Map FAMILY_MEMBER role (was FULL_ACCESS)
INSERT INTO user_tenant (id, user_id, tenant_id, role_id, created_at, updated_at, created_by, updated_by)
SELECT
    fm.id,
    fm.user_id,
    fm.family_id AS tenant_id,
    r.id AS role_id,
    fm.created_at,
    fm.created_at AS updated_at,
    fm.created_by,
    'SYSTEM' AS updated_by
FROM family_member fm
JOIN roles r ON r.name = 'FAMILY_MEMBER'
WHERE fm.role = 'FULL_ACCESS'
ON CONFLICT (id) DO NOTHING;

-- Map FAMILY_VIEWER role (was INFO_ONLY)
INSERT INTO user_tenant (id, user_id, tenant_id, role_id, created_at, updated_at, created_by, updated_by)
SELECT
    fm.id,
    fm.user_id,
    fm.family_id AS tenant_id,
    r.id AS role_id,
    fm.created_at,
    fm.created_at AS updated_at,
    fm.created_by,
    'SYSTEM' AS updated_by
FROM family_member fm
JOIN roles r ON r.name = 'FAMILY_VIEWER'
WHERE fm.role = 'INFO_ONLY'
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- Step 4: Drop legacy family tables
-- ============================================

-- Drop family_member table (has CASCADE from family)
DROP TABLE IF EXISTS family_member CASCADE;

-- Drop family table
DROP TABLE IF EXISTS family CASCADE;

-- Drop indexes (will be dropped with tables, but listed for clarity)
-- DROP INDEX IF EXISTS idx_family_owner;
-- DROP INDEX IF EXISTS idx_family_member_family;
-- DROP INDEX IF EXISTS idx_family_member_user;
-- DROP INDEX IF EXISTS idx_family_member_role;
