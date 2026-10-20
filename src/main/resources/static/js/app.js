// =====================================================================
// Interfaz de la tienda: catálogo, carrito y últimos pedidos.
// Las peticiones a la API están en api.js (objeto Api)
// =====================================================================

const CLAVE_CARRITO = "carniceria-rivas-carrito";
const CANTIDAD_MAXIMA = 99;

// Productos del catálogo por id (los trae la API al cargar la página)
const catalogo = new Map();

// Carrito: [{ productoId, cantidad }]. Se guarda en el navegador para no perderlo al recargar
let carrito = [];

const formatoEuros = new Intl.NumberFormat("es-ES", { style: "currency", currency: "EUR" });
const formatoFecha = new Intl.DateTimeFormat("es-ES", { dateStyle: "short", timeStyle: "short" });

const listaCarrito = document.querySelector("#lista-carrito tbody");
const carritoVacio = document.getElementById("carrito-vacio");
const totalCarrito = document.getElementById("total-carrito");
const contadorCarrito = document.getElementById("contador-carrito");
const listaPedidos = document.getElementById("lista-pedidos");
const sinPedidos = document.getElementById("sin-pedidos");

// ---------------------------------------------------------------------
// UTILIDADES
// ---------------------------------------------------------------------

function escapeHtml(texto) {
  const div = document.createElement("div");
  div.textContent = String(texto ?? "");
  return div.innerHTML;
}

const euros = (valor) => formatoEuros.format(Number(valor));

// ---------------------------------------------------------------------
// CATÁLOGO
// ---------------------------------------------------------------------

async function cargarCatalogo() {
  try {
    const [platos, cortes] = await Promise.all([
      Api.obtenerProductos("PLATO"),
      Api.obtenerProductos("CORTE"),
    ]);

    [...platos, ...cortes].forEach((producto) => catalogo.set(producto.id, producto));

    pintarProductos(document.querySelector("#platos .swiper-wrapper"), platos);
    pintarProductos(document.querySelector("#cortes .swiper-wrapper"), cortes);
    iniciarCarruseles();
  } catch (error) {
    document.querySelectorAll(".platillos .swiper-wrapper").forEach((contenedor) => {
      contenedor.innerHTML = `<p class="error-carga">No se pudo cargar el catálogo: ${escapeHtml(error.message)}</p>`;
    });
  }
}

// Platos y cortes usan el mismo diseño; los cortes llevan los colores apagados y el botón con borde
function pintarProductos(contenedor, productos) {
  contenedor.innerHTML = productos
    .map((producto) => {
      const esCorte = producto.categoria === "CORTE";
      const unidad = esCorte ? "kg" : "ración";

      return `
        <div class="swiper-slide">
          <div class="platillo">
            <h3 class="${esCorte ? "c1" : ""}">${escapeHtml(producto.nombre)}</h3>
            <p class="precio">${euros(producto.precio)} / ${unidad}</p>
            <img src="${escapeHtml(producto.imagen)}" alt="${escapeHtml(producto.nombre)}">
            <p class="${esCorte ? "c2" : ""}">${escapeHtml(producto.descripcion)}</p>
            <a href="#" class="agregar-carrito ${esCorte ? "btn-3" : "btn-2"}" data-id="${producto.id}">Agregar al carrito</a>
          </div>
        </div>`;
    })
    .join("");
}

function iniciarCarruseles() {
  new Swiper(".mySwiper", {
    slidesPerView: 3,
    spaceBetween: 30,
    loop: true,
    pagination: { el: ".swiper-pagination", clickable: true },
    navigation: { nextEl: ".swiper-button-next", prevEl: ".swiper-button-prev" },
    breakpoints: {
      0: { slidesPerView: 1 },
      520: { slidesPerView: 2 },
      950: { slidesPerView: 3 },
    },
  });
}

// Un solo evento para todos los botones "Agregar al carrito" (también los duplicados del carrusel)
document.addEventListener("click", (evento) => {
  const boton = evento.target.closest(".agregar-carrito");
  if (!boton) return;

  evento.preventDefault();
  agregarAlCarrito(Number(boton.dataset.id));
});

// ---------------------------------------------------------------------
// CARRITO
// ---------------------------------------------------------------------

function agregarAlCarrito(productoId) {
  const linea = carrito.find((l) => l.productoId === productoId);

  if (!linea) {
    carrito.push({ productoId, cantidad: 1 });
  } else if (linea.cantidad < CANTIDAD_MAXIMA) {
    linea.cantidad++;
  }

  actualizarCarrito();
}

function restarDelCarrito(productoId) {
  const linea = carrito.find((l) => l.productoId === productoId);
  if (!linea) return;

  linea.cantidad--;
  if (linea.cantidad <= 0) {
    quitarDelCarrito(productoId);
    return;
  }

  actualizarCarrito();
}

function quitarDelCarrito(productoId) {
  carrito = carrito.filter((l) => l.productoId !== productoId);
  actualizarCarrito();
}

function vaciarCarrito() {
  carrito = [];
  actualizarCarrito();
}

// Total orientativo para mostrar en pantalla; el total real lo calcula el servidor al comprar
function totalDelCarrito() {
  return carrito.reduce((total, linea) => {
    const producto = catalogo.get(linea.productoId);
    return total + (producto ? Number(producto.precio) * linea.cantidad : 0);
  }, 0);
}

function actualizarCarrito() {
  guardarCarrito();

  listaCarrito.innerHTML = carrito
    .map((linea) => {
      const producto = catalogo.get(linea.productoId);
      return `
        <tr>
          <td><p>${linea.cantidad}</p></td>
          <td><img src="${escapeHtml(producto.imagen)}" width="70" alt="${escapeHtml(producto.nombre)}"></td>
          <td>${escapeHtml(producto.nombre)}</td>
          <td>${euros(producto.precio)}</td>
          <td class="acciones-carrito">
            <a href="#" class="restar" data-id="${linea.productoId}" title="Quitar uno">-</a>
            <a href="#" class="sumar" data-id="${linea.productoId}" title="Añadir uno">+</a>
            <a href="#" class="borrar" data-id="${linea.productoId}" title="Quitar del carrito">X</a>
          </td>
        </tr>`;
    })
    .join("");

  const unidades = carrito.reduce((suma, linea) => suma + linea.cantidad, 0);
  contadorCarrito.textContent = unidades;
  contadorCarrito.classList.toggle("oculto", unidades === 0);

  carritoVacio.classList.toggle("oculto", carrito.length > 0);
  totalCarrito.textContent = `Total: ${euros(totalDelCarrito())}`;
  totalCarrito.style.display = carrito.length > 0 ? "block" : "none";
}

document.getElementById("carrito").addEventListener("click", (evento) => {
  const boton = evento.target.closest("[data-id]");
  if (!boton) return;

  evento.preventDefault();
  const id = Number(boton.dataset.id);

  if (boton.classList.contains("restar")) restarDelCarrito(id);
  if (boton.classList.contains("sumar")) agregarAlCarrito(id);
  if (boton.classList.contains("borrar")) quitarDelCarrito(id);
});

document.getElementById("vaciar-carrito").addEventListener("click", (evento) => {
  evento.preventDefault();
  vaciarCarrito();
});

document.getElementById("comprar-ahora").addEventListener("click", async (evento) => {
  evento.preventDefault();

  if (carrito.length === 0) {
    alert("No hay productos en el carrito");
    return;
  }

  try {
    const pedido = await Api.crearPedido(carrito);
    alert(`✅ Pedido nº ${pedido.id} realizado. Total: ${euros(pedido.total)}`);
    vaciarCarrito();
    await cargarPedidos();
  } catch (error) {
    alert("❌ " + error.message);
  }
});

function guardarCarrito() {
  try {
    localStorage.setItem(CLAVE_CARRITO, JSON.stringify(carrito));
  } catch {
    // Si el navegador no deja guardar, el carrito funciona igual mientras la página esté abierta
  }
}

// Recupera el carrito guardado, quitando productos que ya no estén en el catálogo
function recuperarCarrito() {
  try {
    const guardado = JSON.parse(localStorage.getItem(CLAVE_CARRITO) || "[]");
    carrito = guardado.filter(
      (l) => catalogo.has(l.productoId) && Number.isInteger(l.cantidad) && l.cantidad > 0
    );
  } catch {
    carrito = [];
  }
  actualizarCarrito();
}

// ---------------------------------------------------------------------
// ÚLTIMOS PEDIDOS
// ---------------------------------------------------------------------

async function cargarPedidos() {
  try {
    pintarPedidos(await Api.obtenerPedidos());
  } catch (error) {
    listaPedidos.innerHTML = "";
    sinPedidos.textContent = "No se pudieron cargar los pedidos.";
    sinPedidos.classList.remove("oculto");
  }
}

function pintarPedidos(pedidos) {
  sinPedidos.classList.toggle("oculto", pedidos.length > 0);

  listaPedidos.innerHTML = pedidos
    .map(
      (pedido) => `
      <tr>
        <td>${pedido.id}</td>
        <td>${formatoFecha.format(new Date(pedido.fecha))}</td>
        <td class="productos-pedido">${pedido.lineas
          .map((l) => `${l.cantidad} × ${escapeHtml(l.nombre)}`)
          .join("<br>")}</td>
        <td>${euros(pedido.total)}</td>
      </tr>`
    )
    .join("");
}

// ---------------------------------------------------------------------
// INICIO
// ---------------------------------------------------------------------

(async () => {
  await cargarCatalogo();
  recuperarCarrito();
  await cargarPedidos();
})();
