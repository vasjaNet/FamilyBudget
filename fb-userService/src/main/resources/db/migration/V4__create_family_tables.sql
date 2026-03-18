-- Family Group tables for Family Budget application
-- Created: 2026-03-12

-- Create family table
CREATE TABLE IF NOT EXISTS family (
    id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create family_member table
CREATE TABLE IF NOT EXISTS family_member (
    id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID NOT NULL REFERENCES family(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL CHECK (role IN ('OWNER', 'FULL_ACCESS', 'INFO_ONLY')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    UNIQUE(family_id, user_id)
);

-- Create indexes for faster lookups
CREATE INDEX idx_family_owner ON family(owner_id);
CREATE INDEX idx_family_member_family ON family_member(family_id);
CREATE INDEX idx_family_member_user ON family_member(user_id);
CREATE INDEX idx_family_member_role ON family_member(role);