# Sprint 03 – Execució i Revisió

## 1. Resultats obtinguts

**Comparació amb l'Sprint Goal:**
L'objectiu de l'Sprint 03 era substituir l'emmagatzematge en memòria (In-Memory) per una persistència de dades real utilitzant SQLite (Room Database), integrar Firebase Authentication, configurar Hilt per a la Injecció de Dependències (DI), i implementar una lògica sòlida de validació i proves.

**L'objectiu s'ha complert al 100%.**
S'ha implementat amb èxit una base de dades local robusta amb Room, incloent-hi relacions de Clau Forana (Foreign Keys) entre Usuaris, Viatges i Itineraris, i una taula de registres d'accés (AccessLog). L'autenticació amb Firebase està completament operativa, exigint la verificació de l'email abans de permetre l'accés. L'arquitectura s'ha professionalitzat mitjançant Dagger Hilt i fluxos de dades totalment reactius (Flow i flatMapLatest) que han solucionat problemes de sincronització a la UI.

---

## 2. Tasques completades

| ID | Completada | Comentaris |
|:---|:---:|:---|
| T1.1 | Sí | AppDatabase creat correctament definint la versió i les entitats. |
| T1.2 | Sí | Entitats TripEntity, ItineraryItemEntity i UserEntity definides amb els camps requerits pel PDF. |
| T1.3 | Sí | Consultes SQL (@Query, @Insert, @Update) creades als respectius DAOs. |
| T1.4 | Sí | Integració completada. Els Repositoris ara mapegen Entities de Room a models de Domain. |
| T1.5 | Sí | DatabaseModule i RepositoryModule configurats amb @InstallIn(SingletonComponent::class). |
| T1.6 | Sí | TripViewModel actualitzat. S'ha refactoritzat per fer ús de fluxos reactius per evitar crides síncrones. |
| T2.1 | Sí | FirebaseAuth connectat i injectat via FirebaseModule. |
| T2.2 | Sí | LoginScreen dissenyada i funcional amb navegació dinàmica. |
| T2.3 | Sí | Connexió a Firebase implementada, comprovant l'estat de isEmailVerified per permetre l'entrada. |
| T2.4 | Sí | Funcionalitat de Logout que tanca la sessió a Firebase, esborra l'estat reactiu i registra l'acció a Room. |
| T3.1 | Sí | RegisterScreen dissenyada amb camps per email, password, username i fullName. |
| T3.2 | Sí | Registre complet: Crea l'usuari a Firebase, l'insereix a Room, envia email de verificació i tanca sessió automàticament. |
| T3.3 | Sí | RecoverPasswordScreen creada i enllaçada al mètode sendPasswordResetEmail de Firebase. |
| T4.1 | Sí | UserEntity ampliada per complir la rúbrica (login, address, country, phone, acceptEmails, etc.). |
| T4.2 | Sí | Clau forana establerta. TripEntity ara requereix un userId obligatori. |
| T4.3 | Sí | Fitxer design.md actualitzat amb les entitats, relacions i arquitectura Hilt/Firebase. |
| T4.4 | Sí | Taula AccessLog creada. Enregistra automàticament events LOGIN, REGISTER_LOGIN i LOGOUT amb timestamps. |
| T5.1 | Sí | Tests d'instrumentació (androidTest) construïts per a TripDao utilitzant bases de dades en memòria. |
| T5.2 | Sí | Validacions afegides per prevenir viatges duplicats (COLLATE NOCASE) i noms d'usuari ja existents. |
| T5.3 | Sí | Ús extensiu de Log.d, Log.i i Log.e a tots els repositoris i ViewModels. |
| T6.1 | Sí | Vídeo demostratiu generat ensenyant el flux sencer i les restriccions d'autenticació. |
| T6.2 | Sí | Redacció d'aquest document completada i release final generada. |

---

## Resultats i Documentació

### 1. Persistència i Flux Reactiu
S'ha eliminat completament el FakeDataSource. Ara l'aplicació connecta la UI amb Room Database mitjançant StateFlow. S'ha aplicat el patró `flatMapLatest` tant per recuperar els viatges de l'usuari actiu com per carregar itineraris dinàmics (_currentTripId), solucionant problemes de llistes buides i asincronia.

### 2. Resultats del Testing de Base de Dades (T5.1)
S'han creat tests instrumentats (`TripDaoTest.kt`) corrent sobre l'emulador. Per validar correctament les insercions, es va resoldre la restricció de la clau forana (*FOREIGN KEY constraint failed*) injectant un `UserEntity` fals abans de provar el DAO de viatges, garantint que els tests reflecteixin el comportament real de l'app.

### 3. Autenticació Avançada (T3.2)
S'ha modificat el flux per defecte de Firebase. En lloc de fer auto-login després del registre, l'app força un `signOut()` intern i expulsa l'usuari cap a la pantalla de Login, obligant-lo a comprovar la seva safata d'entrada i verificar el seu correu electrònic abans de poder interactuar amb l'aplicació.

---

## 3. Desviacions

### Problema amb la Persistència de Dates complexes a Room (Tipus no suportats)
En intentar compilar l'aplicació i executar els tests unitaris, Room llançava errors fatals indicant que no sabia com emmagatzemar objectes `LocalDate` i `LocalTime` presents a `TripEntity` i `ItineraryItemEntity`.
- **Causa i Solució:** SQLite només suporta tipus de dades primitius. Per solucionar-ho, vam desenvolupar una classe `AppTypeConverters` utilitzant les anotacions `@TypeConverter`. Aquesta solució transforma els objectes `LocalDate` a Long (Epoch days) abans de guardar-los. Es va integrar a l'AppDatabase.

### Bug de Dades Creuades i Pèrdua de Reactivitat als Itineraris
Durant les proves manuals, l'itinerari acabat de crear no apareixia a la llista fins que l'usuari sortia i tornava a entrar a la pantalla. A més, en canviar d'usuari, apareixien "fantasmes" de les dades de l'usuari anterior.
- **Causa i Solució:** El `TripViewModel` utilitzava un collect manual que es desconnectava. Es va solucionar refactoritzant la propietat a un `StateFlow` pur utilitzant `flatMapLatest` sobre l'ID del viatge actual. També es va afegir `clearCurrentItinerary()` al Logout.

---

## 4. Retrospectiva

**Què ha funcionat bé**
* **L'ús de Hilt per Injecció de Dependències:** Separar els mòduls ens ha permès instanciar repositoris complexos amb molt poques línies de codi als ViewModels.
* **Validació descentralitzada:** Deixar que Room s'encarregui dels duplicats (`COLLATE NOCASE`) redueix la lògica del ViewModel i blinda la base de dades.

**Què no ha funcionat**
* **Adaptació al paradigma reactiu pur:** La transició cap a fluxos asíncrons (`Flow`) connectats a la base de dades va ser un coll d'ampolla tècnic inicialment.

**Què millorarem al pròxim sprint**
* Aprofundir en el domini d'operadors avançats de Kotlin Coroutines i Flow (`flatMapLatest`, `combine`).
* Preparar l'arquitectura del Repositori per suportar trucades a APIs externes (Retrofit).

---

## 5. Autoavaluació de l'equip (0-10)

**Nota: 10 / 10**

**Justificació:**
Hem aconseguit substituir tota la capa de dades simulada per una arquitectura professional de persistència (Room + Firebase) complint amb tots els requisits. Hem resolt problemes tècnics complexos de claus foranes, conversions de tipus i cicles de vida, deixant l'aplicació estable i preparada per al món real.