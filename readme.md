# Simple JSON Database (SJD)

## Description

Simple JSON Database (SJD) is a lightweight library for working with JSON files as a database. It provides functionality for creating, reading, updating, and deleting records (CRUD), as well as supporting transactions, change logging, backups, and automatic saving of data.

The library is designed for use in Kotlin/Java projects where a simple data storage solution is required without the need to set up a full-fledged database.

---

## Key Features

1. **CRUD Operations**:
    - Insert, update, delete, and search for records.
    - Support for filtering data using lambda expressions.

2. **Transactions**:
    - Ability to perform multiple operations within a single transaction.
    - Automatic rollback upon errors.

3. **Backups**:
    - Creating backups with the ability to restore data.
    - Automatic creation of backups at specified intervals.

4. **Change Logging**:
    - Logging all operations (INSERT, UPDATE, DELETE) with timestamps and unique record identifiers.

5. **Automatic Saving**:
    - Caching data in memory with periodic synchronization to the file system.

6. **Annotation Support**:
    - `@SJDDocument` annotation for defining collection names.
    - `@UniqueKey` annotation for specifying unique entity keys.

7. **Custom Serialization/Deserialization**:
    - Support for data types such as `LocalDateTime` and `Duration` using Jackson.

---

## Usage

### 1. Configuration Setup

Create an `SJDConfig` object to configure paths and database parameters:

```kotlin
val config = SJDConfig(
    basePath = "./data", // Base path for storing data
    backupIntervalHours = 24, // Backup interval (in hours)
    cacheInterval = 5000L // Auto-save interval (in milliseconds)
)
```

### 2. Entity Creation

Define an entity class with annotations:

```kotlin
@SJDDocument(collectionName = "users")
data class User(
    @UniqueKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val age: Int
)
```

### 3. Manager Initialization

Create an instance of `JsonEntityManager` to work with the data:

```kotlin
val entityManager = JsonEntityManager(User::class, config)
```

### 4. CRUD Operations

#### Inserting a Record
```kotlin
val user = User(name = "John Doe", age = 30)
entityManager.insert(user)
```

#### Searching for Records
```kotlin
// Find all records
val allUsers = entityManager.findAll()

// Find records by condition
val usersOver30 = entityManager.findBy { it.age > 30 }
```

#### Updating a Record
```kotlin
val updatedUser = user.copy(age = 31)
entityManager.update(updatedUser)
```

#### Deleting a Record
```kotlin
entityManager.delete(user)
```

### 5. Transactions

Perform multiple operations within a single transaction:

```kotlin
entityManager.runInTransaction {
    entityManager.insert(User(name = "Alice", age = 25))
    entityManager.insert(User(name = "Bob", age = 28))
}
```

### 6. Backups and Restoration

#### Creating a Backup
```kotlin
val isSuccess = entityManager.createBackup()
if (isSuccess) println("Backup created successfully!")
```

#### Restoring from a Backup
```kotlin
val timestamp = entityManager.listBackups().last()
val isRestored = entityManager.restoreBackup(timestamp)
if (isRestored) println("Data restored successfully!")
```

---

## Dependencies

Add the dependencies to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
}
```

---

## License

This project is distributed under the MIT license. For details, see the [LICENSE](LICENSE) file.

---

## Author

Author: k1ll1n
GitHub: https://github.com/k1ll1n/SimpleJsonDatabase

---

## Notes

- Ensure you have write permissions for the specified directory.
- For large data volumes, consider using more robust solutions like SQL or NoSQL databases.
