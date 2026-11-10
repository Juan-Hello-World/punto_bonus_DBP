# Laboratorio Semana 14 – Fly Away ✈️

Construye un frontend en React + TypeScript para la API de reserva de vuelos **Fly Away**.

El backend ya está funcionando y completamente documentado — tu trabajo es construir la aplicación web que lo consume.

**Recompensa total:** hasta 1 punto adicional en tu PC2.

---

## La tarea

Construye una SPA con las siguientes funcionalidades. Las marcadas como **must have** son obligatorias; las **nice to have** suman puntos extra.

---

### 1. Pantalla de Registro — `POST /users/register` · +0.1 pts (must have)

- Formulario con: email, nombre, apellido, contraseña
- Validar campos vacíos antes de enviar
- Mostrar errores del backend al usuario (contraseña débil, email en uso)
- Mostrar mensaje de éxito y redirigir al login

---

### 2. Pantalla de Login — `POST /auth/login` · +0.2 pts (must have) · +0.1 pts (nice to have)

**Must have:**
- Formulario con email y contraseña
- Guardar el token JWT en `localStorage` al iniciar sesión
- Mostrar error si las credenciales son incorrectas
- Redirigir al home/búsqueda tras login exitoso

**Nice to have (+0.1):**
- Mostrar el nombre del usuario autenticado en pantalla (`GET /users/current`)

---

### 3. Búsqueda de Vuelos — `GET /flights/search` · +0.1 pts (must have) · +0.1 pts (nice to have)

**Must have:**
- Inputs de búsqueda: número de vuelo y/o nombre de aerolínea
- Mostrar resultados en tabla: número, aerolínea, salida, llegada, asientos disponibles
- Manejar resultado vacío con mensaje amigable

**Nice to have (+0.1):**
- Filtro por rango de fechas de salida

---

### 4. Reservar un Vuelo — `POST /flights/book` · +0.1 pts (must have) · +0.1 pts (nice to have)

**Must have:**
- Botón "Reservar" en cada resultado de búsqueda
- Solo disponible para usuarios autenticados
- Mostrar mensaje de éxito con el ID de reserva
- Mostrar errores del backend (vuelo pasado, horario superpuesto)

**Nice to have (+0.1):**
- Ver detalle de la reserva (`GET /flights/book/{id}`)

---

### 5. Mis Reservas — `GET /flights/book/{id}` · +0.1 pts (nice to have)

- Guardar IDs de reservas del usuario en `localStorage`
- Listar reservas con: número de vuelo, aerolínea, fecha de salida
- Solo visible para usuarios autenticados

---

### 6. Cerrar Sesión & Navegación · +0.1 pts (must have)

- Botón de logout que limpia el token de `localStorage`
- Rutas protegidas: redirigir al login si no hay token
- Navegación clara entre: Registro → Login → Búsqueda → Reserva

---

## Resumen de puntaje

| Funcionalidad | Must Have | Nice to Have |
|---------------|-----------|--------------|
| Registro | +0.1 | — |
| Login | +0.2 | +0.1 |
| Búsqueda de vuelos | +0.1 | +0.1 |
| Reservar vuelo | +0.1 | +0.1 |
| Mis reservas | — | +0.1 |
| Cerrar sesión & navegación | +0.1 | — |
| **Total** | **0.6** | **0.4** |

---

## Antes de empezar

Necesitas instalado:

| Herramienta | Versión | Verificar con |
|-------------|---------|---------------|
| JDK | **26** (el backend no compila con Java 21 o anterior) | `java -version` |
| Node.js | 20.19+ o 22.12+ (requerido por Vite) | `node -v` |
| Git | cualquiera | `git --version` |

> Si no tienes JDK 26: `sdk install java 26-open` (SDKMAN) o descárgalo desde [adoptium.net](https://adoptium.net). En IntelliJ, selecciona el JDK 26 en *File → Project Structure → SDK*.

Clona el backend y levántalo:

```bash
git clone https://github.com/CS2031-DBP/cs2031-2026-2-week14-fly-away-backend.git
cd cs2031-2026-2-week14-fly-away-backend
./mvnw spring-boot:run
```

Tu frontend va en **otra carpeta**, fuera de este repositorio. Durante el desarrollo tendrás dos terminales abiertas: una con el backend (`localhost:8080`) y otra con el frontend (`npm run dev`, `localhost:5173`).

---

## Stack tecnológico

| Capa | Elección |
|------|----------|
| Framework | React 19 |
| Lenguaje | TypeScript |
| Build tool | Vite |
| Cliente HTTP | Axios (recomendado) o `fetch` |
| Estilos | A tu elección — CSS plano, Tailwind, MUI, etc. |

Inicializa el proyecto con:

```bash
npm create vite@latest fly-away-frontend -- --template react-ts
cd fly-away-frontend
npm install
npm install axios
npm run dev
```

---

## Conectarse al backend

El backend corre localmente en `http://localhost:8080`. Debes iniciarlo antes de correr tu frontend.

```bash
# En la carpeta del backend (este repositorio):
./mvnw spring-boot:run
```

Configura la URL base de Axios:

```ts
// src/api.ts
import axios from 'axios';

const api = axios.create({ baseURL: 'http://localhost:8080' });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default api;
```

> **CORS:** El backend tiene CORS habilitado para cualquier origen `http://localhost:*` y `http://127.0.0.1:*`, por lo que las solicitudes desde `localhost:5173` (puerto por defecto de Vite) funcionarán sin problemas, incluyendo las que envían el header `Authorization`.

---

## Cómo usar los endpoints paso a paso

Todos los ejemplos usan el `api` de `src/api.ts` (arriba). Para probar sin registrarte puedes iniciar sesión con `ana.garcia@utec.edu.pe` / `Password1` (ver *Datos de ejemplo* en el [README.md](./README.md)).

Todos los errores `400` llegan como `ProblemDetail`; el mensaje para mostrar al usuario está en `err.response.data.detail`:

```ts
import axios from 'axios';

function errorMessage(err: unknown): string {
  if (axios.isAxiosError(err)) {
    return err.response?.data?.detail ?? 'Error de conexión con el servidor';
  }
  return 'Error inesperado';
}
```

### 1. Registro — `POST /users/register` (público)

```ts
await api.post('/users/register', { email, firstName, lastName, password });
// 201 → { id: 6 }  → muestra un mensaje de éxito y navega a /login
```

- Reglas del backend: contraseña de **8+ caracteres con al menos una mayúscula y un dígito**; nombre y apellido deben **empezar con mayúscula**.
- Errores (`400`, en `detail`): `password: must be at least 8 characters`, `password: must contain at least one uppercase letter and one digit`, `firstName: must start with an uppercase letter`, `email: must be a valid email`, `Username already exists` (email en uso).
- Valida los campos vacíos **antes** de enviar: el backend acepta un email vacío.

### 2. Login — `POST /auth/login` (público) + `GET /users/current` (protegido)

```ts
const { data } = await api.post('/auth/login', { email, password });
localStorage.setItem('token', data.token); // la clave DEBE ser 'token': es la que lee el interceptor de api.ts

const me = (await api.get('/users/current')).data;
// { id, username, email, firstName, lastName, role }
// muestra `${me.firstName} ${me.lastName}` y navega a la búsqueda
```

- Credenciales incorrectas → `400` con `detail: "Username does not exist"` (el mismo mensaje si el email no existe o si la contraseña es incorrecta).

### 3. Búsqueda de vuelos — `GET /flights/search` (público)

```ts
// `from` y `to` son los valores de inputs <input type="datetime-local"> (pueden estar vacíos)
const { data } = await api.get('/flights/search', {
  params: {
    flightNumber: flightNumber || undefined,
    airlineName: airlineName || undefined,
    estDepartureTimeFrom: from ? new Date(from).toISOString() : undefined,
    estDepartureTimeTo: to ? new Date(to).toISOString() : undefined,
  },
});
const flights = data.items; // ← la lista viene dentro de "items", no es un arreglo directo
```

- Cada vuelo trae: `id`, `flightNumber`, `airlineName`, `estDepartureTime`, `estArrivalTime`, `availableSeats`.
- Todos los filtros son opcionales y se combinan (AND). La búsqueda es parcial y no distingue mayúsculas (`latam` encuentra `LATAM Airlines`).
- Sin resultados → `{ "items": [] }`: muestra un mensaje amigable.
- **Fechas:** convierte siempre con `toISOString()`. Si envías el valor crudo del input (`2026-11-10T05:00`) el backend responde `500`.

### 4. Reservar un vuelo — `POST /flights/book` (protegido)

```ts
const { data } = await api.post('/flights/book', { flightId: flight.id });
// 200 (no 201) → { id: 1 }  ← este es el ID de la reserva: muéstralo en el mensaje de éxito

// Nice to have: detalle de la reserva
const booking = (await api.get(`/flights/book/${data.id}`)).data;
// { id, bookingDate, flightId, flightNumber, airlineName, estDepartureTime, estArrivalTime,
//   customerId, customerFirstName, customerLastName }
```

- Errores (`400`, en `detail`):
  - `Cannot book a past flight` → pruébalo con el vuelo **16** (`LA100`).
  - `Overlapping flight found` → reserva el vuelo **1** (`LA101`) y luego el **17** (`SK120`).
- Reservar el mismo vuelo dos veces **no** es un error para el backend.
- Sin token → `403` con cuerpo vacío. Muestra el botón "Reservar" solo si hay token.

### 5. Mis reservas — `GET /flights/book/{id}` (protegido)

No existe un endpoint que liste las reservas del usuario: guarda los IDs en `localStorage` al reservar, **separados por usuario** (el backend devuelve cualquier reserva a cualquier usuario autenticado).

```ts
const key = `bookings:${me.email}`;

// Al reservar (paso 4):
const ids: number[] = JSON.parse(localStorage.getItem(key) ?? '[]');
localStorage.setItem(key, JSON.stringify([...ids, data.id]));

// En la página "Mis reservas":
const saved: number[] = JSON.parse(localStorage.getItem(key) ?? '[]');
const results = await Promise.allSettled(saved.map(id => api.get(`/flights/book/${id}`)));
const bookings = results
  .filter(r => r.status === 'fulfilled')
  .map(r => (r as PromiseFulfilledResult<any>).value.data);
// muestra: flightNumber, airlineName, estDepartureTime
```

- `Promise.allSettled` ignora los IDs que ya no existen: si reinicias el backend, las reservas se borran pero los IDs siguen en `localStorage`.

### 6. Cerrar sesión y navegación (sin endpoint)

```ts
// Logout
localStorage.removeItem('token');
navigate('/login');

// Rutas protegidas: si no hay token, redirige a /login
if (!localStorage.getItem('token')) navigate('/login');

// Token inválido: ante un 403 de cualquier endpoint protegido, limpia el token y vuelve al login
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 403) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  },
);
```

- Los endpoints protegidos responden `403` (no `401`) sin token o con un token inválido. El token no expira, pero si reinicias el backend los usuarios que registraste se borran y su token deja de servir.

---

## Referencia de la API del backend

Toda la documentación de endpoints — rutas, cuerpos de solicitud, estructura de respuestas, requisitos de autenticación y reglas de negocio — está en **[README.md](./README.md)**.

---

## Entregables

- Una app React + TypeScript funcional que cubra como mínimo los must have
- Código limpio y legible (componentes separados por responsabilidad)
- Un `README.md` corto dentro de tu carpeta de frontend explicando cómo correrlo

---

## Consejos

- Guarda el JWT en `localStorage` por simplicidad
- Usa inputs `datetime-local` y convierte a ISO-8601 para los filtros de fecha (`2026-12-01T00:00:00Z`)
- Al iniciar, el backend carga usuarios y vuelos de ejemplo (contraseña `Password1`), incluyendo un vuelo pasado y uno con horario superpuesto para probar los errores de reserva — ver la sección *Datos de ejemplo* en el [README.md](./README.md)
- Las horas de los vuelos usan ISO-8601 con timezone — `new Date(str).toLocaleString()` las muestra legibles
