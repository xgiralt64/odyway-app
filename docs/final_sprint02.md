# Sprint 02 – Execució i Revisió

## 1. Resultats obtinguts

**Comparació amb l'Sprint Goal:**  
L'objectiu de l'Sprint 02 era implementar la lògica principal de l'aplicació (OdyWay) mitjançant operacions CRUD en memòria, assegurant la validació de dades, l'emmagatzematge de preferències, el suport multi-idioma i la creació de proves sota l'arquitectura MVVM.  
L'objectiu s'ha complert al **100%**.

S'ha implementat una arquitectura neta (Clean Architecture) utilitzant un TripViewModel centralitzat que interactua amb un repositori In-Memory (FakeTripDataSource). S'ha integrat persistència real per a les configuracions mitjançant SharedPreferences i StateFlow. La interfície ara és totalment reactiva, valida regles de negoci complexes (com coherència de dates mitjançant DatePickers natius) i compta amb una suite de proves unitàries (Unit Tests) que garanteixen l'estabilitat del codi.

---

## 2. Tasques completades

| ID   | Completada | Comentaris |
|------|------------|-------------|
| T1.1 | Sí | CRUD implementat amb èxit. S'ha fet ús de StateFlow per reflectir els canvis a la UI a l'instant. |
| T1.2 | Sí | L'itinerari s'agrupa de manera dinàmica per dies a la UI en lloc de forçar entitats de base de dades innecessàries. |
| T1.3 | Sí | Input manual bloquejat. S'han integrat DatePickerDialog de Material 3 garantint la integritat de les dades. |
| T1.4 | Sí | SettingsManager implementat de forma avançada amb OnSharedPreferenceChangeListener per a nom, data, idioma i tema. |
| T1.5 | Sí | Tots els "hardcodes" eliminats. Els idiomes (en, ca, es) s'apliquen de forma reactiva des dels fitxers strings.xml. |
| T2.1 | Sí | Pantalla AddEditTripScreen dissenyada per reaprofitar el mateix formulari tant per a la creació com per a l'edició. |
| T2.2 | Sí | Formulari d'activitats desenvolupat a pantalla completa amb un "carrusel" dinàmic per triar categories amb colors i icones. |
| T2.3 | Sí | Navegació Mestre-Detall fluida passant el paràmetre tripId pel NavGraph sense pèrdua d'estat. |
| T3.1 | Sí | Validació estricta al ViewModel: s'impedeixen dates finals anteriors a les inicials o activitats fora del rang del viatge. |
| T3.2 | Sí | Proves unitàries executades correctament validant les regles de negoci i els errors. |
| T3.3 | Sí | S'ha fet ús de la llibreria MockK als tests per simular interaccions complexes i aïllar el ViewModel. |
| T3.4 | Sí | Document sprints.md i repositori actualitzats amb la informació de les proves i solucions. |
| T3.5 | Sí | S'han afegit registres Log.i, Log.d i Log.e a totes les operacions CRUD i validacions per monitorar el Logcat. |
| T4.1 | Sí | Vídeo demostratiu generat validant totes les funcionalitats exigides a la rúbrica. |
| T4.2 | Sí | Redacció d'aquest document completada. |
| T4.3 | Sí | Etiqueta de la release (v2.0.0) generada a GitHub. |

### Resultados y Documentación (T3.4)

### 1. Arquitectura y Lógica de Negocio
Hemos implementado un flujo **MVVM** estricto utilizando `StateFlow` y Corrutinas para gestionar el estado de la UI (Viajes e Itinerarios). Toda la validación de entrada (T3.1) se ha centralizado en `TripViewModel`, asegurando que no llegue basura al repositorio.

### 2. Resultados del Testing (T3.2 y T3.3)
Se ha implementado una suite de pruebas unitarias (`TripViewModelTest.kt`) simulando interacciones de usuario (mocking de UI calls) y dependencias usando **MockK**:
* **Test Passed:** `addTrip con titulo vacio muestra error en UI y rechaza guardado` -> Garantiza que no se crean viajes corruptos.
* **Test Passed:** `addTrip con fechas invertidas muestra error en UI` -> Comprueba que StartDate <= EndDate.
* **Test Passed:** `addItineraryItem FUERA del rango del viaje muestra error` -> Protege contra actividades fuera de las fechas del viaje (T3.1).
* **Test Passed:** Operaciones CRUD (add, update, delete) verificando llamadas correctas a `TripRepository`.

### 3. Correcciones Aplicadas
* **Problema:** Los botones de "Eliminar" en las listas podían pulsarse por error.
* **Corrección:** Implementamos menús desplegables (`DropdownMenu`) y diálogos de confirmación de borrado (`AlertDialog`) para mejorar la seguridad y la UX.
* **Problema:** Colores ilegibles en "Modo Oscuro" en detalles secundarios.
* **Corrección:** Eliminado el uso de "Color.Gray" hardcodeado, sustituido por referencias semánticas (`MaterialTheme.colorScheme.onSurface`) que se adaptan al `SettingsManager`.

### 4. Logging y Buenas Prácticas (T3.5)
Se ha integrado Android `Log` (niveles `.d`, `.i`, `.e`) en todas las funciones CRUD del ViewModel y se han comentado los bloques funcionales siguiendo buenas prácticas.

---

## 3. Desviacions

**Problema tècnic amb Unit Tests i MockK:**  
Durant el desenvolupament de les proves unitàries (T3.2), la compilació va fallar a causa d'un problema de target de la Màquina Virtual de Java (JVM Target 11 vs 1.8), que impedia utilitzar la llibreria MockK per falsejar la classe Log d'Android.

**Causa i Solució:**  
Es va solucionar ajustant les `compileOptions` i `kotlinOptions` de l'arxiu `build.gradle.kts` per forçar la compatibilitat amb Java 11 (`JavaVersion.VERSION_11`). A més, per evitar el `RuntimeException: Stub!` als tests, es va simular l'acció dels logs utilitzant `mockkStatic(Log::class)`.

---

**Problema visual de contrast en el Mode Fosc:**  
En integrar els formularis i les pantalles de detalls, es va detectar que certs textos i icones havien quedat invisibles en activar el mode fosc, ja que tenien colors estàtics assignats (ex: `Color.Gray` o colors primaris forts sobre fons foscos).

**Solució:**  
Es va aplicar una neteja de codi a la UI, substituint colors estàtics per tokens semàntics adaptatius de Material Design 3 (ex: `MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)` i `secondary`), aconseguint una llegibilitat perfecta en ambdós temes.

---

## 4. Retrospectiva

### Què ha funcionat bé

- **Prevenció de la Sobre-Enginyeria (Over-engineering):** L'equip va decidir utilitzar un únic TripViewModel centralitzat per gestionar tant Viatges com Itineraris. Això ha resultat en un codi molt més net, cohesionat i menys propens a errors d'estat que si s'haguessin creat múltiples repositoris i ViewModels innecessaris per a una estructura In-Memory.

- **Reutilització eficient de la UI:** Fer servir una sola pantalla (AddEditTripScreen) i passar-li un ID opcional per determinar si s'està creant o editant un element ha estalviat molt de temps i codi duplicat (complint amb el principi DRY).

- **Protecció d'accions destructives (UX):** La implementació de menús desplegables i diàlegs de confirmació (AlertDialog) abans de la funció Delete atorga a l'aplicació un aspecte i comportament professional.

### Què no ha funcionat

- Es van deixar temporalment cadenes de text (strings) "hardcodejades" als fitxers Kotlin, cosa que va obligar a fer una refactorització posterior per complir estrictament amb el requisit de suport multi-idioma (T1.5).

### Què millorarem al pròxim sprint

- Extreure tots els textos a `strings.xml` i utilitzar directament els colors de la paleta semàntica `MaterialTheme` des del primer moment de la programació visual per evitar re-treball.

- Preparar mentalment l'equip per a la transició de les dades de memòria volàtil (FakeDataSource) a una base de dades real (Room / Firebase) en els pròxims esprints.

---

## 5. Autoavaluació de l'equip (0-10)

**Nota:** 10 / 10

**Justificació:**  
L'equip ha superat el Sprint 02 amb un grau d'excel·lència notable. No només s'ha complert amb la integritat i validació de dades exigides per la rúbrica, sinó que s'ha cuidat extremadament la qualitat del codi intern (Clean Architecture), les proves unitàries automatitzades i la interfície d'usuari (UX/UI adaptativa). L'aplicació és completament funcional dins dels límits del seu backend simulat.
