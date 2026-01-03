# Microblogging — README

Resumen
-------
Implementación de referencia de una plataforma de microblogging. Esta guía explica cómo levantar la aplicación, ejecutar tests y probar los endpoints. La app está pensada como back-end; no hay front-end.

Requisitos
---------
- Java 25
- Docker & docker-compose
- Git
- (Opcional) Postman o herramienta similar

Archivos importantes
-------------------
- `src/` — código Java (Spring Boot)
- `Dockerfile`, `docker-compose.yml`
- `build.gradle`, `gradlew`
- `README.md`, `DESIGN.md`
- `microblogging.postman_collection.json`

Levantar la aplicación (local, con Gradle)
------------------------------------------
1. Clonar:
   git clone https://github.com/JoseGC789/Microblogging.git
   cd Microblogging

2. Levantar una instancia de Mongo localmente (si no usas docker):
   docker run -d --name mongo -p 27017:27017 mongo:6

3. Ejecutar la app:
   ./gradlew bootRun

4. La app estará en: http://localhost:8080

Levantar con Docker (recomendado)
---------------------------------
1. Levantar servicios:
   docker-compose up --build

2. Parar:
   docker-compose down

Ejecutar tests
--------------
./gradlew test

API — Endpoints (sugeridos / compatibles)
-----------------------------------------
Nota: adapta las rutas si el código actual usa otras. Estos endpoints reflejan la API mínima esperada.

- POST /users
    - Descripción: crea un usuario.
    - Body JSON:
      -   ```
          curl --location 'localhost:8080/microblogging/users' \
          --header 'Content-Type: application/json' \
          --data '{
          "username":"Rylan.Grady98"
          }'
          ```
    - Respuesta: 200 OK
      - ```
        {
            "id": "69599d4b9c1173a590ae4e7b",
            "username": "Shaun_Satterfield92"
        }
        ```
        
- POST /publications
    - Descripción: crea una publicación.
    - Body JSON:
        - ```
            curl --location 'localhost:8080/microblogging/publications' \
            --header 'Content-Type: application/json' \
            --data '{
            "authorId":"69599d4b9c1173a590ae4e7b",
            "content":"Laudantium modi ut quia eos aut nostrum. Libero nihil repudiandae. Deleniti esse atque ut velit tempore sed molestias. Dolore aliquam repellendus itaque omnis modi vel. Officiis commodi enim dignissimos hic vel quibusdam rerum soluta excepturi."
            }'
          ```
    - Respuesta: 200 OK
      - ```
        {
        "id": "69599ddc9c1173a590ae4e7c",
        "author": {
            "id": "69599d4b9c1173a590ae4e7b",
            "username": "Shaun_Satterfield92"
        },
        "content": "Eos cum ut id dolor saepe id nulla. Aperiam dolore voluptatibus consectetur consequatur qui magni provident id repellendus. Porro tempora nemo voluptates nostrum assumenda aut repellat sequi eum. Enim sunt eius qui sit neque est."
        }
        ```

- POST /follows
    - Descripción: seguir a otro usuario.
    - Body JSON:
        - ```
          curl --location --request PUT 'localhost:8080/microblogging/users/follow' \
          --header 'Content-Type: application/json' \
          --data '{
            "follower": "69599d4b9c1173a590ae4e7b",
            "followee": "6959a00f95bca20bc6c94493"
          }'
          ```
    - Respuesta: 200 OK
        - ```
          {
              "id": "6959a02db9781ad4533f34cf",
              "follower": {
                  "id": "69599d4b9c1173a590ae4e7b",
                  "username": "Shaun_Satterfield92"
              },
              "followee": {
                  "id": "6959a00f95bca20bc6c94493",
                  "username": "Demarcus.Schultz61"
              }
          }
          ```

- GET /timeline?owner={id}&cursor={instant}
    - Descripción: devuelve el timeline del usuario (cursor-based pagination).
    - Body JSON:
        - ```
          curl --location 'localhost:8080/microblogging/timelines?owner=6958a0ca4e9c63b41d7cefd5&cursor=2026-01-03T05%3A08%3A55.000-00%3A00'
          ```
    - Respuesta: 200 OK
        - ```
          [
              {
                  "publicationId": "6958a4662ff2e38ea57ff4bd",
                  "owner": "6958a0ca4e9c63b41d7cefd5",
                  "authorId": "69589f940e7f5b006c7bd682",
                  "authorUsername": "Elise43",
                  "content": "Loremp ipsum 1",
                  "createdOn": "2026-01-03T05:08:54.898Z"
              },
              {
                  "publicationId": "6958a4662ff2e38ea57ff4b5",
                  "owner": "6958a0ca4e9c63b41d7cefd5",
                  "authorId": "69589f940e7f5b006c7bd682",
                  "authorUsername": "Elise43",
                  "content": "Loremp ipsum 2",
                  "createdOn": "2026-01-03T05:08:54.425Z"
              },
              {
                  "publicationId": "6958a4412ff2e38ea57ff4ad",
                  "owner": "6958a0ca4e9c63b41d7cefd5",
                  "authorId": "69589f940e7f5b006c7bd682",
                  "authorUsername": "Elise43",
                  "content": "Loremp ipsum 3",
                  "createdOn": "2026-01-03T05:08:17.654Z"
              }
          ]
          ```
Manejo de usuarios
------------------
Se asume que los usuarios son válidos y que el servicio recibirá algún identificador de usuario. En producción siempre validar tokens/JWT.
