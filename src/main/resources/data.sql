-- Utilisateurs
INSERT INTO utilisateurs (nom, email, date_creation) VALUES
('Fatou Diallo',    'fatou.diallo@email.com',    NOW()),
('Ibrahima Sow',    'ibrahima.sow@email.com',    NOW()),
('Aissatou Ndiaye', 'aissatou.ndiaye@email.com', NOW()),
('Mamadou Ba',      'mamadou.ba@email.com',      NOW()),
('Rokhaya Diop',    'rokhaya.diop@email.com',    NOW()),
('Cheikh Sarr',     'cheikh.sarr@email.com',     NOW()),
('Ndeye Fall',      'ndeye.fall@email.com',      NOW()),
('Modou Diouf',     'modou.diouf@email.com',     NOW()),
('Coumba Mbaye',    'coumba.mbaye@email.com',    NOW()),
('Lamine Thiam',    'lamine.thiam@email.com',    NOW());

-- Portefeuilles
INSERT INTO portefeuilles (utilisateur_id, solde, devise, date_creation) VALUES
(1, 150000.00, 'XOF', NOW()),
(1,    250.00, 'EUR', NOW()),
(2, 500000.00, 'XOF', NOW()),
(3,   1200.00, 'USD', NOW()),
(4,  75000.00, 'XOF', NOW());

-- Transactions
INSERT INTO transactions (portefeuille_id, type, montant, date_transaction, statut) VALUES
(1, 'DEPOT',   100000.00, NOW(), 'SUCCES'),
(1, 'DEPOT',    50000.00, NOW(), 'SUCCES'),
(2, 'DEPOT',      250.00, NOW(), 'SUCCES'),
(3, 'RETRAIT',   30000.00, NOW(), 'SUCCES'),
(4, 'DEPOT',    75000.00, NOW(), 'SUCCES');