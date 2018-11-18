package modelTest

import cats.effect.IO
import doobie.specs2._
import doobie.util.transactor.Transactor
import org.scalacheck.{Gen, Prop, Properties}
import org.scalacheck.Prop.BooleanOperators
import org.specs2.mutable.Specification
import reservaciones.model.Curso
import reservaciones.model.CursosModel

import scala.concurrent.ExecutionContext

object CursosModelQueriesTest extends Specification with IOChecker {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  "Typecheck getAllCourses" in {
    check(CursosModel.getAllCourses())
  }

  "Typecheck findCurso" in {
    check(CursosModel.findCurso(Curso("hola", 10, "Hola", "Zechinelli")))
  }

  "Typecheck findCursoBySecc" in {
    check(CursosModel.findCurosBySecc(10))
  }

  "Typecheck findCursosByTitulo" in {
    check(CursosModel.findCursosByTitulo("hola"))
  }

  "Typecheck findCursosByTitulo" in {
    check(CursosModel.findCursosByTitulo("Hola"))
  }

  "Typecheck findCursosByProfesor" in {
    check(CursosModel.findCursosByProfesor("Zechinelli"))
  }

  "Typecheck insertCurso" in {
    check(CursosModel.insertCurso(Curso("hola", 10, "Hola", "Zechinelli")))
  }

}

object CursoCheckPropierties extends Properties("Curso Simple") {
  property("Cursos es compatible con la base de datos (Restricciones de Dominio)") =
    Prop.forAll(
      Gen.alphaStr.retryUntil(_.length <= 10 ),
      Gen.posNum[Int],
      Gen.alphaUpperStr.retryUntil(_.length <= 30),
      Gen.alphaUpperStr.retryUntil(_.length <= 30)) {
      (clave: String, secc: Int, nom: String, prof: String) => {
        (clave.length <= 10 && nom.length <= 30 && prof.length <= 30) ==> {
          val cur = Curso(clave, secc, nom, prof)
          cur.clave == clave
          cur.secc == secc
          cur.titulo == nom
          cur.prof == prof
        }
      }
    }
}