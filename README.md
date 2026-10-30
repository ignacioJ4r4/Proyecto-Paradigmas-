# Horda Nocturna: Sobrevive hasta el Amanecer — Versión Beta

**GameLab – Paradigmas de Programación 2026**
Integrantes: Mathias Figueroa e Ignacio Jara

**Etapa 3 – Avance funcional (30-10):** primera versión jugable que ejecuta el ciclo principal del juego y permite comprobar sus mecánicas esenciales.

---

## 1. Cómo ejecutar

- **Opción rápida:** doble clic en `HordaNocturna.jar` (requiere Java 11 o superior).
- **Windows:** `compilar_y_ejecutar.bat`
- **NetBeans:** *File → New Project → Java with Ant → Java Project with Existing Sources*, agregar la carpeta `src` y elegir como clase principal `hordanocturna.Main`.

| Tecla | Acción |
|---|---|
| W A S D (o flechas) | Mover al cazador |
| ESC | Pausar / continuar |
| Q (en pausa) | Volver al menú |

---

## 2. Qué incluye esta beta

| Mecánica | Estado en la beta |
|---|---|
| Menú principal | ✅ Nueva partida / Salir |
| Movimiento libre y cámara que sigue al jugador | ✅ |
| Ciclo de juego (`javax.swing.Timer`, ~60 FPS) | ✅ |
| Arma con ataque automático | ✅ Daga (apunta sola al enemigo más cercano) |
| Enemigos y oleadas progresivas | ✅ 2 tipos: Murciélago (zigzag) y Esqueleto |
| Gemas de experiencia y subida de nivel | ✅ La barra se llena y aparece el aviso "¡Subiste a nivel N!" (todavía sin mejoras) |
| Farol (mecánica distintiva) | ✅ Versión básica: oscuridad y luz que se apaga; se recarga con aceite |
| HUD (vida, experiencia, nivel, tiempo, farol) | ✅ |
| Pausa | ✅ |
| Victoria / derrota con estadísticas | ✅ Partida corta de prueba: **5 minutos** |

### Pendiente para el siguiente avance

- **Sistema de mejoras al subir de nivel:** elegir 1 de 3 opciones (arma nueva, subir un arma o mejora pasiva).
- Más armas (Aura Sagrada, Hacha, Rayo) y 4 mejoras pasivas (Botas Veloces, Corazón Robusto, Mecha Larga, Amuleto Imán).
- Nuevos enemigos: el Lobo, que embiste, y el **jefe final**.
- Partida completa de 10 minutos.
- Farol completo: +50% de daño a los enemigos iluminados y ojos brillantes en la oscuridad.
- **Evolución de armas** (por ejemplo, Daga + Botas Veloces → Lluvia de Dagas).
- Persistencia (monedas, mejoras y récords), tienda y CRUD de niveles.

---

## 3. Arquitectura MVC

```
src/hordanocturna/
├── Main.java                      Punto de entrada
├── modelo/                        MODELO: estado y reglas (sin Swing)
│   ├── Partida.java               Ciclo de actualización, colisiones, fin de partida
│   ├── Entidad.java               Clase abstracta base (posición, radio, colisión)
│   ├── Jugador.java, Farol.java
│   ├── GeneradorOleadas.java, EstadoPartida.java, EstadisticasPartida.java
│   ├── armas/      Arma (abstracta), Daga, TipoArma
│   ├── enemigos/   Enemigo (abstracta), Murcielago, Esqueleto, TipoEnemigo
│   └── objetos/    Proyectil, Recolectable (abstracta), GemaExperiencia, FrascoAceite
├── vista/                         VISTA: VentanaPrincipal (CardLayout), PanelMenu, PanelJuego, PanelFinPartida
└── controlador/                   CONTROLADOR: ControladorAplicacion (navegación), ControladorJuego (teclado, mouse, Timer)
```

Cada cuadro sigue este flujo: `Timer` → `ControladorJuego.tick()` → `partida.actualizar(dt)` → `panelJuego.repaint()`.

---

## 4. Conceptos aplicados

- **Encapsulamiento:** atributos privados. `Jugador.getArmas()` devuelve una lista de solo lectura, y la vida solo cambia con `recibirDano()` o `curar()`.
- **Herencia:** `Entidad` es la base de Jugador, Enemigo, Proyectil y Recolectable. `Enemigo`, `Arma` y `Recolectable` son abstractas.
- **Polimorfismo:** `Partida` llama `enemigo.actualizar()` sin saber de qué tipo es el enemigo; el murciélago sobrescribe `calcularVelocidad()` para hacer zigzag. Lo mismo ocurre con `Arma.atacar()` y `Recolectable.aplicar()`.
- **Interfaz:** `KeyListener` en el controlador. En el siguiente avance se agregará la interfaz propia `OpcionMejora` para el sistema de mejoras.
- **Patrones:** Template Method (`Arma.actualizar()`), Factory (`TipoArma.crear()`, `TipoEnemigo.crear()`) y MVC.
- **Validaciones:** `agregarArma()` impide armas repetidas; `ganarExperiencia()` y `Farol.recargar()` rechazan valores negativos; la partida solo avanza en estado `JUGANDO`.

---

## 5. Pruebas

`test/hordanocturna/SimulacionPartida.java` juega partidas automáticas con un bot que esquiva enemigos y recoge gemas, y verifica que el ciclo del juego no produzca errores y que la partida termine correctamente. Con las semillas 1 a 6 hubo 4 victorias (5:00) y 2 derrotas (3:37 y 3:52).

También se probó la ventana real: menú → partida → movimiento → pausa → volver al menú.
