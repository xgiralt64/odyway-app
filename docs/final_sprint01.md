# Sprint 01 – Execució i Revisió

## 1. Resultats obtinguts

Comparació amb l’Sprint Goal:  
L’objectiu de l’Sprint 01 era establir l’arquitectura visual base de l’aplicació (OdyWay) i desenvolupar el prototip funcional de la interfície gràfica sense lògica de backend.  
L’objectiu s’ha complert al **100%**.

S’ha implementat un sistema de navegació fluid (NavGraph amb BottomNavigationBar), s’han desenvolupat totes les pantalles principals (Login, Home, Trips, Profile) i secundàries (Settings, Preferences, About, Terms), i s’ha integrat amb èxit un sistema de temes escalable amb suport total per al Mode Clar i Mode Fosc.

---

## 2. Tasques completades

| ID   | Completada | Comentaris |
|------|------------|-------------|
| T1.1 | Sí | Sense problemes |
| T1.2 | Sí | S'ha implementat amb èxit el MaterialTheme complet per suportar Mode Clar i Mode Fosc de forma dinàmica. |
| T1.3 | Sí | Sense problemes |
| T1.4 | Sí | Sense problemes |
| T2.1 | Sí | S'ha configurat la navegació (NavGraph) per amagar la barra inferior automàticament en entrar a pantalles secundàries. |
| T2.2 | Sí | Sense problemes |
| T2.3 | Sí | Sense problemes |
| T2.4 | Sí | Sense problemes |
| T2.5 | Sí | Sense problemes |
| T2.6 | Sí | Sense problemes |
| T2.7 | Sí | Sense problemes |
| T3.1 | Sí | Sense problemes |
| T3.2 | Sí | Ús exitós de LazyColumn i LazyVerticalGrid per mostrar dades 'mock' a Home i Trips. |
| T3.3 | Sí | Sense problemes |
| T3.4 | Sí | Sense problemes |
| T3.5 | Sí | Sense problemes |
| T3.6 | Sí | Sense problemes |
| T3.7 | Sí | Sense problemes |
| T3.8 | Sí | Sense problemes |
| T3.9 | Sí | Sense problemes |
| T4.1 | Sí | Problemes de 'crash' inesperats causats pel component clickable (efecte ripple) i icones esteses. Solucionat usant Surface i icones base (Core). |
| T4.2 | Sí | Sense problemes |
| T4.3 | Sí | Sense problemes |
| T4.4 | Sí | Interfície gràfica (Mock) per als 'Switches' de mode fosc i notificacions creada correctament. |
| T4.5 | Sí | Sense problemes |
| T4.6 | Sí | Sense problemes |

---

## 3. Desviacions

**Problema tècnic amb Jetpack Compose:**  
Durant el desenvolupament de la navegació cap a Settings, l’aplicació patia tancaments inesperats (crashes).

**Causa i Solució:**  
Es va identificar un conflicte de compatibilitat amb `Modifier.clickable` i l’efecte visual d’ona (Ripple) en la versió utilitzada de Material 3, a més de la manca de la llibreria d’icones esteses.  
Es va solucionar ràpidament substituint el modificador pel component natiu **Surface** i utilitzant les icones bàsiques (Core), evitant haver de modificar dependències a Gradle i perdre temps.

---

## 4. Retrospectiva

### Què ha funcionat bé
- **Creació de components reutilitzables:** La creació de components visuals reutilitzables (com *SettingsItem*, *StatItem* o les targetes dels viatges) ha accelerat molt el desenvolupament de les pantalles secundàries i ha garantit una coherència visual impecable a tota l’app.

- **Arquitectura del disseny i temes:** La centralització estricta de la paleta de colors i la tipografia dins la carpeta `ui/theme` ha permès implementar un Mode Clar i Fosc impecable i automàtic, sense haver de “hardcodejar” colors a cada pantalla.

- **Gestió de la navegació:** L’estructuració del NavGraph centralitzat ha funcionat perfectament per gestionar de manera neta i automàtica quines pantalles havien de mostrar la barra inferior i quines no.

- **Disseny del logo i estètica general:** Els colors escollits per al logo han funcionat molt bé, tant per la seva combinació entre si com per la seva adaptació perfecta al Mode Clar i Mode Fosc. El resultat és una identitat visual sòlida, moderna i coherent amb la temàtica de viatges d’OdyWay.

### Què no ha funcionat
- S’ha perdut una mica de temps depurant errors de renderitzat de la interfície causats per components incompatibles de Material Design.

### Què millorarem al pròxim sprint
- Investigar i assegurar la compatibilitat de les llibreries abans d’intentar implementar components visuals avançats.
- Començar a estructurar la capa de domini/dades per substituir els models *mock* per dades reals.



---

## 5. Autoavaluació de l’equip (0-10)
**Nota:** 9 / 10

**Justificació:**  
L’equip ha superat bloquejos tècnics frustrants (com els tancaments en la navegació) aïllant el problema i buscant solucions creatives sense trencar l’arquitectura.  
Hem lliurat un prototip visualment molt atractiu, escalable i que compleix amb tots els requisits sol·licitats per aquest primer 