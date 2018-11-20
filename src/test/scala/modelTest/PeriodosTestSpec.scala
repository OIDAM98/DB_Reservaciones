package modelTest

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.scalatest._
import doobie.util.query.Query0
import doobie.util.update.Update0
import doobie.implicits._
import java.sql.Timestamp
import java.time.LocalDateTime

import org.scalacheck.Gen
import org.scalacheck.Prop.BooleanOperators
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import reservaciones.model.{Periodo, PeriodosModel}

import scala.Some
import scala.concurrent.ExecutionContext

object DB {
  implicit val cs = IO.contextShift(ExecutionContext.global)
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  def executeQuery[A](query: Query0[A]) = query.option.transact(xa).unsafeRunSync

  def executeListQuery[A](query: Query0[A]) = query.to[List].transact(xa).unsafeRunSync

  def executeUpdate( up: Update0) = up.withUniqueGeneratedKeys[Periodo]("titulo", "fechainicio", "fechafin").transact(xa).unsafeRunSync
}

class PeriodosModelOpTest extends FunSpec with Matchers with GivenWhenThen {

  describe("PeriodosModel") {
    it("get all Periodos from the Database") {
      val all = DB.executeListQuery(PeriodosModel.getAllPeriods())
      all should not be empty
    }

    it("get the current Periodo from the Database") {
      val curr = DB.executeQuery(PeriodosModel.getCurrentPeriod())
      curr shouldBe defined
      curr shouldBe a [Some[_]]
    }

    it("find a specific Periodo from the Database"){
      Given("a certain Periodo to search")
      val s = Periodo("4173 Raven Park", Timestamp.valueOf("2021-11-22"), Timestamp.valueOf("2019-11-28"))
      When("the Periodo is obtained")
      val res = DB.executeQuery(PeriodosModel.findPeriodo(s))
      Then("the result should be of type Some(Periodo)")
      res shouldBe defined
      res shouldBe Some(s)
    }

    it("find Periodos by Fecha Inicio") {
      Given("a certain Fecha Inicio to search")
      val f = Timestamp.valueOf("2021-11-22")
      When("the list of Periodos that have that Fecha Inicio is obtained")
      val res = DB.executeListQuery(PeriodosModel.findPeriodosByIni(f))
      Then("the resulting list should not be empty")
      res should not be empty

    }

    it("find Periodos by Fecha Final") {
      Given("a certain Fecha Final to search")
      val f = Timestamp.valueOf("2019-11-28")
      When("the list of Periodos that have that Fecha Final is obtained")
      val res = DB.executeListQuery(PeriodosModel.findPeriodosByFin(f))
      Then("the resulting list should not be empty")
      res should not be empty
    }

    it("insert a Periodo to the Database") {
      Given("a certain Periodo to insert to the Database")
      val ins = Periodo("Testing 101", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
      When("the Periodo is correctly inserted")
      val res = DB.executeUpdate(PeriodosModel.insertPeriodo(ins))
      Then("searching for that Periodo should result that Periodo")
      val s = DB.executeQuery(PeriodosModel.findPeriodo(ins))
      res shouldBe s
    }

  }

}

class PeriodosModelQueriesTest extends FunSuite with Matchers with IOChecker {

  implicit val cs = IO.contextShift (ExecutionContext.global)
  val transactor = Transactor.fromDriverManager[IO] (
  "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  test("Typecheck getAllPeriods"){
    check(PeriodosModel.getAllPeriods())
  }

  test("Typecheck getCurrentPeriod") {
    check(PeriodosModel.getCurrentPeriod())
  }

  test("Typecheck findPeriodo") {
    check(PeriodosModel.findPeriodo(Periodo("hola", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()))))
  }

  test("Typecheck findPeriodosByTitulo") {
    check(PeriodosModel.findPeriodosByTitulo("hola"))
  }

  test("Typecheck findPeriodosByFechaIni") {
    check(PeriodosModel.findPeriodosByIni(Timestamp.valueOf(LocalDateTime.now())))
  }

  test("Typecheck findPeriodosByFechaFin") {
    check(PeriodosModel.findPeriodosByFin(Timestamp.valueOf(LocalDateTime.now())))
  }

}

//TODO: finish PeriodoCheckProperties (Missing generating Timestamps!)

/*
object PeriodoCheckPropierties extends Properties("Perido Simple") {
  property("Periodo es compatible con la base de datos (Restricciones de Dominio)") = ???
}*/
