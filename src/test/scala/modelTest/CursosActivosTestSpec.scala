package modelTest

import cats.effect.IO
import doobie.specs2._
import doobie.util.transactor.Transactor
import org.scalacheck.{Gen, Prop, Properties}
import org.scalacheck.Prop.BooleanOperators
import org.specs2.mutable.Specification
import reservaciones.model.{CursoActivo, CursosActivosModel}

import scala.concurrent.ExecutionContext

object CursosActivosModelQueriesTest extends Specification with IOChecker {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  "Typecheck getAllActiveCourses" in {
    check(CursosActivosModel.getAllActiveCourses())
  }

  "Typeecheck getCurrentActiveCourses" in {
    check(CursosActivosModel.getAllCurrentActiveCourses())
  }

  "Typecheck findCursoActivo" in {
    check(CursosActivosModel.findCursoActivo(CursoActivo("hola", 0, "hoy")))
  }

  "Typecheck findCursosActivosByClave" in {
    check(CursosActivosModel.findCursosActivoByClave("Hola"))
  }

  "Typecheck findCursosActivosBySecc" in {
    check(CursosActivosModel.findCursosActivoBySecc(0))
  }

  "Typecheck findCursosActivosByPeriodo" in {
    check(CursosActivosModel.findCursosActivoByPeriodo("Hoy"))
  }

  "Typecheck insertCursoActivo" in {
    check(CursosActivosModel.insertCursoActivo(CursoActivo("hola", 0, "Hoy")))
  }
}

object CursoActivoCheckPropierties extends Properties("Curso Activo Simple") {
  property("CursosActivos es compatible con la base de datos (Restricciones de Dominio)") =
    Prop.forAll(
      Gen.alphaStr.retryUntil(_.length <= 10 ),
      Gen.posNum[Int],
      Gen.alphaUpperStr.retryUntil(_.length <= 20)) {
      (clave: String, secc: Int, titulo: String) => {
        (clave.length <= 10 && titulo.length <= 20) ==> {
          val cur = CursoActivo(clave, secc, titulo)
          cur.clave == clave
          cur.secc == secc
          cur.titulo == titulo
        }
      }
    }
}
