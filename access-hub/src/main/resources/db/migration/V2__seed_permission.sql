-- Seed Permissions
INSERT INTO auth.permissions
(permission_name, permission_description, created_at, updated_at, created_by, updated_by, version)
VALUES
-- User Management
('CREATE_USER', 'Allows creating new user accounts.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),
('READ_USER', 'Allows viewing user details and user lists.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(),
 NULL, 0),
('UPDATE_USER', 'Allows updating user information.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),
('DELETE_USER', 'Allows deleting or soft deleting users.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(),
 NULL, 0),

-- Role Management
('CREATE_ROLE', 'Allows creating new roles.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),
('READ_ROLE', 'Allows viewing roles.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),
('UPDATE_ROLE', 'Allows updating role information.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),
('DELETE_ROLE', 'Allows deleting roles.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),

-- Permission Management
('CREATE_PERMISSION', 'Allows creating permissions.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),
('READ_PERMISSION', 'Allows viewing permissions.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),
('UPDATE_PERMISSION', 'Allows updating permissions.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),
('DELETE_PERMISSION', 'Allows deleting permissions.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL, 0),

-- Role Permission Management
('CREATE_ROLE_PERMISSION', 'Allows assigning permissions to roles.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 gen_random_uuid(), NULL, 0),
('DELETE_ROLE_PERMISSION', 'Allows removing permissions from roles.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
 gen_random_uuid(), NULL, 0),

-- User Role Management
('CREATE_USER_ROLE', 'Allows assigning roles to users.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL,
 0),
('DELETE_USER_ROLE', 'Allows removing roles from users.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid(), NULL,
 0);