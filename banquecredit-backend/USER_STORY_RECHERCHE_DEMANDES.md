# User Story : Recherche multicritères de demandes de crédit

## 📋 User Story (Format Standard)

**En tant que** conseiller bancaire  
**Je veux** rechercher des demandes de crédit par client, statut et/ou montant  
**Afin de** filtrer rapidement les demandes pertinentes et optimiser mon suivi des dossiers

---

## 🎯 Critères d'acceptation

### AC1 : Recherche par nom de client
```gherkin
Étant donné que je suis un conseiller authentifié
Quand je recherche les demandes avec le nom de client "Dupont"
Alors je reçois uniquement les demandes dont le client porte ce nom
Et les demandes supprimées (deleted=true) ne sont pas incluses
Et les résultats sont triés par date de soumission décroissante
```

### AC2 : Recherche par statut
```gherkin
Étant donné que je suis un conseiller authentifié
Quand je recherche les demandes avec le statut "EN_ANALYSE"
Alors je reçois uniquement les demandes ayant ce statut
Et les demandes supprimées (deleted=true) ne sont pas incluses
Et les statuts valides sont : BROUILLON, SOUMISE, EN_ANALYSE, ACCEPTEE, REFUSEE, ANNULEE
```

### AC3 : Recherche par plage de montant
```gherkin
Étant donné que je suis un conseiller authentifié
Quand je recherche les demandes avec montantMin=5000 et montantMax=20000
Alors je reçois uniquement les demandes dont le montantDemande est entre 5000 et 20000 (inclus)
Et les demandes supprimées (deleted=true) ne sont pas incluses
```

### AC4 : Recherche multicritères combinés
```gherkin
Étant donné que je suis un conseiller authentifié
Quand je recherche avec nom="Martin", statut="SOUMISE" et montantMin=10000
Alors je reçois uniquement les demandes qui satisfont TOUS les critères (opération AND)
Et les demandes supprimées (deleted=true) ne sont pas incluses
```

### AC5 : Recherche sans critère (liste complète)
```gherkin
Étant donné que je suis un conseiller authentifié
Quand je recherche sans fournir aucun filtre
Alors je reçois toutes les demandes non supprimées (deleted=false)
Et les résultats sont triés par date de soumission décroissante
```

### AC6 : Insensibilité à la casse pour le nom de client
```gherkin
Étant donné que je suis un conseiller authentifié
Quand je recherche avec le nom "duPOnt"
Alors je reçois les demandes des clients "Dupont", "DUPONT", "dupont", etc.
```

### AC7 : Recherche partielle sur le nom de client
```gherkin
Étant donné que je suis un conseiller authentifié
Quand je recherche avec le nom "Dup"
Alors je reçois les demandes des clients dont le nom contient "Dup" (ex: "Dupont", "Duparc")
```

---

## ⚠️ Cas limites et règles de gestion

### 🔍 Cas limite 1 : Un seul filtre actif
**Scénario :** Recherche avec uniquement le statut "ACCEPTEE"  
**Comportement attendu :** Retourne toutes les demandes acceptées non supprimées  
**Validation :** Les autres filtres (nom, montant) sont ignorés/null

### 🔗 Cas limite 2 : Plusieurs filtres combinés
**Scénario :** nom="Martin" ET statut="SOUMISE" ET montantMin=5000 ET montantMax=15000  
**Comportement attendu :** Opération AND entre tous les filtres actifs  
**Validation :** Résultat = intersection de tous les critères

### 📭 Cas limite 3 : Aucun résultat trouvé
**Scénario :** Recherche avec des critères ne correspondant à aucune demande  
**Comportement attendu :**  
- Code HTTP 200 OK  
- Corps de réponse : `[]` (liste vide)  
- Pas d'erreur levée  

### 🔤 Cas limite 4 : Nom partiel et insensible à la casse
**Scénario :** Recherche "mar" doit trouver "Martin", "Martel", "MARY", etc.  
**Implémentation technique :**  
```sql
WHERE LOWER(client.nom) LIKE LOWER('%mar%')
```
**Validation :** Utiliser `ILIKE` (PostgreSQL) ou `LOWER()` pour insensibilité à la casse

### 💰 Cas limite 5 : Montant min > Montant max
**Scénario :** montantMin=20000 et montantMax=10000  
**Comportement attendu :**  
- **Option A (recommandée)** : Retourner HTTP 400 Bad Request avec message explicite  
  ```json
  {
    "error": "Le montant minimum ne peut pas être supérieur au montant maximum",
    "montantMin": 20000,
    "montantMax": 10000
  }
  ```
- **Option B** : Inverser automatiquement les valeurs (moins recommandé)

### 🗑️ Cas limite 6 : Compatibilité soft-delete
**Contexte :** L'entité `DemandeCredit` a un champ `deleted` (Boolean)  
**Règle métier :** Les demandes supprimées ne doivent JAMAIS apparaître dans les résultats  
**Implémentation technique :**  
```java
// Dans DemandeCreditRepository
@Query("SELECT d FROM DemandeCredit d WHERE d.deleted = false " +
       "AND (:nom IS NULL OR LOWER(d.client.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) " +
       "AND (:statut IS NULL OR d.statut = :statut) " +
       "AND (:montantMin IS NULL OR d.montantDemande >= :montantMin) " +
       "AND (:montantMax IS NULL OR d.montantDemande <= :montantMax)")
List<DemandeCredit> rechercherDemandes(
    @Param("nom") String nom,
    @Param("statut") StatutDemande statut,
    @Param("montantMin") Double montantMin,
    @Param("montantMax") Double montantMax
);
```

### 🔒 Cas limite 7 : Client soft-deleted mais demande active
**Scénario :** Un client est supprimé (client.deleted=true) mais ses demandes sont encore actives  
**Comportement attendu :**  
- **Option A** : Exclure aussi ces demandes (cascade logique)  
  ```sql
  WHERE d.deleted = false AND d.client.deleted = false
  ```
- **Option B** : Inclure ces demandes (conservation historique)  
**Décision à prendre** : Selon votre règle métier

### 📊 Cas limite 8 : Montant aux limites métier
**Contexte :** Votre validation impose montant entre 1000.01 et 100000  
**Scénarios à tester :**
- montantMin=1000.01 (limite basse valide)
- montantMax=100000 (limite haute valide)
- montantMin=999 (hors limites métier, mais valide pour la recherche)
- Recherche avec montants hors limites métier doit fonctionner (car anciennes demandes peuvent exister)

### 🎭 Cas limite 9 : Statut invalide
**Scénario :** Recherche avec statut="INVALIDE"  
**Comportement attendu :**  
- HTTP 400 Bad Request  
- Message : "Statut invalide. Valeurs acceptées : BROUILLON, SOUMISE, EN_ANALYSE, ACCEPTEE, REFUSEE, ANNULEE"

### 🔢 Cas limite 10 : Valeurs nulles vs vides
**Scénario :** nom="" (chaîne vide) vs nom=null  
**Comportement attendu :**
- `null` : filtre ignoré (ramène tout)
- `""` (vide) : HTTP 400 Bad Request ou traité comme null selon votre choix

---

## 🛠️ Proposition d'implémentation

### 1. DTO de requête (SearchDemandeCreditRequest.java)
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDemandeCreditRequest {
    
    private String nomClient;  // recherche partielle insensible à la casse
    
    private StatutDemande statut;
    
    @DecimalMin(value = "0.0", message = "Le montant minimum doit être positif")
    private Double montantMin;
    
    @DecimalMin(value = "0.0", message = "Le montant maximum doit être positif")
    private Double montantMax;
    
    // Validation custom
    @AssertTrue(message = "Le montant minimum ne peut pas être supérieur au montant maximum")
    public boolean isMontantRangeValid() {
        if (montantMin != null && montantMax != null) {
            return montantMin <= montantMax;
        }
        return true;
    }
}
```

### 2. Méthode dans DemandeCreditRepository
```java
@Query("SELECT d FROM DemandeCredit d " +
       "JOIN FETCH d.client c " +
       "WHERE d.deleted = false " +
       "AND c.deleted = false " +  // Exclure aussi les clients supprimés
       "AND (:nomClient IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :nomClient, '%'))) " +
       "AND (:statut IS NULL OR d.statut = :statut) " +
       "AND (:montantMin IS NULL OR d.montantDemande >= :montantMin) " +
       "AND (:montantMax IS NULL OR d.montantDemande <= :montantMax) " +
       "ORDER BY d.dateSoumission DESC")
List<DemandeCredit> searchDemandes(
    @Param("nomClient") String nomClient,
    @Param("statut") StatutDemande statut,
    @Param("montantMin") Double montantMin,
    @Param("montantMax") Double montantMax
);
```

### 3. Méthode dans DemandeCreditService
```java
List<DemandeCreditResponse> searchDemandes(SearchDemandeCreditRequest request);
```

### 4. Endpoint dans DemandeCreditController
```java
@GetMapping("/search")
public ResponseEntity<List<DemandeCreditResponse>> searchDemandes(
    @Valid @ModelAttribute SearchDemandeCreditRequest request
) {
    return ResponseEntity.ok(demandeCreditService.searchDemandes(request));
}
```

### Exemple d'appel
```http
GET /api/demandes/search?nomClient=Dupont&statut=EN_ANALYSE&montantMin=5000&montantMax=20000
```

---

## 🧪 Scénarios de test à implémenter

### Tests unitaires (DemandeCreditServiceTest)
1. ✅ Recherche avec un seul filtre (nom uniquement)
2. ✅ Recherche avec un seul filtre (statut uniquement)
3. ✅ Recherche avec un seul filtre (plage montant uniquement)
4. ✅ Recherche multicritères (tous les filtres)
5. ✅ Recherche sans critère (tous null)
6. ✅ Recherche retournant aucun résultat
7. ✅ Recherche insensible à la casse
8. ✅ Recherche partielle sur nom client
9. ✅ Validation montantMin > montantMax (erreur attendue)
10. ✅ Exclusion des demandes soft-deleted
11. ✅ Exclusion des demandes avec client soft-deleted

### Tests d'intégration (DemandeCreditControllerTest)
1. ✅ GET /search avec paramètres valides → 200 OK
2. ✅ GET /search sans paramètres → 200 OK (toutes les demandes)
3. ✅ GET /search aucun résultat → 200 OK avec liste vide
4. ✅ GET /search montantMin > montantMax → 400 Bad Request
5. ✅ GET /search statut invalide → 400 Bad Request

---

## 📝 Notes supplémentaires

### Performance
- Ajouter un index sur `(client_id, statut, montant_demande)` pour optimiser les recherches
- Limiter le nombre de résultats (pagination recommandée si > 100 résultats)

### Sécurité
- Vérifier les autorisations : `@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")`
- Logger les recherches pour audit

### Évolutions futures
- Ajout de filtres sur date de soumission (plage de dates)
- Ajout de filtres sur score simplifié
- Recherche sur email du client
- Export des résultats (CSV, Excel)

---

**Date de création :** 5 août 2026  
**Statut :** Prêt pour implémentation  
**Priorité :** Haute  
**Estimation :** 5 story points (2-3 jours)

