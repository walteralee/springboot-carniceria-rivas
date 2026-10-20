//Slider
var swiper = new Swiper(
    ".mySwiper",
    {
        slidesPerView: 3,
        spaceBetween: 30,
        loop: true,
        pagination: {
            el: ".swiper-pagination",
            clickable: true,
        },
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev",
        },
        breakpoints: {
            0: {
                slidesPerView: 1,
            },
            520: {
                slidesPerView: 2,
            },
            950: {
                slidesPerView: 3,
            }
        }
    }
);


//--------------------------------------------------------- Prodcutos ------------------------------------------------------------------------


// Clase Producto
class Producto {
    constructor(id, imagen, titulo, precio, cantidad) {
        this.id = String(id);
        this.imagen = String(imagen);
        this.titulo = String(titulo);
        this.precio = Number(precio); // 👈 Asegura que sea número
        this.cantidad = parseInt(cantidad);
    }
}




// Array vacío para almacenar productos
const productos = [];

function buscar_producto(id_producto) {
    // Recorremos el array de productos
    for (let i = 0; i < productos.length; i++) {
        if (productos[i].id === id_producto) {
            return true; // Producto encontrado
        }
    }

    return false; // Producto no encontrado
}


function sumar(id_producto, img, titulo, precio) {
    // Si el producto no existe, lo creamos con cantidad 1
    if (!buscar_producto(id_producto)) {
        const nuevoProducto = new Producto(id_producto, img, titulo, precio, "1");
        productos.push(nuevoProducto);
        return;
    }

    // Si existe, buscamos el producto y aumentamos en 1 su cantidad
    for (let i = 0; i < productos.length; i++) {
        if (productos[i].id === id_producto) {
            let cantidad = parseInt(productos[i].cantidad);
            cantidad += 1;
            productos[i].cantidad = String(cantidad);
            return;
        }
    }
}


function restar(id_producto) {
    id_producto = String(id_producto); // asegurar comparación en string

    for (let i = 0; i < productos.length; i++) {
        if (productos[i].id === id_producto) {
            let cantidad = parseInt(productos[i].cantidad);
            cantidad = Math.max(0, cantidad - 1); // evitamos negativos

            if (cantidad === 0) {
                productos.splice(i, 1); // eliminamos el producto del array
                return 0;
            } else {
                productos[i].cantidad = String(cantidad);
                return cantidad;
            }
        }
    }

    return 0; // si no se encontró el producto
}


function mostrar_productos() {
    if (productos.length === 0) {
        console.log("No hay productos en el array.");
        return;
    }

    console.log("===== Productos en el array =====");
    productos.forEach((producto, index) => {
        console.log(`Producto ${index + 1}:`);
        console.log(`  ID: ${producto.id}`);
        console.log(`  Título: ${producto.titulo}`);
        console.log(`  Imagen: ${producto.imagen}`);
        console.log(`  Precio: ${producto.precio}`);
        console.log(`  Cantidad: ${producto.cantidad}`);
        console.log("----------------------------");
    });
}


function eliminar_producto_por_id(id_producto) {
    id_producto = String(id_producto); // aseguramos string

    for (let i = 0; i < productos.length; i++) {
        if (productos[i].id === id_producto) {
            productos.splice(i, 1); // eliminamos 1 elemento en la posición i
            return true; // Producto eliminado
        }
    }

    return false; // No se encontró el producto
}


function precio_total() {
    return productos.reduce((total, prod) => {
        const cantidad = isNaN(prod.cantidad) ? 0 : prod.cantidad;
        const precio = isNaN(prod.precio) ? 0 : prod.precio;
        return total + (precio * cantidad);
    }, 0);
}









//--------------------------------------------------------------------------Carrito---------------------------------------------------------------

const carrito = document.getElementById('carrito'); //Variable del carrito
const elementos = document.getElementById('lista'); //Variable de los elementos a comprar seccion comida
const elementos2 = document.getElementById('lista-2');  //Variable de los elementos a comprar seccion carnes
const lista = document.querySelector('#lista-carrito tbody'); //Variable del cuerpo de la tabla del carrito
const vaciarCarritoBtn = document.getElementById('vaciar-carrito'); //Variable del boton vaciar carrito
const comprar_productos = document.getElementById('comprar-ahora'); //Variable del boton vaciar carrito
const contador = document.querySelector('.parte-inferior-carrito h3'); //Contador de dinero
contador.textContent = 'Total: 123€';


cargarEventListeners(); //Funcion que carga todos los eventos

function cargarEventListeners() {
    elementos.addEventListener('click', comprarElemento); //Cuando se presiona "Agregar al carrito" en la seccion comida
    elementos2.addEventListener('click', comprarElemento); //Cuando se presiona "Agregar al carrito" en la seccion carnes
    carrito.addEventListener('click', quitar_producto_de_carrito); //Cuando se presiona "X" en el carrito
    vaciarCarritoBtn.addEventListener('click', vaciarCarritoEvent);  //Cuando se presiona "Vaciar carrito"
}

function comprarElemento(e) {
    e.preventDefault(); //Previene la accion por defecto del evento

    if (e.target.classList.contains('agregar-carrito')) { //Verifica si el elemento presionado tiene la clase "agregar-carrito"
        const elemento = e.target.parentElement.parentElement; //Accede al elemento padre del elemento presionado
        leerDatosElemento(elemento); //Envia el elemento a la funcion leerDatosElemento
    }

}

function leerDatosElemento(elemento) {
    const textoPrecio = elemento.querySelector('.precio').textContent.trim(); // Quita espacios
    const soloNumeros = textoPrecio.replace(/[^\d.,]/g, ""); // Elimina todo menos números, punto o coma
    const precioLimpio = parseFloat(soloNumeros.replace(",", ".")) || 0; // Cambia coma por punto y asegura número

    const infoElemento = {
        imagen: elemento.querySelector('img').src,
        titulo: elemento.querySelector('h3').textContent.trim(),
        precio: precioLimpio,  // ✅ Ya es número
        id: elemento.querySelector('a').getAttribute('data-id')
    };

    sumar(infoElemento.id, infoElemento.imagen, infoElemento.titulo, infoElemento.precio);
    actualizar_carrito();
}





function actualizar_carrito() {

    // 1) Vaciar carrito
    vaciarCarrito();

    // 2) Recorre productos
    for (let i = 0; i < productos.length; i++) {

        annadir_producto_carrito(productos[i]);
    }

    // 3) Calcular el total
    const total = precio_total();

    // 4) Mostrar u ocultar según el total
    if (total > 0) {
        contador.style.display = "block"; // Mostrar
        contador.textContent = `Total: ${total}€`;
    } else {
        contador.textContent = "Total: 0€";
        contador.style.display = "none"; // Ocultar
    }

}

function vaciarCarrito() { //Funcion que vacia el carrito     

    while (lista.firstChild) {  //Mientras el cuerpo de la tabla tenga un primer hijo
        lista.removeChild(lista.firstChild); //Elimina el primer hijo del cuerpo de la tabla
    }

    return false;
}

function annadir_producto_carrito(elemento) {

    const row = document.createElement('tr'); //Crea una fila en la tabla del carrito
    row.innerHTML = ` 
        <td>
            <p>${elemento.cantidad}</p>
        </td> 
        <td>
            <img src="${elemento.imagen}" width="70" />
        </td>       
        <td>
            ${elemento.titulo}
        </td>
        <td>
            ${elemento.precio}
        </td>
        <td>
            <a href="#" class="borrar" data-id="${elemento.id}">X</a>
        </td>
        <td>
            <a href="#" class="restar">-</a>
        </td>
    `;

    lista.appendChild(row); //Agrega la fila al cuerpo de la tabla del carrito

}


function quitar_producto_de_carrito(e) {

    e.preventDefault();

    let elemento, elementoId;

    if (e.target.classList.contains('borrar')) {

        elemento = e.target.parentElement.parentElement;
        elemento.remove();
        elementoId = elemento.querySelector('a').getAttribute('data-id');
        eliminar_producto_por_id(elementoId);

    }
    else if (e.target.classList.contains('restar')) {

        elemento = e.target.parentElement.parentElement;
        elementoId = elemento.querySelector('a').getAttribute('data-id');
        cantidad = restar(elementoId);

        if (cantidad === 0) {
            elemento.remove();
        }

    }

    actualizar_carrito();
}



function vaciarCarritoEvent(e) {
    
    e.preventDefault();
    console.log("Botón clickeado");

    // Vaciar visualmente
    while (lista.firstChild) {
        lista.removeChild(lista.firstChild);
    }

    // Vaciar datos
    productos.length = 0;

    // Actualizar total si lo tienes
    actualizar_carrito();
}


comprar_productos.addEventListener('click', async () => {

    // Parte 1
    if (productos.length === 0) {
        alert("No hay productos en el carrito");
        return;
    }

    // Parte 2
    const data = productos.map(p => ({
        titulo: p.titulo,
        url_imagen: p.imagen,
        precio: parseFloat(p.precio)
    }));


    // Parte 3
    try {

        // Parte 3.1
        const res = await fetch("/api/productos/comprar", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });


        // Parte 3.2
        if (res.ok) {
            const result = await res.json();
            console.log("Productos guardados:", result);
            alert("Compra realizada con éxito");
            productos.length = 0; // vaciar array
        } else {
            alert("Error al guardar los productos");
        }

    } catch (err) {
        console.error("Error de conexión:", err);
    }
});