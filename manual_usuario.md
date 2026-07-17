## 1. Roles de Acceso en el Sistema

El sistema implementa seguridad basada en roles para separar las responsabilidades del negocio:
* Administrador (ROLE_ADMIN): Tiene control total sobre el catálogo de productos (crear, editar, eliminar), la administración de usuarios del sistema (crear/modificar personal) y la visualización de los KPIs del negocio.
* Empleado (ROLE_USER): Tiene acceso al catálogo de productos de la sucursal asignada para realizar transacciones de ventas ("Pedir") e inspeccionar el historial general de pedidos realizados.

---

## 2. Acceso al Sistema (Login)

1. Abre el navegador e ingresa a la URL del sistema (`http://localhost:4200` o tu URL en la nube).
2. Verás la pantalla de inicio de sesión con el logotipo de Tambo y una ilustración elegante clásica clásica en la derecha.
3. Ingresa tu Username y Contraseña:
   * Ejemplo Admin: `admin` / `admin123`
   * Ejemplo Empleado: `empleado` / `empleado123`
4. Si ingresas datos inválidos o dejas campos vacíos, el sistema te alertará visualmente mediante bordes rojos de advertencia.
5. Presiona Iniciar Sesión para acceder al panel principal.

---

## 3. Selector de Sucursal y Alertas (Barra Superior)

Una vez inicias sesión, notarás dos controles premium en la barra superior derecha de tu pantalla:

### 3.1 Selector de Sucursal
* Un menú desplegable al lado de un icono de tienda te permite conmutar entre las tiendas físicas:
  * Tambo - Miraflores
  * Tambo - San Isidro
  * Tambo - Surco
* Al cambiar la sucursal seleccionada, toda la interfaz se actualiza de forma automática: se recargan los productos de esa tienda, las métricas del panel principal y el ranking de ventas de esa locación específica.

### 3.2 Campana de Alertas de Stock Crítico
* A la derecha del selector se encuentra el icono de una campanilla.
* Si la sucursal seleccionada tiene productos con existencias de 10 unidades o menos, la campana mostrará un badge flotante rojo con el número total de alertas rebotando (`animate-bounce`).
* Haz clic en la campana para desplegar un listado detallado con los nombres de los productos y la cantidad exacta disponible en almacén para alertar el reabastecimiento.

---

## 4. Sección "Inicio" (Dashboard de Analíticas)

Esta es la pantalla de bienvenida por defecto:

* Tarjetas de KPIs: Cuatro indicadores sintéticos en modo oscuro:
  - Ventas Totales: Suma en tiempo real de los soles ingresados por pedidos en la sucursal.
  - Productos Registrados: Cantidad de ítems en el catálogo de la sucursal.
  - Stock Crítico: Número de alertas activas (se torna rojo de advertencia si es mayor a cero).
  - Usuarios Activos: Total de empleados activos del sistema.
* Gráfico de "Productos más vendidos: Un gráfico de barras adaptativo que muestra cuáles son los 5 productos con mayor salida comercial en la sucursal, lo cual facilita la toma de decisiones sobre compras de inventario.

---

## 5. Catálogo de Productos

Haz clic en la pestaña Productos en la barra lateral:

### 5.1 Registro de Nuevos Productos (Solo Administrador)
1. Presiona el botón Nuevo Producto en la esquina superior derecha.
2. Rellena el formulario del modal indicando: Nombre, Descripción, Precio y Stock inicial.
3. Presiona Guardar. El producto se creará asignado automáticamente a la sucursal activa en la que te encuentras.

### 5.2 Acciones del Catálogo
* Administrador: Puede hacer clic en el icono de lápiz para editar precio, stock o datos del producto, o en el icono de basurero para realizar una baja lógica del producto del catálogo (se marca como inactivo sin borrar el historial de pedidos de la base de datos).
* Empleado: Visualiza el botón Pedir.
  - Si haces clic en "Pedir", se abre un modal de venta donde ingresas la cantidad de unidades que el cliente va a comprar.
  - El sistema valida automáticamente que no puedas ingresar una cantidad mayor al stock disponible física en tienda.
  - Presiona Registrar Venta para descontar del stock en tiempo real y sumar al KPI de ventas del Dashboard.

---

## 6. Historial de Pedidos

Haz clic en la pestaña Pedidos en la barra lateral:
* Muestra una tabla detallada con el registro cronológico descendente (últimos pedidos primero) de todas las transacciones de ventas del sistema.
* Cada fila detalla: ID del pedido, el Nombre del empleado que realizó la transacción, el Producto, la Cantidad en unidades, la Fecha/Hora exacta de la venta y el Total facturado en soles (`S/`).

---

## 7. Gestión de Usuarios (Solo Administrador)

Haz clic en la pestaña Usuarios (Admin):
* Lista todos los usuarios/empleados del sistema.
* Permite al administrador crear nuevas cuentas de acceso, modificar el nombre completo, actualizar correos electrónicos, y asignar roles (`ADMIN` para gerentes, o `USER` para personal de caja y almacén).
