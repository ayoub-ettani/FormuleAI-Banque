# 🔐 Audit de Sécurité & Performance - Endpoint /api/demandes/recherche

**Date :** 6 août 2026  
**Scope :** DemandeCreditServiceImpl.rechercherDemandes() + GET /api/demandes/recherche

---

## 🚨 PROBLÈMES CRITIQUES

### 1️⃣ **FUITE DE DONNÉES - Pas de contrôle d'accès**

#### ❌ Problème identifié
```java
@GetMapping("/recherche")
public ResponseEntity<List<DemandeCreditResponse>> recherche(...) {
    // PAS DE @PreAuthorize !
    // N'importe quel utilisateur authentifié peut voir TOUTES les demandes
}
```

#### 💥 Impact
- **N'importe quel conseiller peut rechercher les dossiers de tous les autres conseillers**
- **Violation RGPD** : accès non autorisé aux données personnelles des clients
- **Fuite métier** : un conseiller peut voir les statistiques de toute la banque

#### ✅ Recommandation **CRITIQUE**
Ajouter `@PreAuthorize` selon le rôle :

**Option A - Conseillers voient leurs dossiers, managers voient tout :**
```java
@GetMapping("/recherche")
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
public ResponseEntity<List<DemandeCreditResponse>> recherche(
        @RequestParam(required = false) String clientNom,
        @RequestParam(required = false) StatutDemande statut,
        @RequestParam(required = false) Double montantMin,
        @RequestParam(required = false) Double montantMax,
        @Parameter(hidden = true) Authentication authentication) {
    
    // Si ROLE_USER : filtrer par conseiller assigné
    // Si ROLE_MANAGER : voir tout
    return ResponseEntity.ok(demandeCreditService.rechercherDemandes(
        clientNom, statut, montantMin, montantMax, authentication));
}
```

**Option B - Seulement les managers peuvent rechercher :**
```java
@GetMapping("/recherche")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public ResponseEntity<List<DemandeCreditResponse>> recherche(...) {
    // Recherche réservée aux managers
}
```

**MA RECOMMANDATION : Option A** avec filtrage par conseiller dans le service.

---

### 2️⃣ **INJECTION LIKE - Caractères spéciaux non échappés**

#### ⚠️ Problème identifié
```java
// DemandeCreditSpecifications.java ligne 28
return cb.like(cb.lower(clientJoin.get("nom")), "%" + nomClient.toLowerCase() + "%");
```

#### 💥 Impact
- Un utilisateur peut envoyer `clientNom=%` → retourne TOUS les clients
- Un utilisateur peut envoyer `clientNom=_` → correspond à n'importe quel caractère unique
- **Pas d'injection SQL** (JPA protège), mais comportement non intentionnel

#### ✅ Recommandation
Échapper les caractères spéciaux LIKE :
```java
public static Specification<DemandeCredit> clientNomContains(String nomClient) {
    return (root, query, cb) -> {
        if (nomClient == null || nomClient.isBlank()) {
            return cb.conjunction();
        }
        // Échapper les caractères spéciaux LIKE
        String escaped = nomClient.replace("%", "\\%")
                                  .replace("_", "\\_")
                                  .toLowerCase();
        
        Join<DemandeCredit, Client> clientJoin = root.join("client");
        return cb.like(cb.lower(clientJoin.get("nom")), "%" + escaped + "%", '\\');
    };
}
```

---

## ⚠️ PROBLÈMES DE SÉCURITÉ MODÉRÉS

### 3️⃣ **Validation insuffisante des montants**

#### ⚠️ Problème identifié
```java
// Pas de validation pour montants négatifs
if (montantMin != null && montantMax != null && montantMin > montantMax) {
    throw new BusinessException("...");
}
```

#### 💥 Impact
- Un utilisateur peut envoyer `montantMin=-1000000` → recherche invalide
- Résultats potentiellement incohérents

#### ✅ Recommandation
Ajouter validation :
```java
@Override
public List<DemandeCreditResponse> rechercherDemandes(...) {
    // Validation montants négatifs
    if (montantMin != null && montantMin < 0) {
        throw new BusinessException("Le montant minimum doit être positif");
    }
    if (montantMax != null && montantMax < 0) {
        throw new BusinessException("Le montant maximum doit être positif");
    }
    
    // Validation montantMin > montantMax
    if (montantMin != null && montantMax != null && montantMin > montantMax) {
        throw new BusinessException("Le montant minimum ne peut pas être supérieur au montant maximum");
    }
    
    // ... reste du code
}
```

---

### 4️⃣ **Limite de résultats - Pas de pagination**

#### ⚠️ Problème identifié
```java
return demandeCreditRepository.findAll(spec).stream()
        .map(demandeCreditMapper::toResponse)
        .toList();
// Retourne TOUTES les lignes en mémoire !
```

#### 💥 Impact
- **DoS possible** : recherche vide retourne 10 000+ demandes → OutOfMemoryError
- **Performance** : transfert de gros volumes JSON

#### ✅ Recommandation
Ajouter pagination :
```java
// Service
public Page<DemandeCreditResponse> rechercherDemandes(
        String clientNom, StatutDemande statut, 
        Double montantMin, Double montantMax,
        Pageable pageable) {
    
    Specification<DemandeCredit> spec = ...;
    
    Page<DemandeCredit> page = demandeCreditRepository.findAll(spec, pageable);
    return page.map(demandeCreditMapper::toResponse);
}

// Controller
@GetMapping("/recherche")
public ResponseEntity<Page<DemandeCreditResponse>> recherche(
        ...,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by("dateSoumission").descending());
    return ResponseEntity.ok(demandeCreditService.rechercherDemandes(..., pageable));
}
```

---

## 🐌 PROBLÈMES DE PERFORMANCE

### 5️⃣ **Index manquant sur clients.nom**

#### ⚠️ Problème identifié
```java
@Column(nullable = false)
private String nom;  // PAS D'INDEX !

// Requête générée :
// SELECT ... WHERE LOWER(c.nom) LIKE LOWER('%dupont%')
// → FULL TABLE SCAN sur 100 000+ clients !
```

#### 💥 Impact
- Recherche par nom **TRÈS LENTE** sur grandes bases
- `LIKE '%xxx%'` ne peut pas utiliser d'index B-Tree classique

#### ✅ Recommandations (par ordre de priorité)

**Option 1 - Index classique (amélioration partielle) :**
```java
@Entity
@Table(name = "clients", indexes = {
    @Index(name = "idx_client_nom", columnList = "nom")
})
public class Client {
    @Column(nullable = false)
    private String nom;
```
⚠️ Ne fonctionne QUE pour `LIKE 'dupont%'` (pas `LIKE '%dupont%'`)

**Option 2 - Full-Text Search (PostgreSQL) :**
```sql
CREATE INDEX idx_client_nom_fulltext ON clients USING gin(to_tsvector('french', nom));

-- Dans Specification :
WHERE to_tsvector('french', c.nom) @@ plainto_tsquery('french', :nomClient)
```
✅ Très performant pour recherche texte

**Option 3 - Index composite optimisé :**
```java
@Table(name = "clients", indexes = {
    @Index(name = "idx_client_deleted_nom", columnList = "deleted, nom")
})
```
✅ Optimise `WHERE deleted = false AND nom LIKE ...`

**MA RECOMMANDATION : Option 3** (le plus simple et suffisant pour < 100k lignes)

---

### 6️⃣ **Double JOIN sans optimisation**

#### ⚠️ Problème identifié
```java
// 2 Specifications font des JOIN séparés :
.and(DemandeCreditSpecifications.clientNotDeleted())   // JOIN client
.and(DemandeCreditSpecifications.clientNomContains())  // JOIN client (doublon !)
```

#### 💥 Impact
- JOIN dupliqué si JPA n'optimise pas automatiquement
- Requête SQL potentiellement moins performante

#### ✅ Recommandation
Vérifier le SQL généré avec :
```properties
# application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Si JOIN dupliqué, refactorer :
```java
public static Specification<DemandeCredit> clientSpec(String nomClient) {
    return (root, query, cb) -> {
        Join<DemandeCredit, Client> clientJoin = root.join("client");
        
        Predicate clientNotDeleted = cb.equal(clientJoin.get("deleted"), false);
        
        if (nomClient == null || nomClient.isBlank()) {
            return clientNotDeleted;
        }
        
        Predicate nomContains = cb.like(
            cb.lower(clientJoin.get("nom")), 
            "%" + escapeSpecialChars(nomClient) + "%", '\\'
        );
        
        return cb.and(clientNotDeleted, nomContains);
    };
}
```

---

## ✅ POINTS POSITIFS

1. ✅ **Utilisation de JPA Criteria API** → Protection contre injection SQL native
2. ✅ **Validation montantMin > montantMax** → Cohérence métier
3. ✅ **Gestion des nulls** dans les Specifications
4. ✅ **Soft-delete respecté** (demandes ET clients)
5. ✅ **Enum StatutDemande** → Pas d'injection possible

---

## 📋 PLAN D'ACTION PRIORITAIRE

### 🔴 URGENT (À faire MAINTENANT)
1. ✅ Ajouter `@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")` sur `/recherche`
2. ✅ Filtrer par conseiller si `ROLE_USER` (ne voir que ses dossiers)
3. ✅ Échapper les caractères spéciaux LIKE (`%`, `_`)

### 🟠 IMPORTANT (Cette semaine)
4. ✅ Ajouter validation montants négatifs
5. ✅ Ajouter pagination (Page<> + Pageable)
6. ✅ Ajouter index composite `idx_client_deleted_nom`

### 🟡 AMÉLIORATION (Sprint prochain)
7. ✅ Ajouter limite max de résultats (ex: 1000)
8. ✅ Logger les recherches pour audit
9. ✅ Ajouter rate limiting (ex: 10 recherches/minute)

---

## 🎯 RÉPONSE À VOS QUESTIONS

### Q1 : Injection possible via les paramètres ?
**Réponse :** ✅ Protection SQL OK (JPA Criteria), mais ⚠️ LIKE pas échappé

### Q2 : Fuite de données ?
**Réponse :** ❌ **OUI - CRITIQUE** : Pas de `@PreAuthorize`, tout le monde voit tout !

### Q3 : Performance indexée ?
**Réponse :** ❌ **NON** : Aucun index sur `clients.nom`, FULL TABLE SCAN

### Q4 : Gestion valeurs nulles/négatives ?
**Réponse :** ✅ Null OK, ⚠️ Négatifs pas validés

### Q5 : Quel @PreAuthorize ?
**Réponse :** **`@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")`**  
+ Filtrage par conseiller dans le service si ROLE_USER

---

**Score de sécurité actuel : 4/10** ⚠️  
**Score après corrections urgentes : 8/10** ✅

