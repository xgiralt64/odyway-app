# Sprint 04 – Planning Document

## 1. Sprint Goal
Integrar la llibreria Retrofit per connectar l'aplicació amb una API REST remota per a la gestió de reserves d'hotels.  
L'objectiu principal és implementar la cerca, reserva, llistat i cancel·lació d'habitacions, combinant les dades remotes amb la base de dades local (Room). A més, s'ha d'implementar un sistema de gestió de galeries d'imatges per a cada viatge guardades localment. Tot mantenint l'arquitectura MVVM i Hilt com a injecció de dependències.

---

## 2. Sprint Backlog

### Taula de Tasques

| ID   | Tarea                                                              | Responsable     | Estimació | Prioritat |
|------|--------------------------------------------------------------------|-----------------|-----------|-----------|
| T1.1 | Afegir dependències de Retrofit i configurar client HTTP           | Jonas           | 1h        | Alta      |
| T1.2 | Crear models de dades i interfícies API (MVVM)                     | Jonas           | 1.5h      | Alta      |
| T1.3 | Crear capa Repository per abstreure l'ús de l'API remota           | Jonas           | 1.5h      | Alta      |
| T1.4 | Crear Unit Tests mockejant la connexió remota                      | Xavier          | 2h        | Alta      |
| T2.1 | UI Cerca d'hotels (Londres, París, BCN, dates inici/fi)            | Jonas           | 2h        | Alta      |
| T2.2 | Lògica per mostrar hotels i habitacions obtingudes de l'API        | Jonas           | 2h        | Alta      |
| T2.3 | Lògica de reserva i desat local a Room (nova entitat o via Trip)   | Jonas           | 2.5h      | Alta      |
| T2.4 | UI per mostrar imatges d'hotels i habitacions a la pantalla de reserva | Jonas       | 1.5h      | Mitjana   |
| T3.1 | UI per permetre a l'usuari adjuntar múltiples imatges a un viatge  | Xavier          | 1.5h      | Alta      |
| T3.2 | Lògica per desar imatges localment (Storage / Room DB)             | Xavier          | 2h        | Alta      |
| T3.3 | UI Galeria d'imatges específica als detalls del viatge             | Xavier          | 1.5h      | Mitjana   |
| T4.1 | UI Llistat de totes les reserves locals indicant el viatge associat  | Xavier          | 1.5h      | Alta      |
| T4.2 | Funcionalitat per esborrar reserves (Localment + via API si cal)   | Xavier          | 2h        | Alta      |
| T4.3 | Mostrar imatges d'hotels/habitacions a la llista de reserves       | Xavier          | 1h        | Mitjana   |
| T4.4 | Actualitzar "My Trips" per indicar reserves i mostrar els detalls  | Xavier          | 1.5h      | Alta      |
| T5.1 | Gravar vídeo demostratiu amb l'emulador mostrant totes les tasques | Jonas           | 1h        | Alta      |
| T5.2 | Actualitzar design.md/sprints.md i generar Release v4.x.x          | Xavier          | 1h        | Alta      |

---

## 3. Definition of Done (DoD)

### Connexió API i Pantalles de Reserva (Jonas)
- [ ] Retrofit està completament configurat amb l'URL base de l'API proporcionat.
- [ ] Les trucades a l'API es gestionen de forma asíncrona utilitzant el patró Repository i Flow.
- [ ] La pantalla de cerca permet filtrar per ciutat i dates correctament.
- [ ] Es mostren les opcions d'habitacions (mínim 3) amb les seves imatges obtingudes de la xarxa.
- [ ] En fer una reserva, les dades es guarden correctament a la base de dades local (Room).
- [ ] La injecció de dependències es continua gestionant amb Hilt i l'arquitectura MVVM.

### Imatges, Llistats i Testing (Xavier)
- [ ] El sistema permet escollir diverses imatges del dispositiu i associar-les a un viatge.
- [ ] Les imatges dels viatges es guarden de forma persistent (Device Storage / URIs a Room).
- [ ] La galeria del viatge es mostra de forma fluida a la pantalla de detalls.
- [ ] Existeix una pantalla per llistar totes les reserves d'hotels fetes.
- [ ] Es poden cancel·lar reserves, eliminant-les tant de la base de dades local com enviant la petició a l'API.
- [ ] La pestanya "My Trips" s'ha actualitzat reflectint els estats de les reserves d'hotels.
- [ ] S'han superat els tests unitaris mockejant l'API.

### Control de Versions i Documentació
- [ ] Fitxer `Sprint.md` actualitzat amb el repartiment de tasques.
- [ ] El codi es troba a les carpetes adequades (`view`, `viewmodel`, `repo`, `di`, `data`).
- [ ] Vídeo demostratiu gravat ensenyant el flux de cerca, reserva, galeria i cancel·lació.
- [ ] Vídeo pujat correctament a la ruta `/docs` o `/documentation/evidence/v4.x.x`.
- [ ] Release v4.x.x publicada al repositori de GitHub.

---

## 4. Matriu de Riscos i Estratègies de Mitigació

### Trucades de Xarxa asíncrones (Retrofit)
**El Risc:** L'API pot trigar a respondre, estar caiguda, o el telèfon pot perdre connexió. Si no es gestiona bé, l'app pot bloquejar-se (ANR) o tancar-se inesperadament (Crash).  
**Estratègia de Mitigació:** Utilitzar la classe `Result<T>` o una classe segellada (Sealed Class com `UiState`) als Repositoris per encapsular estats de `Loading`, `Success` i `Error`. Implementar blocs `try-catch` a totes les trucades de xarxa al ViewModel i mostrar missatges amigables (Snackbars) a l'usuari en cas de fallada d'Internet.

### Gestió i Persistència d'Imatges (Galeria)
**El Risc:** Guardar imatges d'alta resolució directament a la base de dades (Room) com a BLOBs col·lapsarà la memòria i reduirà dràsticament el rendiment de l'app.  
**Estratègia de Mitigació:** En lloc de guardar la imatge directament, l'app desarà les imatges a l'emmagatzematge intern del dispositiu (Storage) i únicament guardarà l'identificador (URI/Path) en format de text (`String`) a la base de dades local de Room.

### Sincronització de l'Estat Local vs Remot (Cancel·lacions)
**El Risc:** En cancel·lar una reserva, es pot eliminar de la base de dades local però que la petició a l'API falli, provocant una inconsistència de dades (l'API es pensa que la reserva segueix activa, però l'usuari ja no la veu).  
**Estratègia de Mitigació:** Executar primer la petició HTTP de cancel·lació a través de Retrofit. Només si l'API retorna un codi d'èxit (HTTP 200 OK), el Repositori procedirà a esborrar la reserva de la base de dades local de Room.