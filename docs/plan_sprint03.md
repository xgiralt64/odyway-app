# Sprint 03 – Planning Document

## 1. Sprint Goal
Substituir l'emmagatzematge en memòria (In-Memory) per una persistència de dades real utilitzant SQLite (Room Database) per a viatges i itineraris.  
Integrar Firebase Authentication i configurar Hilt com a llibreria d’Injecció de Dependències (DI).  
Objectius: flux de dades segur, persistència real, autenticació funcional, proves unitàries i registre local d’accessos.

---

## 2. Sprint Backlog

### Taula de Tasques

| ID   | Tarea                                                              | Responsable     | Estimació | Prioritat |
|------|--------------------------------------------------------------------|-----------------|-----------|-----------|
| T1.1 | Crear RoomDatabase                                                 | Xavier          | 1h        | Alta      |
| T1.2 | Definir Entitats Room (Trip, ItineraryItem)                        | Xavier          | 1.5h      | Alta      |
| T1.3 | Crear DAOs                                                         | Xavier          | 1.5h      | Alta      |
| T1.4 | CRUD complet de Trips i Itineraris                                 | Jonas           | 2h        | Alta      |
| T1.5 | Configurar Hilt per injectar RoomDatabase                          | Jonas           | 2.5h      | Alta      |
| T1.6 | Modificar TripViewModel per llegir de Room                         | Jonas           | 2h        | Alta      |
| T2.1 | Connectar Firebase Auth                                            | Jonas           | 1.5h      | Alta      |
| T2.2 | Dissenyar Login UI                                                 | Jonas           | 1.5h      | Alta      |
| T2.3 | Lògica de Login + navegació                                        | Jonas           | 2h        | Alta      |
| T2.4 | Funció Logout                                                      | Jonas           | 0.5h      | Mitjana   |
| T3.1 | Dissenyar Register UI                                              | Jonas           | 1.5h      | Alta      |
| T3.2 | Lògica de Registre + Verificació Email                             | Xavier          | 2.5h      | Alta      |
| T3.3 | UI Recuperació Contrasenya                                         | Xavier          | 1.5h      | Mitjana   |
| T4.1 | Entitat User a Room                                                | Xavier          | 1.5h      | Alta      |
| T4.2 | FK User --> Trip                                                   | Xavier          | 1h        | Alta      |
| T4.3 | Actualitzar design.md amb esquemes                                 | Jonas           | 1h        | Baixa     |
| T4.4 | Taula AccessLog                                                    | Xavier          | 1h        | Mitjana   |
| T5.1 | Unit Tests DAOs                                                    | Xavier          | 2h        | Alta      |
| T5.2 | Validació avançada de dades                                        | Xavier          | 1.5h      | Mitjana   |
| T5.3 | Tracking d'errors al Logcat                                        | Xavier          | 0.5h      | Baixa     |
| T6.1 | Vídeo demostratiu                                                  | Jonas           | 1h        | Alta      |
| T6.2 | final_sprint03.md + Release v3.0.0                                 | Jonas           | 1h        | Alta      |

---

## 3. Definition of Done (DoD)

### Arquitectura i Backend (Xavier)
- [ ] Eliminada la base de dades en memòria.
- [ ] Room operatiu amb Trip, ItineraryItem, User i AccessLog.
- [ ] CRUD via DAOs implementat correctament.
- [ ] Trips filtrats per usuari autenticat de forma automàtica.
- [ ] Repository + Hilt implementats com a font de dades.
- [ ] Firebase Auth funcional (Registre, Login, Verificació email).
- [ ] Registre local d’accessos (Login/Logout) gravat a la base de dades.
- [ ] Unit Tests per DAOs completats i aprovats.

### Frontend i Lògica UI (Jonas)
- [ ] Redirecció automàtica segons estat de la sessió.
- [ ] Pantalles de Login, Registre i Recuperació totalment funcionals.
- [ ] UI reactiva connectada amb Flow o LiveData a la base de dades.
- [ ] Validacions aplicades (noms duplicats, gestió d'errors de Firebase).
- [ ] Botó de Logout integrat de forma intuïtiva a la interfície.

### Control de Versions i Documentació
- [ ] Fitxer design.md actualitzat amb el nou esquema de Room (Room Schema).
- [ ] Fitxer sprints.md actualitzat amb les assignacions d'aquest sprint.
- [ ] Vídeo demostratiu sencer gravat i preparat.
- [ ] Vídeo pujat correctament a la ruta /doc/evidence/v3.0.0.
- [ ] Release v3.0.0 publicada al repositori de GitHub.

---

## 4. Matriu de Riscos i Estratègies de Mitigació

### Hilt (Injeccio de Dependencies)
El Risc: Configurar Dagger/Hilt en projectes Jetpack Compose pot provocar crashes severs a l’inici de l'aplicació si el graf de dependències no està correctament anotat (per exemple, oblidar `@HiltAndroidApp` o `@AndroidEntryPoint`).
Estratègia de Mitigació: Establir la configuració base de Hilt com la tasca número u del projecte. Assegurar que la injecció del `RoomDatabase` arriba correctament al `ViewModel` en un entorn controlat abans de programar cap lògica de negoci addicional.

### Apocalipsi dels Esquemes (Room Migrations)
El Risc: A diferència de les llistes en memòria, afegir noves columnes a les entitats de Room (com User o Trip) un cop la base de dades ja està generada a l'emulador provocarà bloquejos que exigeixen una `Migration`.
Estratègia de Mitigació: Dissenyar i tancar l'esquema relacional complet al document `design.md` abans de fer la primera compilació. En cas de canvis estructurals inevitables durant el desenvolupament, establirem la norma d'esborrar les dades de l'emulador (Wipe Data) per començar amb una base de dades neta sense haver de programar migracions complexes.

### Desfasament Asíncron (Firebase contra Room)
El Risc: L'autenticació de Firebase és asíncrona i depèn de la xarxa, mentre que la creació d'usuaris a Room és local i gairebé immediata. Intentar escriure l'usuari a la base de dades local abans d'obtenir el UID de Firebase pot generar dades corruptes (Condició de Carrera).
Estratègia de Mitigació: Ús estricte de corrutines i funcions de suspensió per forçar que les transaccions de Room esperin sempre el senyal d'èxit de Firebase abans de crear la persistència de l'usuari al dispositiu.
