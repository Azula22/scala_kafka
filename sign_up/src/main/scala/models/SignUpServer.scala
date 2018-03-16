package models

import java.util.UUID

case class SignUpServer(email: String, password: String, id: String)

object SignUpServer {

  def apply(s: SignUpClient): SignUpServer =
    SignUpServer(s.email, s.password, UUID.randomUUID().toString)

}
