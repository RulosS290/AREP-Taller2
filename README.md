# AREP-Taller2: Microframeworks Web
## Descripción
El proyecto busca mejorar un servidor web existente, transformándolo en un framework web completo. Este nuevo marco permitirá desarrollar aplicaciones web con servicios REST backend, proporcionando herramientas para:
* Definir servicios REST usando funciones lambda.
* Gestionar parámetros de consulta en las solicitudes.
* Especificar la ubicación de los archivos estáticos
## Empezando
Estas instrucciones le permitirán obtener una copia del proyecto en funcionamiento en su máquina local para fines de desarrollo y prueba. Consulte la sección Implementación para obtener notas sobre cómo implementar el proyecto en un sistema en vivo.

### Requisitos

* [Git](https://git-scm.com/) - Control de versiones
* [Maven](https://maven.apache.org/) - Manejador de dependencias
* [Java](https://www.oracle.com/java/technologies/downloads/#java17) - Lenguaje de programación

* ### Instalación

Realice los siguientes pasos para clonar el proyecto en su máquina local.

```bash
git clone https://github.com/RulosS290/AREP-Taller2.git
```

## Ejecutando la aplicación

Para ejecutar la aplicación, ejecute el siguiente comando:
* Compilar el proyecto
```bash
mvn compile
```
* Ejecutar pruebas
```bash
mvn test
```
* Construir el proyecto
```bash
mvn clean package
```
* Ejecutar el Proyecto
```bash
mvn clean compile exec:java '-Dexec.mainClass=com.eci.SimpleWebServer'
```
* Seguimos el siguiente URl para ver la aplicación: http://localhost:8080/index.html
## Funcionalidad
1. Probamos el primer requerimiento, el cual es hacer un metodo GET para los servicios estaticos. http://localhost:8080/helloWorld
2. 
   ![imagen](https://github.com/user-attachments/assets/9d6e1702-5e32-482a-937e-828882fdf586)
3. Probamos el segundo requerimiento. http://localhost:8080/hello?name=Daniel
4. 
   ![imagen](https://github.com/user-attachments/assets/faa8ef56-1713-44bc-b627-b8a644c854e8)
   
   Se puede cambiar el nombre cambiando el nombre después del igual(Ej hello?name=Federico)
5. Adicional http://localhost:8080/nameAge?name=Daniel&age=21
   ![imagen](https://github.com/user-attachments/assets/e470e9ae-c364-431a-8964-a71369567167)
6. Tercer requerimiento, especificación de los archivos estaticos por medio de un metodo staticfiles().
   ![imagen](https://github.com/user-attachments/assets/46b73fd2-e3cd-431f-80ac-fef80662ebce)
   
   En caso de cambiar la ruta no se encontrarian los archivos.
   
   ![imagen](https://github.com/user-attachments/assets/f9a1a9ce-fd00-4bf1-8ce3-58160b570157)
   
   ![imagen](https://github.com/user-attachments/assets/dce0e12c-6a15-4e13-97ec-9650e7b883be)

7. Responder solicitudes de archivos estaticos. http://localhost:8080/From_Software.json o http://localhost:8080/EA.json
   
![imagen](https://github.com/user-attachments/assets/d4b7683e-1ac5-4350-a57d-70f3a3e81c04)

## Ejecutando pruebas

```bash
mvn test
```

![imagen](https://github.com/user-attachments/assets/4accbf6f-c8c0-43af-b7cd-569c72dfac4d)

## Arquitectura del Proyecto


### SimpleWebServer

Un servidor web simple en Java que maneja solicitudes HTTP GET para archivos estáticos y servicios dinámicos. Este servidor está diseñado para ser extensible, permitiendo la adición de nuevos servicios personalizados.

##### Componentes Principales

- **Paquete Principal:** `com.eci`
- **Clase Principal:** `SimpleWebServer`

##### Estructura

- **Servidor HTTP:**
  - Escucha en el puerto `8080`.
  - Utiliza un `ExecutorService` con un grupo de hilos (`threadPool`) para manejar múltiples conexiones simultáneas.
  - Acepta conexiones de clientes mediante un `ServerSocket`.

- **Manejo de Solicitudes:**
  - **Cliente:** `ClientHandler` (implementa `Runnable`):
    - Lee la solicitud HTTP del cliente.
    - Determina si la solicitud es para un archivo estático o un servicio registrado.
    - Envía la respuesta adecuada (archivo o servicio).

- **Archivos Estáticos:**
  - La ubicación de los archivos estáticos se define con el método `staticfiles(String folder)`, que establece `WEB_ROOT` a una carpeta dentro de `target/`.

- **Servicios Dinámicos:**
  - Servicios personalizados pueden ser registrados mediante el método `get(String url, Service s)`, que asocia una URL con una implementación de `Service`.
  - La interfaz `Service` define un único método `getValues(Request req, String response)` para manejar las solicitudes.

##### Manejo de Solicitudes HTTP

- **Método `GET`:**
  - Si la solicitud coincide con una ruta de servicio registrada, se ejecuta el servicio correspondiente.
  - Si la solicitud es para un archivo estático, el archivo se sirve con el tipo de contenido adecuado.
  - Si el archivo no existe, se devuelve un error 404.

##### Componentes Internos

- **`Request` y `Response`:**
  - Se crean objetos para representar la solicitud y la respuesta. `Request` se usa para obtener parámetros de la consulta.
  
- **`Request` Clase:**
  - **Constructor:** `Request(String requestLine)`
    - Analiza la línea de solicitud para extraer el camino y los parámetros de consulta.
  - **Métodos:**
    - `getPath()`: Devuelve el camino de la solicitud.
    - `getValues(String name, String defaultValue)`: Obtiene el valor del parámetro de consulta, o un valor por defecto si no se encuentra.

- **`RequestHandler` Clase:**
  - **Método Estático:** `handleRequest(Request req)`
    - Maneja solicitudes específicas basadas en el camino de la solicitud.
    - Ejemplos de manejo:
      - Para `"/hello"`: Devuelve un saludo con los parámetros `name` y `age`.
      - Para otros caminos: Devuelve `"404 Not Found"`.

- **`Service` Interface:**
  - Define la lógica para procesar solicitudes dinámicas.
  
- **`getContentType(String fileRequested)`:**
  - Determina el tipo de contenido del archivo basado en la extensión del archivo.

##### Manejo de Archivos

- **`readFileData(File file, int fileLength)`:**
  - Lee el contenido del archivo estático y lo envía al cliente.

##### Uso

1. **Configuración de Archivos Estáticos:**
   Configura la ubicación de los archivos estáticos con:
   ```java
   SimpleWebServer.staticfiles("classes/webroot");
   ```

2. **Registro de Servicios:**
   Registra un servicio personalizado con:
   ```java
   SimpleWebServer.get("/example", (req, resp) -> "Hello from the service");
   ```

3. **Ejecución:**
   Ejecuta el servidor con:
   ```java
   java com.eci.SimpleWebServer
   ```
   ## Autores
   
   Daniel Santiago Torres Acosta [https://github.com/RulosS290](https://github.com/RulosS290)

   ## Agradecimientos
   Daniel Benavides
   






