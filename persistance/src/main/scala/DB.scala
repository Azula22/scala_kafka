import org.mindrot.jbcrypt.BCrypt
import slick.jdbc.PostgresProfile.api._

object DB {

  val tableName = "users"

  val db = Database.forConfig("postgres.properties")

  val createNeededTable =
    sqlu"""
        CREATE TABLE IF NOT EXISTS '#$tableName' (
            id uuid PRIMARY KEY,
            email CHARACTER VARYING NOT NULL UNIQUE,
            password CHARACTER VARYING NOT NULL
        );
  """
  db.run(createNeededTable)

  def create(row: SignUpRow) = {
    val encryptedPassword = BCrypt.hashpw(row.password, BCrypt.gensalt())
    val q = sqlu"""
          INSERT INTO #$tableName VALUES (#${row.id}, #${row.email}, #$encryptedPassword)
          ON CONFLICT DO NOTHING;
        """
    db.run(q)
  }

}
