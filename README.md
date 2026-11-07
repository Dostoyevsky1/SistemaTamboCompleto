# Sistema Tambo

Sistema Tambo es una aplicación pensada para llevar el control diario de una tienda de manera sencilla. Desde un solo lugar se puede revisar el inventario, registrar pedidos, consultar las ventas y detectar rápidamente qué productos están por agotarse.

Al entrar al sistema encontrarás un panel con un resumen del negocio. El selector de sucursal permite consultar la información de cada local, mientras que las alertas ayudan a identificar productos con poco stock. En la sección **Productos** se administra el catálogo y se registran ventas; cada pedido descuenta automáticamente las unidades correspondientes. La sección **Pedidos** conserva el historial de movimientos y el panel principal muestra indicadores y los productos más vendidos.

Existen dos tipos de acceso: el administrador puede gestionar productos y usuarios, mientras que el empleado puede consultar el catálogo y registrar pedidos. Para probar todas las funciones administrativas, la pantalla de inicio ya incluye estas credenciales:

- **Usuario:** `admin`
- **Contraseña:** `admin123`

Solo hay que abrir el enlace del sistema y pulsar **Iniciar sesión**. La idea es que cualquier persona pueda recorrer la demostración sin tener que solicitar una cuenta antes.

El proyecto está formado por una interfaz web en Angular y un servicio en Spring Boot que se encarga de la seguridad, las reglas del negocio y la comunicación con la base de datos.
