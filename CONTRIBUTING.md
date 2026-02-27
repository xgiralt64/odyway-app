# Estratègia de branques – OdyWay

Aquest document descriu l’estratègia de control de versions utilitzada en  **OdyWay** durant el desenvolupament dels sprints.

## Branques principals

El projecte utilitza dues branques principals:

### main
- Conté únicament versions estables.
- Cada entrega oficial (Release) es crea des d’aquesta branca.
- No s’hi fan commits directes.
- Només es fusiona amb `develop` quan es publica una nova versió.

### develop
- És la branca d’integració.
- Conté totes les funcionalitats completades del sprint en curs.
- És la base per crear noves funcionalitats.
- Ha de mantenir-se sempre funcional i compilable.

---

## Branques de funcionalitat (feature)

Cada nova funcionalitat s’ha de desenvolupar en una branca pròpia creada a partir de `develop`.

### Format del nom

feature/<nom-funcionalitat>

### Exemples

feature/login  
feature/trip-screen  
feature/preferences  
feature/navigation  

---