# DESIGN — Microblogging

Objetivo
--------
Documento high-level con la arquitectura, decisiones de diseño, opciones de escalado y justificaciones para una plataforma de microblogging orientada a lecturas.

Resumen de la arquitectura
--------------------------
- Estilo: Hexagonal / Ports & Adapters (core independiente de infra).
- Componentes:
    - API (Spring Boot controllers): expone REST endpoints.
    - Core: reglas de negocio (publicar, eliminar, seguir, timeline).
    - SPI: persistencia (MongoDB), cache (Redis), broker (Kafka).
      - DB primario: MongoDB para prototipo; en producción considerar multiples bases de datos, Cassandra o un store optimizado, para writes y reads masivos.
      - Cache: Redis para hot timelines y usuarios con alto tráfico.
      - Message Broker: Kafka para fan-out asíncrono y procesamiento en background.
      - Infra: Docker -> Kubernetes (GKE/EKS/AKS) para despliegue, Ingress + LB.

Decisiones clave y justificación
--------------------------------
1. Lenguaje / Framework
    - Java 25 + Spring Boot: rápido desarrollo, ecosistema maduro (Spring Data, Validation, Actuator).

2. Base de datos
    - Prototipo: MongoDB (document store). Ventajas: rápido para iterar, buena integración con Spring Data.
    - Producción/escala masiva: Cassandra o ScyllaDB para altísima ingestión y replicación eficiente por partición, o una combinación (wide-column store, metadata en RDB o document DB).

3. Optimización para lecturas (timelines)
    - Estrategias:
        - Fan-out-on-write: cuando se publica, push a timelines de cada follower (Redis or materialized timelines in DB). Pros: lecturas rápidas; Cons: writes costosos y almacenamiento duplicado.
        - Fan-out-on-read: al solicitar timeline, leer las últimas publicaciones de cada followee y mergear. Pros: writes baratos; Cons: lecturas más lentas.
    - Recomendación híbrida:
        - Para la mayoría: fan-out-on-write o cachear timelines periódicamente.
        - Para cuentas con muchos followers (celebridades): fan-out-on-read.
    - Uso de Kafka:
        - Producer en el flujo de creación de publicaciones.
        - Worker(s) consumen y materializan timelines (fan-out) a Redis/DB.

4. Cache
    - Redis para:
        - Timelines precomputados (sorted sets).
        - Hot publications / user profiles.
    - Estrategia de expiración y invalidación (LRU y TTL para elementos de timeline).

5. Paginación y consistencia
    - Usar cursor-based pagination (cursor = timestamp+id) para eficiencia y continuidad en lecturas.
    - Consistencia eventual para timelines materializados (es aceptable si la UI tolera pequeñas latencias).

6. Autorización y seguridad
    - JWT + OAuth2 / OpenID Connect.
    - Rate limiting y protección contra abuso.
    - Validación de inputs (jakarta.validation).

7. Observability
    - Logs estructurados (JSON).
    - Métricas: Prometheus + Grafana.
    - Tracing distribuido.
    - Alerting: SLOs y alertas en errores y latencias.

8. Infra / Deployment
    - Docker images para la app.
    - Kubernetes:
        - Deployments (replicas), HPA basado en CPU/latency.
    - CI/CD:
        - Build image, run tests, push image, deploy to staging, smoke tests, promote to prod.

9. Testing
    - Unit tests para use-cases.
    - Integration tests con Testcontainers (Mongo).
    - Contract tests para adapters.

Flujo simplificado (crear publicación)
-------------------------------------
1. Client POST /publication
2. Controller llama al core `PublicationService`
3. Seersiste publication via PublicationsSpi
4. Publicación escrita en DB
5. Evento enviado para fan-out y notificaciones
6. Worker(s) consumen evento y materializan timelines / actualizan cache Redis

Escalado a millones de usuarios (plan)
-------------------------------------
- Particionar datos por `userId` (sharding).
- Offload reads to caches y CDN-like layer for static content.
- Horizontal scale de stateless services detrás de LB.
- Usar Kafka para desacoplar y manejar picos de tráfico.
- Considerar retención de datos (TTL para eventos antiguos) y almacenamiento frío para historiales.

Anexos
------
- Recomendación DB por carga:
    - Write-heavy (millones writes/s): Cassandra/Scylla
    - Read-heavy with complex queries: ElasticSearch (search) + primary store

Suposiciones
-----------
- Usuarios ya existen y son siempre válidos.
- No hay front-end ni manejo de multimedia (solo texto).
- Latencia de lectura priorizada frente a latencia de escritura para la mayoría de usuarios.
