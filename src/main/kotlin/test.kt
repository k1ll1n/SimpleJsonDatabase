import com.araksis.sjd.core.SJDConfig
import com.araksis.sjd.core.JsonEntityManager
import com.araksis.sjd.annotations.SJDDocument
import com.araksis.sjd.models.SJDCollection
import com.github.javafaker.Faker
import java.util.*


suspend fun main() {
    /*val userDao = SimpleORM(PlayerData::class, "./TestDatabase")
    val tt = mutableMapOf(
        UUID.randomUUID() to Pair(LocalDateTime.now(), Duration.ZERO),
    )
    //userDao.insert(PlayerData("User", "ffdgdfgdgf", LocalDateTime.now(), null, tt))

    val t = userDao.findBySync { it.id == UUID.fromString("325fef41-347e-4c83-8eac-ac81a1fc0cc1") }
    //userDao.delete(t)
    println(t?.nickname)*/

    val faker = Faker(Locale("ru"))

    val sjdConfig = SJDConfig("./TestDatabase")
    val userDao = JsonEntityManager(User::class, sjdConfig)

    userDao.runInTransaction {
        userDao.insert(User(faker.name().fullName(), "subikrus@gmail4.com"))
        userDao.delete(User(null!!, "UpdatedData"))
    }

    println(userDao.findBy { it.email == "subikrus@gmail1.com" })

    userDao.commitChanges()
}

@SJDDocument("users")
data class User(
    val name: String,
    val email: String,
) : SJDCollection()
