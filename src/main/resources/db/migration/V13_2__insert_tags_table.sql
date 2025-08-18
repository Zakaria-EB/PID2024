INSERT INTO tags (tag) VALUES
  ('théâtre'),
  ('humour'),
  ('enfants'),
  ('chansons'),
  ('haïti'),
  ('anticipation'),
  ('société');

  -- Ayiti : théâtre, société, haïti
  INSERT INTO show_tag (show_id, tag_id) VALUES
    (1, 1), -- théâtre
    (1, 5), -- haïti
    (1, 7); -- société

  -- Cible mouvante : anticipation, société, enfants
  INSERT INTO show_tag (show_id, tag_id) VALUES
    (2, 3), -- enfants
    (2, 6), -- anticipation
    (2, 7); -- société

  -- Ceci n’est pas un chanteur belge : humour, chansons
  INSERT INTO show_tag (show_id, tag_id) VALUES
    (3, 2), -- humour
    (3, 4); -- chansons

  -- Manneke : humour, enfants
  INSERT INTO show_tag (show_id, tag_id) VALUES
    (4, 2), -- humour
    (4, 3); -- enfants

