# Sprint 01 – Planning Document

## 1. Sprint Goal

Definir l'objectiu de l'sprint

## 2. Sprint Backlog


| ID    | Tarea                                          | Responsable    | Estimació | Prioridad |
| ------- | ------------------------------------------------ | ---------------- | ------------ | ----------- |
| T1.1  | Crear nom de la APP                            | Jonas          | 1h         | Alta      |
| T1.2  | Crear logo                                     | Xavier i Jonas | 2h         | Mitjana   |
| T1.3  | Definir minSdk i targetSdk                     | Xavier         | 20 min     | Alta      |
| T1.4  | Crear projecte Android amb nom definit         | Xavier         | 20 min     | Alta      |
| T2.1  | Crear repositori GitHub públic                | Xavier         | 10 min     | Alta      |
| T2.3  | Afegir LICENSE                                 | Xavier         | 0.5        | Mitjana   |
| T2.4  | Crear CONTRIBUTING.md (branques)               | Xavier         | 1          | Mitjana   |
| T2.5  | Crear README.md inicial                        | Xavier         | 1          | Alta      |
| T2.7  | Crear design.md (arquitectura MVVM)            | Xavier         | 2          | Alta      |
| T2.8  | Configurar branques (main, develop, feature/*) | Xavier         | 0.5        | Alta      |
| T2.10 | Primera release v0.1.0                         | Xavier         | 0.5        | Alta      |
| T3.1  | Definir model de dades                         | Xavier         | 3          | Alta      |
| T3.5  | Crear pantalles Home                           | Jonas          | 2          | Alta      |
| T3.6  | Crear pantalla Llista Trips                    | Jonas          | 2          | Alta      |
| T3.7  | Crear pantalla Trip Detail                     | Jonas          | 2          | Alta      |
| T3.8  | Crear pantalla Itinerary                       | Jonas          | 2          | Mitjana   |
| T3.9  | Crear pantalla Preferences (UI mock)           | Jonas          | 2          | Alta      |
| T3.10 | Crear pantalla About                           | Jonas          | 1          | Mitjana   |
| T3.11 | Crear pantalla Terms                           | Jonas          | 1          | Mitjana   |
| T3.12 | Implementar Navigation (NavController)         | Jonas          | 2          | Alta      |
| T4.1  | Crear Splash Screen                            | Jonas          | 1          | Mitjana   |
| T4.2  | Integrar logo i tema visual                    | Jonas          | 1.5        | Mitjana   |
| T4.3  | Crear color-palette.md                         | Jonas          | 1          | Mitjana   |
| T4.4  | Escriure plan_sprint01.md                      | Xavier i Jonas | 1          | Alta      |
| T4.5  | Escriure final_sprint01.md                     | Xavier i Jonas | 1          | Alta      |
| T4.6  | Crear Release final v1.0.0                     | Xavier         | 0.5        | Alta      |

---

## 3. Definition of Done (DoD)

### Arquitectura i Backend (Xavier)

- [ ] Model de domini complet implementat a `domain/model`
- [ ] Diagrama del model amb les classes implementades
- [ ] Interfícies `Repository` definides a `domain/repository`
- [ ] Classes amb funcions mínimes implementades i `@TODO` documentats correctament
- [ ] Separació clara entre capes `ui`, `domain` i `data`
- [ ] Cap classe innecessària ni codi al directori arrel (root)

---

### Frontend (Jonas)

- [ ] Pantalla Home creada amb menú de navegació
- [ ] Pantalla de llista de Trips implementada amb dades mock
- [ ] Pantalla Trip Detail implementada
- [ ] Pantalla Itinerary implementada
- [ ] Pantalla Preferences (UI mock) implementada
- [ ] Pantalla About implementada
- [ ] Pantalla Terms & Conditions implementada
- [ ] Navegació funcional entre pantalles amb `NavController`
- [ ] Dades mock visibles a totes les pantalles
- [ ] Splash Screen implementada amb el logo de l’app
- [ ] Paleta de colors definida a `docs/color-palette.md`
- [ ] Paleta aplicada correctament al `theme/`

---

### Control de Versions i Documentació

- [ ] Estructura del repositori segueix exactament l’esquema obligatori
- [ ] Commits descriptius seguint bones pràctiques
- [ ] README actualitzat amb:
  - [ ] Descripció del projecte
  - [ ] Informació de l’equip
  - [ ] Captures de pantalla
  - [ ] Instruccions d’execució
- [X] `LICENSE` present al root
- [ ] `CONTRIBUTING.md` amb estratègia de branques
- [ ] `docs/design.md` explicant arquitectura i decisions tècniques
- [ ] `docs/plan_sprint01.md` complet
- [ ] `docs/final_sprint01.md` complet

---

### Entrega

- [ ] Versió `v0.1.0` creada i publicada
- [ ] Release final `v1.0.0` publicada a GitHub
- [ ] L’aplicació compila sense errors
- [ ] No hi ha carpetes buides
- [ ] No hi ha codi al root

---

## 4. Riscos identificats

- Problemes amb la versió del sdk de Android Studio
- L'emulador de Android consumeix molts recursos
- Falta d'experiència amb Kotlin

---

⚠ Este documento no puede modificarse después del 30% del sprint.
Fecha límite modificación: DD/MM/YYYY
