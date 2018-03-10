package models

import java.util.UUID

case class SignUpServer(email: String, password: String, id: UUID)

object SignUpServer {

  def apply(s: SignUpClient) = SignUpServer(s.email, s.password, UUID.randomUUID())

}
