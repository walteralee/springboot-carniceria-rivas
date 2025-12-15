# 🥩 Página Web – Carnicería Rivas

Aplicación web desarrollada con **Spring Boot** que permite registrar y gestionar
los productos (carnes) de una carnicería, almacenando la información de forma
persistente en una base de datos local.

El objetivo del proyecto es mostrar una aplicación backend funcional, con
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
## ⚙️ Ejecución del proyecto

1. Clona el repositorio desde GitHub:

```bash
git clone https://github.com/walteralee/springboot-carniceria-rivas.git

2. Accede al directorio del proyecto:

cd springboot-carniceria-rivas

3. Ejecuta la aplicación.

mvn spring-boot:run

Si no tienes Maven instalado (opción recomendada), usa Maven Wrapper:

./mvnw spring-boot:run

Una vez iniciada la aplicación, accede desde el navegador a:

http://localhost:8080
