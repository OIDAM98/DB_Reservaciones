package modelTest

import cats.effect.IO

import doobie.scalatest._
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import doobie.implicits._

import org.scalacheck.Gen
import org.scalacheck.Prop.BooleanOperators
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import reservaciones.model.{CursoActivo, CursosActivosModel}

import scala.concurrent.ExecutionContext

object DB {
  implicit val cs = IO.contextShift(ExecutionContext.global)
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  def executeQuery[A](query: Query0[A]) = query.option.transact(xa).unsafeRunSync

  def executeListQuery[A](query: Query0[A]) = query.to[List].transact(xa).unsafeRunSync

  def executeUpdate( up: Update0) = up.withUniqueGeneratedKeys[CursoActivo]("clave", "secc", "titulo").transact(xa).unsafeRunSync
}

class CursosActivosOpTest extends FunSpec with Matchers with GivenWhenThen {
  describe("CursosActivos Model"){

    it("get all Active Courses") {
      val res = DB.executeListQuery(CursosActivosModel.getAllActiveCourses())
      res should not be empty
    }

    it("get all current Active Courses") {
      val res = DB.executeListQuery(CursosActivosModel.getAllCurrentActiveCourses())
      res should not be empty
    }

    it("find a specific Curso Activo") {
      Given("a specific Curso Activo to search")
      val s = CursoActivo("#7f0", 3, "4173 Raven Park")
      When("the Curso Activo is retrieved")
      val res = DB.executeQuery(CursosActivosModel.findCursoActivo(s))
      Then("the result should be of type Some(CursoActivo)")
      res shouldBe defined
      And("contain the specified Curso Activo")
      res shouldBe Some(s)
    }

    it("find Cursos Activos by a specific Clave") {
      Given("a Clave to search Cursos Activos by")
      val c = "#7f0"
      When("the List of Cursos Activos by Clave is retrieved")
      val res = DB.executeListQuery(CursosActivosModel.findCursosActivoByClave(c))
      Then("the resuslt should be a non empty List")
      res should not be empty
      res shouldBe a [List[_]]
      And("of type Cursos Activos")
      res.head shouldBe a [CursoActivo]

    }

    it("find Cursos Activos by a specific Seccion"){
      Given("a Seccion to search Cursos Activos by")
      val s = 10
      When("the List of Cursos Activos by Seccion is retrieved")
      val res = DB.executeListQuery(CursosActivosModel.findCursosActivoBySecc(s))
      Then("the resuslt should be a non empty List")
      res should not be empty
      res shouldBe a [List[_]]
      And("of type Cursos Activos")
      res.head shouldBe a [CursoActivo]
    }

    it("find Cursos Activos by a specific Periodo (Titulo)"){
      Given("a Periodo (its Titulo) to search Cursos Activos by")
      val t = "4173 Raven Park"
      When("the List of Cursos Activos by Periodo is retrieved")
      val res = DB.executeListQuery(CursosActivosModel.findCursosActivoByPeriodo(t))
      Then("the resuslt should be a non empty List")
      res should not be empty
      res shouldBe a [List[_]]
      And("of type Cursos Activos")
      res.head shouldBe a [CursoActivo]
    }

    it("insert a Curso Activo to the Database"){
      Given("a certain Curso Activo to insert into the Database")
      val ins = CursoActivo("#d86", 109, "5 Dakota Way")
      When("the Curso Activo is correctly inserted")
      DB.executeUpdate(CursosActivosModel.insertCursoActivo(ins))
      Then("searching for that Curso Activo should return that Curso Activo")
      DB.executeQuery(CursosActivosModel.findCursoActivo(ins)) shouldBe Some(ins)
    }


  }
}


class CursosActivosModelQueriesTest extends FunSuite with Matchers with IOChecker {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  test("Typecheck getAllActiveCourses") {
    check(CursosActivosModel.getAllActiveCourses())
  }

  test("Typeecheck getCurrentActiveCourses") {
    check(CursosActivosModel.getAllCurrentActiveCourses())
  }

  test("Typecheck findCursoActivo") {
    check(CursosActivosModel.findCursoActivo(CursoActivo("hola", 0, "hoy")))
  }

  test("Typecheck findCursosActivosByClave") {
    check(CursosActivosModel.findCursosActivoByClave("Hola"))
  }

  test("Typecheck findCursosActivosBySecc") {
    check(CursosActivosModel.findCursosActivoBySecc(0))
  }

  test("Typecheck findCursosActivosByPeriodo") {
    check(CursosActivosModel.findCursosActivoByPeriodo("Hoy"))
  }

  test("Typecheck insertCursoActivo") {
    check(CursosActivosModel.insertCursoActivo(CursoActivo("hola", 0, "Hoy")))
  }
}

class SalonesCheckPropierties extends PropSpec with PropertyChecks with Matchers {

  property("CursosActivos es compatible con la base de datos (Restricciones de Dominio)") {
    forAll(
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
}


