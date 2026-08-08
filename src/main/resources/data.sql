INSERT INTO utilisateurs (nom, email, mot_de_passe, role, date_creation) VALUES
('Admin Système', 'admin@email.com',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN',  NOW()),
('Fatou Diallo',  'fatou.diallo@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CLIENT', NOW()),
('Ibrahima Sow',  'ibrahima.sow@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CLIENT', NOW());

INSERT INTO portefeuilles (utilisateur_id, solde, devise, date_creation) VALUES
(2, 150000.00, 'XOF', NOW()),
(3, 500000.00, 'XOF', NOW());

INSERT INTO transactions (portefeuille_id, type, montant, date_transaction, statut) VALUES
(1, 'DEPOT',   100000.00, NOW(), 'SUCCES'),
(1, 'DEPOT',    50000.00, NOW(), 'SUCCES'),
(2, 'DEPOT',      250.00, NOW(), 'SUCCES'),
(3, 'RETRAIT',  30000.00, NOW(), 'SUCCES'),
(4, 'DEPOT',    75000.00, NOW(), 'SUCCES');