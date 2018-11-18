package modelTest

import cats.effect.IO
import doobie.specs2._
import doobie.util.transactor.Transactor
import org.scalacheck.{Gen, Prop, Properties}
import java.sql.Date
import org.specs2.mutable.Specification
import reservaciones.model.{Periodo, PeriodosModel}

import scala.concurrent.ExecutionContext

object PeriodosModelQueriesTest extends Specification with IOChecker {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  "Typecheck getAllPeriods" in {
    check(PeriodosModel.getAllPeriods())
  }

  "Typecheck getCurrentPeriod" in {
    check(PeriodosModel.getCurrentPeriod())
  }

  "Typecheck findPeriodo" in {
    check(PeriodosModel.findPeriodo(Periodo("hola", new Date(new java.util.Date().getTime), new Date(new java.util.Date().getTime))))
  }

  "Typecheck findPeriodosByTitulo" in {
    check(PeriodosModel.findPeriodosByTitulo("hola"))
  }

  "Typecheck findPeriodosByFechaIni" in {
    check(PeriodosModel.findPeriodosByIni(new Date(new java.util.Date().getTime)))
  }

  "Typecheck findPeriodosByFechaFin" in {
    check(PeriodosModel.findPeriodosByFin(new Date(new java.util.Date().getTime)))
  }

}

/*object PeriodoCheckPropierties extends Properties("Perido Simple") {
  property("Periodo es compatible con la base de datos (Restricciones de Dominio)") = ???
}*/
