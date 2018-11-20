package modelTest

import cats.effect.IO

import doobie.util.transactor.Transactor
import doobie.scalatest._
import doobie.util.query.Query0
import doobie.util.update.Update0
import doobie.implicits._

import reservaciones.model.{Curso, CursosModel}

import org.scalacheck.Gen
import org.scalacheck.Prop.BooleanOperators

import org.scalatest._
import org.scalatest.prop.PropertyChecks

import scala.concurrent.ExecutionContext



object DB {
  implicit val cs = IO.contextShift(ExecutionContext.global)
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  def executeQuery[A](query: Query0[A]) = query.option.transact(xa).unsafeRunSync

  def executeListQuery[A](query: Query0[A]) = query.to[List].transact(xa).unsafeRunSync

  def executeUpdate( up: Update0): Unit = up.withUniqueGeneratedKeys[Curso]("clave","secc","titulo", "prof").transact(xa).unsafeRunSync
}

class CursosModelOpTest extends FunSpec with Matchers with GivenWhenThen {

  describe("Cursos Model") {
    it("get all Courses from the Database") {
      val all = DB.executeListQuery(CursosModel.getAllCourses())
      all should not be empty
    }

    it("find a specific Curso") {
      Given("a certain Curso to find")
      val s = Curso("#7f0", 3, "DTA", "Muriel Aslum")
      When("the Curso is obtained")
      val res = DB.executeQuery(CursosModel.findCurso(s))
      Then("the result should be of type Some(Curso)")
      s shouldBe defined
      s shouldBe Some(s)
    }

    it("find Cursos by Seccion") {
      Given("a certain Seccion to find")
      val secc = 10
      When("the list of Cursos of certain Seccion is obtained")
      val res = DB.executeListQuery(CursosModel.findCurosBySecc(secc))
      Then("the list should not be empty")
      res should not be empty
      And("should be a List")
      res shouldBe a [List[_]]
      And("should contain type Curso")
      res.head shouldBe a [Curso]
    }

    it("find Cursos by Titulo") {
      Given("a certain Titulo to find")
      val title = "DTA"
      When("the list of Cursos containing the Titulo is obtained")
      val res = DB.executeListQuery(CursosModel.findCursosByTitulo(title))
      Then("the list should not be empty")
      res should not be empty
      And("should be a List")
      res shouldBe a [List[_]]
      And("should contain type Curso")
      res.head shouldBe a [Curso]
    }

    it("find Cursos by Profesor") {
      Given("a certain Profesor to find")
      val prof = "Nolly Poone"
      When("the list of Cursos imparted by the Profesor is obtained")
      val res = DB.executeListQuery(CursosModel.findCursosByProfesor(prof))
      Then("the list should not be empty")
      res should not be empty
      And("should be a List")
      res shouldBe a [List[_]]
      And("should contain type Curso")
      res.head shouldBe a [Curso]
    }

    it("insert a Curso into the Database") {
      Given("a certain Curso to insert into the Database")
      val ins = Curso("basesdatos", 1, "Bases de Datos", "Zechinelli")
      When("the Curso is correctly inserted")
      DB.executeUpdate(CursosModel.insertCurso(ins))
      Then("searching for that Curso should return that Curso")
      DB.executeQuery(CursosModel.findCurso(ins)) shouldBe Some(ins)
    }

    it("delete a Curso from the Database") {
      Given("a certain Curso to delete from the Database")
      val del = Curso("basesdatos", 1, "Bases de Datos", "Zechinelli")
      When("the Curso is correctly deleted")
      DB.executeUpdate(CursosModel.deleteCurso(del))
      Then("searchign for that Curso should return None")
      DB.executeQuery(CursosModel.findCurso(del)) shouldBe None
    }

  }

}

class CursosModelQueriesTest extends FunSuite with Matchers with IOChecker {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  test("Typecheck getAllCourses") {
    check(CursosModel.getAllCourses())
  }

  test("Typecheck findCurso") {
    check(CursosModel.findCurso(Curso("hola", 10, "Hola", "Zechinelli")))
  }

  test("Typecheck findCursoBySecc") {
    check(CursosModel.findCurosBySecc(10))
  }

  test("Typecheck findCursosByTitulo") {
    check(CursosModel.findCursosByTitulo("hola"))
  }

  test("Typecheck findCursosByTitulo") {
    check(CursosModel.findCursosByTitulo("Hola"))
  }

  test("Typecheck findCursosByProfesor") {
    check(CursosModel.findCursosByProfesor("Zechinelli"))
  }

  test("Typecheck insertCurso") {
    check(CursosModel.insertCurso(Curso("hola", 10, "Hola", "Zechinelli")))
  }

}

class SalonesCheckPropierties extends PropSpec with PropertyChecks with Matchers{

  property("Cursos es compatible con la base de datos (Restricciones de Dominio)") {
    forAll(
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

}
