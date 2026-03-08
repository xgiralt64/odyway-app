# Disseny de la app OdyWay

## Arquitectura General

Hem decitit que OdyWay segueixi una arquitectura en 3 capes per mantenir el codi ordenat i fàcil d’escalar:

- **UI (`/ui/`)**  
  Conté les pantalles (Jetpack Compose) i la navegació. Mostra informació i captura accions de l’usuari, però no guarda dades ni aplica lògica complexa.

- **Domain (`/domain/`)**  
  Conté el **model de domini** (classes com `Trip` o `Activity`) i altres funcions de calcul
  De moment hi ha funcionalitats futures marcades amb `@TODO`.

- **Data (`/data/`)**  
  Proporciona dades a l’app. De moment en aquest Sprint es treballa amb dades mock que mes endevant substituirem amb la API


## **Estructura del Projecte**

```plaintext
com.example.odyway/
├── ui/
│   ├── screens/          # Totes les pantalles de l'app (Home, Trips, Profile, etc.)
│   ├── theme/            # Colors, tipografia i tema dinàmic (Clar/Fosc)
│   └── navigation/       # Graf de navegació i rutes
├── domain/               # Model de domini (Trip, User, Activity, ItineraryItem...)
└── data/
    ├── repository/       
    └── local/            

```

## Pantalles Implementades (Sprint 01)

---

## Flux d'Autenticació

### **SplashScreen**
- Logo de l'app  
- Indicador de càrrega  
- Versió (amb retard simulat)

### **LoginScreen**
- Formulari d’inici de sessió (usuari + contrasenya)

---

## Pestanyes Principals (Bottom Navigation)

### **HomeScreen**
- Panell principal  
- Viatge actual ("LIVE")  
- Propers viatges planificats  
- Suggeriments de destins

### **TripsScreen**
- Gestió d’un viatge  
- Pestanyes internes:
  - Itinerari  
  - Galeria (grid d’imatges)  
  - Costos  

### **ProfileScreen**
- Dades de l’usuari  
- Estadístiques (Trips, Countries, Photos)

---

## Altres Pantalles (Settings Flow)

### **SettingsScreen**
- Opcions del compte  
- Components reutilitzables (*SettingsItem*)

### **PreferencesScreen**
- Mode fosc  
- Idioma  
- Notificacions push

### **AboutScreen**
- Informació de l’equip  
- Llicència  
- Detalls tècnics

### **TermsConditionsScreen**
- Condicions d’ús  
- Scroll independent  
- Botons d’acció

---

## Model de Dades

L’aplicació gestiona les següents entitats principals (definides a `/domain/`):

### **User**
- ID  
- Nom complet  
- Nom d’usuari  
- Email  
- Foto de perfil  

### **Preferences**
- Idioma  
- Tema (clar/fosc)  
- Notificacions  

### **Trip**
- Títol  
- Destinació  
- Pressupost  
- Estat  
- Dates d’inici i fi  

### **Activity** *(Gestió de Pressupost)*
- Títol  
- Descripció  
- Localització  
- Cost  

### **ItineraryItem** *(Agenda)*
- Data  
- Hora  
- Títol  
- Localització  
- Estat  

### **GalleryImage**
- URL  
- Descripció  

### **Recommendation**
- Suggeriments de destins per inspirar l’usuari  

---

## Relacions

- Un **User** té unes úniques **Preferences**  
- Un **User** posseeix múltiples **Trips**  
- Un **User** rep múltiples **Recommendations**  
- Un **Trip** gestiona múltiples **Activities**  
- Un **Trip** programa múltiples **ItineraryItems**  
- Un **Trip** emmagatzema múltiples **GalleryImages**  

El diagrama següent reflecteix aquestes relacions i s’ha mantingut coherent amb les classes implementades a `app/domain/`.

![alt text](domain_model.png)