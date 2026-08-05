-- Migration: Ajout d'index composite sur clients (deleted, nom)
-- Date: 2026-08-06
-- But: Optimiser les recherches par nom de client dans l'endpoint /api/demandes/recherche

-- PostgreSQL
CREATE INDEX IF NOT EXISTS idx_client_deleted_nom ON clients(deleted, nom);

-- Pour vérifier que l'index existe :
-- SELECT indexname, indexdef FROM pg_indexes WHERE tablename = 'clients';

-- MySQL (si vous utilisez MySQL au lieu de PostgreSQL)
-- CREATE INDEX idx_client_deleted_nom ON clients(deleted, nom);

-- Pour analyser la performance de la requête :
-- EXPLAIN ANALYZE
-- SELECT c.* FROM clients c
-- WHERE c.deleted = false
-- AND LOWER(c.nom) LIKE LOWER('%dupont%');

-- Résultat attendu : Index Scan au lieu de Seq Scan

