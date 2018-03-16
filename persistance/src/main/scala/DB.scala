import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

object DB {

  val tableName = "users"

  val db = Database.forConfig("postgres.properties")

  val createNeededTable = {
    sqlu"""
        CREATE TABLE IF NOT EXISTS #$tableName (
            id CHARACTER VARYING PRIMARY KEY,
            email CHARACTER VARYING NOT NULL UNIQUE,
            password CHARACTER VARYING NOT NULL
        );
  """
  }

  db.run(createNeededTable)

  def create(row: SignUpRow)(implicit ex: ExecutionContext) = {
    println(row)
    val q = sqlu"""
          INSERT INTO #$tableName VALUES ('#${row.id}', '#${row.email}', '#${row.password}')
          ON CONFLICT DO NOTHING;
        """
    val r = db.run(q)
    r.failed.foreach(err => println(err.getMessage))
    r
  }

}
