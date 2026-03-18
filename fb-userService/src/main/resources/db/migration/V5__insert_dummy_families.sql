-- Insert dummy families and family members

-- Family 1: Cooper Family (owned by Sheldon)
INSERT INTO family (id, name, description, owner_id, created_by, updated_by)
VALUES
  (gen_random_uuid(), 'Cooper Family', 'The Cooper household budget', (SELECT id FROM users WHERE username = 'sheldon'), 'SYSTEM', 'SYSTEM')
ON CONFLICT DO NOTHING;

-- Family 2: Hofstadter Household (owned by Leonard)
INSERT INTO family (id, name, description, owner_id, created_by, updated_by)
VALUES
  (gen_random_uuid(), 'Hofstadter Household', 'Leonard and Penny''s shared expenses', (SELECT id FROM users WHERE username = 'leonard'), 'SYSTEM', 'SYSTEM')
ON CONFLICT DO NOTHING;

-- Family 3: Wolowitz Family (owned by Howard)
INSERT INTO family (id, name, description, owner_id, created_by, updated_by)
VALUES
  (gen_random_uuid(), 'Wolowitz Family', 'Howard and Bernadette family budget', (SELECT id FROM users WHERE username = 'howard'), 'SYSTEM', 'SYSTEM')
ON CONFLICT DO NOTHING;

-- Add members to Cooper Family (Sheldon's family)
-- Sheldon is owner
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'OWNER', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Cooper Family' AND u.username = 'sheldon'
ON CONFLICT DO NOTHING;

-- Add Penny as FULL_ACCESS member
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'FULL_ACCESS', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Cooper Family' AND u.username = 'penny'
ON CONFLICT DO NOTHING;

-- Add Leonard as INFO_ONLY member
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'INFO_ONLY', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Cooper Family' AND u.username = 'leonard'
ON CONFLICT DO NOTHING;

-- Add members to Hofstadter Household (Leonard's family)
-- Leonard is owner
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'OWNER', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Hofstadter Household' AND u.username = 'leonard'
ON CONFLICT DO NOTHING;

-- Add Penny as FULL_ACCESS member
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'FULL_ACCESS', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Hofstadter Household' AND u.username = 'penny'
ON CONFLICT DO NOTHING;

-- Add Sheldon as INFO_ONLY member
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'INFO_ONLY', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Hofstadter Household' AND u.username = 'sheldon'
ON CONFLICT DO NOTHING;

-- Add members to Wolowitz Family (Howard's family)
-- Howard is owner
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'OWNER', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Wolowitz Family' AND u.username = 'howard'
ON CONFLICT DO NOTHING;

-- Add Bernadette as FULL_ACCESS member
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'FULL_ACCESS', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Wolowitz Family' AND u.username = 'bernadette'
ON CONFLICT DO NOTHING;

-- Add Raj as INFO_ONLY member
INSERT INTO family_member (id, family_id, user_id, role, created_by)
SELECT gen_random_uuid(), f.id, u.id, 'INFO_ONLY', 'SYSTEM'
FROM family f, users u
WHERE f.name = 'Wolowitz Family' AND u.username = 'raj'
ON CONFLICT DO NOTHING;