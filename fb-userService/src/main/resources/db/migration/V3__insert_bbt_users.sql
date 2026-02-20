CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (id, username, email, first_name, last_name, created_by, updated_by)
VALUES
  (gen_random_uuid(), 'sheldon', 'sheldon.cooper@bbt.example', 'Sheldon', 'Cooper', 'SYSTEM', 'SYSTEM'),
  (gen_random_uuid(), 'leonard', 'leonard.hofstadter@bbt.example', 'Leonard', 'Hofstadter', 'SYSTEM', 'SYSTEM'),
  (gen_random_uuid(), 'penny', 'penny.hofstadter@bbt.example', 'Penny', 'Hofstadter', 'SYSTEM', 'SYSTEM'),
  (gen_random_uuid(), 'howard', 'howard.wolowitz@bbt.example', 'Howard', 'Wolowitz', 'SYSTEM', 'SYSTEM'),
  (gen_random_uuid(), 'raj', 'rajesh.koothrappali@bbt.example', 'Rajesh', 'Koothrappali', 'SYSTEM', 'SYSTEM'),
  (gen_random_uuid(), 'bernadette', 'bernadette.wolowitz@bbt.example', 'Bernadette', 'Rostenkowski-Wolowitz', 'SYSTEM', 'SYSTEM'),
  (gen_random_uuid(), 'amy', 'amy.fowler@bbt.example', 'Amy', 'Farrah Fowler', 'SYSTEM', 'SYSTEM'),
  (gen_random_uuid(), 'stuart', 'stuart.bloom@bbt.example', 'Stuart', 'Bloom', 'SYSTEM', 'SYSTEM')
ON CONFLICT (username) DO NOTHING;
