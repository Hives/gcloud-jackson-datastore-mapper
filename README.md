# Datastore data class/entity mapper

Maps Kotlin data classes to gcloud datastore entities, and vice versa

Uses Jackson to map the data object to a JsonNode so we can easily iterate over
the fields when converting to an Entity. When converting from an Entity to 
the data class we iterate over the Entity properties and build up a JsonNode,
then use Jackson to convert to the class.

### TODO

- More property types
  - `enum` classes
  - other types of numbers?
- Collections (`List`, `Set`, `Map`...?)
- Handle sealed classes automatically?
- Indexes?
