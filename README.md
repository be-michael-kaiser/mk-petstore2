# mk-petstore2

A small, self-contained pet store application built with **Vaadin Flow 24** and **Spring Boot 3.5**.
Unlike the sibling `mk-petstore` project (which is a UI on top of the remote Swagger Petstore API),
this app owns its own data via **Spring Data JPA** and an in-memory **H2** database.

## Features

- Pet CRUD: create, edit and delete pets from a Vaadin `Grid` with a modal editor dialog
- Search and filter by name/category (free text) and by availability status
- Bean-validated form (`@NotBlank`, `@DecimalMin`, `@Size`) wired through `BeanValidationBinder`
- Sample data seeded on first startup
- Unit tests for the service layer and the view wiring

## Requirements

- Java 25
- Maven 3.9+

## Running

```bash
mvn spring-boot:run
```

Then open <http://localhost:8081>.

## Testing

```bash
mvn test
```

## Production build

```bash
mvn -Pproduction package
java -jar target/mk-petstore2-1.0.0.jar
```

## Configuration

| Property | Default | Description |
| --- | --- | --- |
| `server.port` | `8081` | HTTP port (8081 avoids clashing with `mk-petstore`) |
| `petstore.sample-data` | `true` | Seed demo pets when the database is empty |
| `spring.datasource.url` | `jdbc:h2:mem:petstore` | Swap for a file/Postgres URL to persist data |

## Project layout

```
src/main/java/com/mk/petstore2/
├── MkPetstore2Application.java   Spring Boot entry point + Vaadin @Theme
├── model/Pet.java                JPA entity with validation constraints
├── model/PetStatus.java          AVAILABLE / PENDING / SOLD
├── repository/PetRepository.java Search + distinct-category queries
├── service/PetService.java       Transactional facade used by the UI
├── service/SampleDataLoader.java Seeds demo pets on an empty database
└── views/
    ├── MainLayout.java           AppLayout shell with branded header
    ├── PetsView.java             Grid, toolbar filters, row actions
    └── PetEditorDialog.java      Modal create/edit form
```
