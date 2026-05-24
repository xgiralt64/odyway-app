# Sprint 04 – Execució i Revisió

## 1. Resultats obtinguts

**Comparació amb l'Sprint Goal:**  
L'objectiu de l'Sprint 04 era integrar la llibreria Retrofit per connectar l'aplicació amb una API REST remota, permetent la cerca, reserva, llistat i cancel·lació d'habitacions d'hotel. A més, s'havia d'implementar un sistema de galeria d'imatges per als viatges guardat localment, mantenint l'arquitectura MVVM i Hilt.

**L'objectiu s'ha complert al 100%.**  
S'ha implementat amb èxit la connexió a l'API del professor mitjançant Retrofit, gestionant de forma segura la URL base des del fitxer `build.gradle.kts`. La sincronització entre la base de dades remota (API) i la local (Room) funciona perfectament, reflectint les reserves com a viatges actius.  
S'ha integrat el selector d'imatges natiu d'Android (PhotoPicker) per copiar i persistir fotos a l'emmagatzematge intern de l'app de forma segura, i s'ha complert amb èxit la creació de tests unitaris mockejant l'API.

---

## 2. Tasques completades

| ID | Completada | Comentaris |
|:---|:---:|:---|
| T1.1 | Sí | Retrofit i Coil (per imatges remotes) afegits. Variables globals configurades a `BuildConfig`. |
| T1.2 | Sí | DTOs (Data Transfer Objects) creats amb `@SerializedName` i mapejats als models de Domini. |
| T1.3 | Sí | `HotelRepositoryImpl` creat i injectat amb Hilt per gestionar trucades HTTP de forma asíncrona. |
| T1.4 | Sí | Tests unitaris creats a `HotelRepositoryTest` utilitzant Mockito i Coroutines Test per simular respostes. |
| T2.1 | Sí | Pantalla de cerca implementada amb selecció de dates i ciutat connectada al `checkAvailability`. |
| T2.2 | Sí | Llistat d'hotels i habitacions mostrat correctament amb les seves imatges remotes funcionant. |
| T2.3 | Sí | Funció `reserveRoom` implementada. Desa a l'API remota i automàticament crea un `TripEntity` a Room. |
| T2.4 | Sí | Pantalles de cerca i detalls d'hotel carreguen les imatges de l'API combinant la `baseUrl`. |
| T3.1 | Sí | Implementació de `PickMultipleVisualMedia` (PhotoPicker) per seleccionar diverses fotos alhora. |
| T3.2 | Sí | Les imatges es copien físicament al `context.filesDir` i es desen a Room. |
| T3.3 | Sí | Pestanya de galeria afegida a `TripDetailScreen` amb visualització en graella i opció d'esborrar. |
| T4.1 | Sí | Pantalla de Perfil actualitzada (pestanya **Reserves**) per llistar reserves associades a l'email. |
| T4.2 | Sí | Cancel·lació segura: s'elimina primer de l'API i, si té èxit, del `TripDao` local. |
| T4.3 | Sí | La llista de reserves mostra foto de l'hotel, dates, preu i detalls estèticament. |
| T4.4 | Sí | Etiqueta visual dinàmica **Hotel Inclòs** afegida a les targetes de *My Trips*. |
| T5.1 | Sí | Vídeo demostratiu generat mostrant el flux complet, galeria i cancel·lació. |
| T5.2 | Sí | Release v4.x.x generada. |

---

## 3. Resultats i Documentació

### 1. Connexió API i Bones Pràctiques
S'ha evitat l'ús de dades de connexió *hardcodejades*. L'URL de l'API i el Group ID es gestionen de forma centralitzada mitjançant `buildConfigField` al Gradle, facilitant futurs canvis d'entorn.

### 2. Gestió de Galeria amb PhotoPicker (T3)
S'ha evitat l'error comú de guardar només la URI temporal del sistema (revocable per Android).  
Ara, el `TripViewModel` llegeix els bytes de la imatge seleccionada i els copia a un arxiu privat dins l'app, garantint persistència després de tancar l'aplicació.

### 3. Sincronització Local–Remota Segura (T4.2)
Flux de cancel·lació segur:
1. L'usuari confirma l'eliminació.  
2. Retrofit notifica l'API remota.  
3. Només amb HTTP **200 OK**, s'elimina el viatge a Room.  

Això evita inconsistències i dades fantasma.

---

## 4. Desviacions

### Problema: Xoc de formats JSON a la llista de Reserves
Retrofit fallava en descarregar les reserves.
- **Causa:** L'app esperava una llista directa (`[ {...} ]`), però l'API retornava un objecte amb la llista a dins (`{ "reservations": [ {...} ] }`).
- **Solució:** Creació d'un DTO empaquetador (`ReservationListDto`) per extreure la llista interna correctament.

### Problema: Talls visuals a la Interfície (UI Clipping)

En complir la tasca **T4.4** i afegir l'etiqueta visual **"Hotel Inclòs"** a la targeta dels viatges, l'espai fix de la targeta provocava que els textos de **data** i **preu** desapareguessin de la pantalla quan aquests eren massa llargs.

### Causa i Solució

El problema es va solucionar aplicant diverses millores de disseny:

- Increment de l'altura de la targeta a **160.dp**.
- Limitació dels textos a un màxim de **2 línies** amb `TextOverflow.Ellipsis`.
- Aplicació de `Modifier.weight(1f)` al camp de **data** perquè s'adaptés a l'espai restant sense empènyer el **preu** fora dels límits visuals.

---

## 4. Retrospectiva

### Què ha funcionat bé

#### L'arquitectura establerta a l'Sprint 03
Tenir **Hilt** ja configurat ha permès injectar **Retrofit** i els nous repositoris de manera ràpida, sense haver de refer codi de fases anteriors.

#### El PhotoPicker d'Android
Utilitzar la interfície moderna del sistema per seleccionar imatges ha millorat notablement l'experiència d'usuari (UX), permetent escollir fotos tant locals com del núvol (Google Photos) de forma senzilla.

### Què no ha funcionat

#### El maneig inicial dels JSON
Confiar en estructures de dades deduïdes sense inspeccionar directament la resposta real de l'API va provocar pèrdua de temps en tasques de depuració a la capa de xarxa.

---

## 5. Autoavaluació de l'equip (0–10)

**Nota:** 10 / 10

### Justificació

Hem superat amb èxit la complexitat d'integrar una capa de xarxa asíncrona real connectada a un sistema de persistència local.  
Totes les funcionalitats sol·licitades s'han implementat mantenint una UI atractiva, neta i professional.

A més, s'han realitzat ajustos proactius per garantir el compliment rigorós del 100% de la rúbrica i evitar qualsevol tipus de penalització.
