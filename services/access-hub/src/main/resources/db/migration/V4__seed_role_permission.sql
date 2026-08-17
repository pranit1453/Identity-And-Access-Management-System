-- ============================================================
-- SUPERADMIN
-- Full control over everything
-- ============================================================

INSERT INTO auth.role_permissions
(role_id, permission_id, created_at, updated_at, created_by, updated_by, version)
SELECT
    r.role_id,
    p.permission_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    gen_random_uuid(),
    NULL,
    0
FROM auth.roles r
         CROSS JOIN auth.permissions p
WHERE r.role_name = 'SUPERADMIN'
    ON CONFLICT (role_id, permission_id) DO NOTHING;


-- ADMIN
-- Control only over Users and Roles

INSERT INTO auth.role_permissions
(role_id, permission_id, created_at, updated_at, created_by, updated_by, version)
SELECT
    r.role_id,
    p.permission_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    gen_random_uuid(),
    NULL,
    0
FROM auth.roles r
         JOIN auth.permissions p
              ON p.permission_name IN (
                  -- User Management
                                       'READ_USER',
                                       'UPDATE_USER',
                                       'DELETE_USER',

                  -- Role Management
                                       'CREATE_ROLE',
                                       'READ_ROLE',
                                       'UPDATE_ROLE',
                                       'DELETE_ROLE'
                  )
WHERE r.role_name = 'ADMIN'
    ON CONFLICT (role_id, permission_id) DO NOTHING;

-- USER
-- Control only over Users

INSERT INTO auth.role_permissions
(role_id, permission_id, created_at, updated_at, created_by, updated_by, version)
SELECT
    r.role_id,
    p.permission_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    gen_random_uuid(),
    NULL,
    0
FROM auth.roles r
         JOIN auth.permissions p
              ON p.permission_name IN (
                                       'CREATE_USER',
                                       'READ_USER',
                                       'UPDATE_USER',
                                       'DELETE_USER'
                  )
WHERE r.role_name = 'USER'
    ON CONFLICT (role_id, permission_id) DO NOTHING;