# 🥩 Página Web – Carnicería Rivas

Aplicación web desarrollada con **Spring Boot** que permite registrar y gestionar
los productos (carnes) de una carnicería, almacenando la información de forma
persistente en una base de datos local.

El objetivo del proyecto es mostrar una aplicación backend funcional con
persistencia de datos, arquitectura clara y configuración portable.

---

## 🚀 Funcionalidades

- Registro de productos (nombre, imagen y precio)
- Persistencia de datos entre ejecuciones
- Creación automática de la base de datos y tablas
- Backend desarrollado con Spring Boot
- Frontend servido desde recursos estáticos

---

## 🛠️ Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- SQLite (base de datos embebida)
- Maven
- HTML, CSS y JavaScript

---

## 📂 Estructura del proyecto

src/
├── main/
│ ├── java/
│ │ └── com.informaticonfig.spring.app1.springboot_applications
│ │ ├── controllers
│ │ ├── model
│ │ └── repository
│ └── resources/
│ ├── static/
│ └── application.properties
└── test/

---

## ⚙️ Ejecución del proyecto

1. Clonar el repositorio:
   git clone https://github.com/TU_USUARIO/NOMBRE_DEL_REPOSITORIO.git

2. Acceder al directorio del proyecto:
   cd NOMBRE_DEL_REPOSITORIO

3. Ejecutar la aplicación:
   mvn spring-boot:run

4. Acceder desde el navegador:
   http://localhost:8080
