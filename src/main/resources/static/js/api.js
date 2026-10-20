// =====================================================================
// Acceso a la API REST. Aquí solo hay peticiones HTTP; la interfaz está en app.js
// =====================================================================

// Hace la petición y devuelve el JSON. Si la API responde con error, lanza un Error con su mensaje
async function peticion(ruta, opciones = {}) {
  const respuesta = await fetch(ruta, opciones);
  const datos = await respuesta.json().catch(() => ({}));

  if (!respuesta.ok) {
    throw new Error(datos.message || `Error ${respuesta.status} en el servidor`);
  }

  return datos;
}

const Api = {
  // categoria: "PLATO" o "CORTE"
  obtenerProductos: (categoria) => peticion(`/api/productos?categoria=${encodeURIComponent(categoria)}`),

  // lineas: [{ productoId, cantidad }]. El total lo calcula el servidor
  crearPedido: (lineas) =>
    peticion("/api/pedidos", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ lineas }),
    }),

  obtenerPedidos: () => peticion("/api/pedidos"),
};
