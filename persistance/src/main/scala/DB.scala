import java.util.UUID

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

object DB {

  val tableName = "users"

  val db = Database.forConfig("postgres.properties")

  val createNeededTable = {
    sqlu"""
        CREATE TABLE IF NOT EXISTS #$tableName (
            id CHARACTER VARYING PRIMARY KEY,
            email CHARACTER VARYING NOT NULL,
            password CHARACTER VARYING NOT NULL
        );
  """
  }

  db.run(createNeededTable)

  def create(row: SignUpRow)(implicit ex: ExecutionContext) = {
    val q = sqlu"""
          INSERT INTO #$tableName VALUES ('#${row.id}', '#${row.email}', '#${row.password}')
          ON CONFLICT DO NOTHING;
        """
    db.run(q)
  }

  def get(id: UUID)(implicit ex: ExecutionContext) = {
    val q = sql"""
               SELECT * FROM #$tableName WHERE id = '#${id.toString}';
        """.as[(String, String, String)].flatMap {
      case Vector() => DBIO.failed(new Throwable(s"No user with id $id"))
      case Vector(el) => DBIO.successful(el)
      case v => DBIO.failed(new Throwable(s"There are ${v.length} users with this id, but supposed to be one"))
    }
    db.run(q)
  }

}
