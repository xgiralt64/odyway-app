# Sprint 02 – Planning Document

## 1. Sprint Goal

Implementar la lògica principal de l'aplicació *OdyWay* mitjançant operacions CRUD en memòria per a viatges i itineraris. També s'assegurarà la validació correcta de les dades, l'emmagatzematge de les preferències de l'usuari, el suport multi-idioma i la creació de proves i documentació seguint estrictament l'arquitectura MVVM.

## 2. Sprint Backlog

| ID   | Tarea                                                              | Responsable    | Estimació | Prioritat |
| ---- | ------------------------------------------------------------------ | -------------- | --------- | --------- |
| T1.1 | Implementar CRUD en memòria per Trips (ViewModel i Repository)     | Xavier         | 3h        | Alta      |
| T1.2 | Implementar CRUD en memòria per Activities/Itinerari               | Xavier         | 3h        | Alta      |
| T1.3 | Implementar DatePickers i validació de dates a la UI               | Jonas          | 2h        | Alta      |
| T1.4 | Implementar persistència de User Settings (SharedPreferences)      | Xavier         | 2h        | Mitjana   |
| T1.5 | Afegir suport multi-idioma (en, ca, es)                            | Jonas          | 1.5h      | Mitjana   |
| T2.1 | Crear fluxos UI per afegir/modificar Trips                         | Jonas          | 2.5h      | Alta      |
| T2.2 | Crear fluxos UI per afegir/modificar Activities                    | Jonas          | 2.5h      | Alta      |
| T2.3 | Connectar UI amb ViewModels per actualització dinàmica             | Xavier i Jonas | 2h        | Alta      |
| T3.1 | Lògica de validació d'errors i missatges clars a la UI             | Xavier i Jonas | 2h        | Alta      |
| T3.2 | Escriure proves unitàries (Unit Tests) per operacions CRUD         | Xavier         | 2.5h      | Mitjana   |
| T3.3 | Afegir logs al Logcat (INFO, DEBUG, ERROR)                         | Xavier         | 1h        | Mitjana   |
| T3.4 | Actualitzar documentació amb resultats dels tests                  | Xavier         | 1h        | Baixa     |
| T4.1 | Gravar vídeo demostratiu mostrant funcionalitats                   | Jonas          | 0.5h      | Alta      |
| T4.2 | Escriure document final_sprint02.md                                | Xavier i Jonas | 1h        | Mitjana   |
| T4.3 | Crear Release final v2.X.X                                         | Xavier         | 0.5h      | Alta      |

---

## 3. Definition of Done (DoD)

### Arquitectura i Backend (Xavier)
- [ ] Operacions CRUD (Add, Edit, Delete) implementades en memòria utilitzant col·leccions (llistes) al `Repository`.
- [ ] L'arquitectura MVVM s'ha respectat rigorosament sense lògica de negoci a la capa UI.
- [ ] Validació de dades al `ViewModel` (camps obligatoris, coherència de dates).
- [ ] Estat del `ViewModel` preservat correctament (sense pèrdua de dades al girar la pantalla o navegar).
- [ ] Preferències d'usuari (username, data de naixement, mode fosc, idioma) guardades i carregades amb `SharedPreferences`.
- [ ] Tests unitaris per al CRUD escrits i validats.
- [ ] Missatges de Logcat implementats correctament (CRUD, errors, validacions).

### Frontend (Jonas)
- [ ] Interacció amb els formularis totalment funcional (Afegir, Editar i Esborrar).
- [ ] Totes les dates s'introdueixen obligatòriament mitjançant components `DatePicker`.
- [ ] És impossible introduir text lliure als camps de dates.
- [ ] Missatges d'error clars visibles a la UI si la validació falla.
- [ ] Actualització dinàmica a les llistes de viatges i itineraris quan es modifica, crea o esborra un element.
- [ ] Les preferències d'idioma i mode fosc es carreguen automàticament a l'inici de l'app.
- [ ] Idiomes Anglès, Català i Castellà configurats i funcionals.

### Control de Versions i Documentació
- [ ] Llistat de tasques assignades detallat a la documentació.
- [ ] Fitxer README / Design actualitzat amb els canvis estructurals introduïts a l'Sprint 02.
- [ ] Documentació de resultats de proves unitàries redactada i integrada.

### Entrega
- [ ] Vídeo demostratiu gravat (amb telèfon o emulador) on es comproven totes les funcions.
- [ ] Vídeo emmagatzemat correctament a la ruta `/doc/evidence/v2.X.X/`.
- [ ] Versió de Release catalogada com a `v2.X.X` a GitHub.

---

## 4. Riscos identificats

- Pèrdua de dades quan es canvia l'orientació del dispositiu o es navega, per un ús incorrecte de l'estat en el `ViewModel`.
- Temps de desenvolupament extra a l'hora de sincronitzar les dades introduïdes entre la UI i les operacions In-Memory del `FakeDataSource`.
- Dificultat gestionant la conversió de formats de data (ex: inputs de la UI vs variables LocalDate del domini) i les validacions creuades (garantir que una Activitat estigui dins del rang de dates del Viatge).

---

⚠ Aquest document s'ha de considerar la base del treball per l'Sprint 02. No es recomana realitzar modificacions estructurals un cop transcorregut el 30% del temps de l'esprint.