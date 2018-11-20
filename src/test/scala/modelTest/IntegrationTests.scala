package modelTest

import java.sql.Timestamp
import java.time.LocalDateTime

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import reservaciones.model._
import org.scalatest.{Ignore, _}

import scala.concurrent.ExecutionContext

trait DBTest extends BeforeAndAfterEach { this: Suite =>
  var xa: Aux[IO, Unit] = _

  override def beforeEach(){
    implicit val cs = IO.contextShift(ExecutionContext.global)
    xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
    )
    super.beforeEach()
  }

  override def afterEach(){
    try super.afterEach()
    finally {
      xa = null
    }

  }

}

class SalonesModelOpTest extends FunSpec with Matchers with GivenWhenThen with DBTest {

  describe("SalonesModel") {
    it("get all Salones from the Database"){
      println("getAllSalones")
      val all = SalonesModel.getAllClassrooms().to[List].transact(xa).unsafeRunSync
      all should not be empty
    }

    it("find a specific Salon on the Database"){
      println("findSalon")
      Given("a specific Salon to search")
      val sal = Salon("awz-835", 60, "C")
      When("the Salon is obtained")
      val res = SalonesModel.findSalon(sal).option.transact(xa).unsafeRunSync
      Then("the result should be of type Some")
      res shouldBe defined
      And("should contain the Salon searched")
      res shouldBe Some(sal)
    }

    it("find all Salones by Tipo"){
      println("findSalonByTipo")
      Given("a specific tipo of Salones to find")
      val t = "C"
      When("the list of Salones of certain Tipo is obained")
      val res = SalonesModel.findSalonesByTipo(t).to[List].transact(xa).unsafeRunSync
      Then("the list should not be empty")
      res should not be empty
      And("should be a List")
      res shouldBe a [List[_]]
      And("should contain type Salon")
      res.head shouldBe a [Salon]
    }

    it("find all Salones by Capacidad") {
      println("findSalonesByCap")
      Given("a specific Capacidad of Salones to find")
      val cap = 50
      When("the list of Salones of certain Capacidad is obtained")
      val res = SalonesModel.findSalonesByCap(cap).to[List].transact(xa).unsafeRunSync
      Then("the list should not be empty")
      res should not be empty
      And("should be a List")
      res shouldBe a [List[_]]
      And("should contain type Salon")
      res.head shouldBe a [Salon]
    }

    it("insert a Salon to the Database"){
      println("insertSalon")
      Given("a Salon to insert")
      val ins = Salon("ia-205", 50, "C")
      When("the Salon is inserted correctly to the Database")
      SalonesModel.insertSalon(ins).withUniqueGeneratedKeys("idsalon", "capacidad", "tipo").transact(xa).unsafeRunSync
      Then("searching for that Salon should return that salon")
      SalonesModel.findSalon(ins).option.transact(xa).unsafeRunSync shouldBe Some(ins)
    }

    it("delete a Salon from the Database"){
      println("deleteSalon")
      Given("a Salon to insert")
      val del = Salon("ia-205", 50, "C")
      When("the Salon is deleted correctly from the Database")
      SalonesModel.deleteSalon(del).withUniqueGeneratedKeys("idsalon", "capacidad", "tipo").transact(xa).unsafeRunSync
      Then("searching for that Salon should return None")
      SalonesModel.findSalon(del).option.transact(xa).unsafeRunSync shouldBe None
    }

  }

}

class PeriodosModelOpTest extends FunSpec with Matchers with GivenWhenThen with DBTest {

  describe("PeriodosModel") {
    it("get all Periodos from the Database") {
      val all = PeriodosModel.getAllPeriods().to[List].transact(xa).unsafeRunSync
      all should not be empty
    }

    ignore("get the current Periodo from the Database") {
      val curr = PeriodosModel.getCurrentPeriod().option.transact(xa).unsafeRunSync
      curr shouldBe defined
      curr shouldBe a [Some[_]]
    }

    it("find a specific Periodo from the Database"){
      Given("a certain Periodo to search")
      val s = Periodo("4173 Raven Park", Timestamp.valueOf("2021-11-22 00:00:00.000000000"), Timestamp.valueOf("2019-11-28 00:00:00.000000000"))
      When("the Periodo is obtained")
      val res = PeriodosModel.findPeriodo(s).option.transact(xa).unsafeRunSync
      Then("the result should be of type Some(Periodo)")
      res shouldBe defined
      res shouldBe Some(s)
    }

    it("find Periodos by Fecha Inicio") {
      Given("a certain Fecha Inicio to search")
      val f = Timestamp.valueOf("2021-11-22 00:00:00.000000000")
      When("the list of Periodos that have that Fecha Inicio is obtained")
      val res = PeriodosModel.findPeriodosByIni(f).to[List].transact(xa).unsafeRunSync
      Then("the resulting list should not be empty")
      res should not be empty

    }

    it("find Periodos by Fecha Final") {
      Given("a certain Fecha Final to search")
      val f = Timestamp.valueOf("2019-11-28 00:00:00.000000000")
      When("the list of Periodos that have that Fecha Final is obtained")
      val res = PeriodosModel.findPeriodosByFin(f).to[List].transact(xa).unsafeRunSync
      Then("the resulting list should not be empty")
      res should not be empty
    }

    val today = Timestamp.valueOf(LocalDateTime.now())
    val tomorrow = Timestamp.valueOf(LocalDateTime.now().plusDays(1))

    it("insert a Periodo to the Database") {
      Given("a certain Periodo to insert to the Database")
      val ins = Periodo("Testing 101", today, tomorrow)
      When("the Periodo is correctly inserted")
      PeriodosModel.insertPeriodo(ins).withUniqueGeneratedKeys[Periodo]("titulo", "fechainicio", "fechafin").transact(xa).unsafeRunSync
      Then("searching for that Periodo should result that Periodo")
      val res = PeriodosModel.findPeriodo(ins).option.transact(xa).unsafeRunSync
      println("s")
      res shouldBe Some(ins)
    }

    it("delete a Periodo to the Database") {
      Given("a certain Periodo to delete from the Database")
      val del = Periodo("Testing 101", today, tomorrow)
      When("the Periodo is correctly deleted")
      PeriodosModel.deletePeriodo(del).withUniqueGeneratedKeys[Periodo]("titulo", "fechainicio", "fechafin").transact(xa).unsafeRunSync
      Then("searching for that Periodo should result in None")
      val res = PeriodosModel.findPeriodo(del).option.transact(xa).unsafeRunSync
      res shouldBe None
    }

  }

}

class CursosModelOpTest extends FunSpec with Matchers with GivenWhenThen with DBTest {

  describe("Cursos Model") {
    it("get all Courses from the Database") {
      val all = CursosModel.getAllCourses().to[List].transact(xa).unsafeRunSync
      all should not be empty
    }

    it("find a specific Curso") {
      Given("a certain Curso to find")
      val s = Curso("#7f0", 3, "DTA", "Muriel Aslum")
      When("the Curso is obtained")
      val res = CursosModel.findCurso(s).option.transact(xa).unsafeRunSync
      Then("the result should be of type Some(Curso)")
      res shouldBe defined
      res shouldBe Some(s)
    }

    it("find Cursos by Seccion") {
      Given("a certain Seccion to find")
      val secc = 10
      When("the list of Cursos of certain Seccion is obtained")
      val res = CursosModel.findCurosBySecc(secc).to[List].transact(xa).unsafeRunSync
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
      val res = CursosModel.findCursosByTitulo(title).to[List].transact(xa).unsafeRunSync
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
      val res = CursosModel.findCursosByProfesor(prof).to[List].transact(xa).unsafeRunSync
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
      CursosModel.insertCurso(ins).withUniqueGeneratedKeys[Curso]("clave","secc","titulo", "prof").transact(xa).unsafeRunSync
      Then("searching for that Curso should return that Curso")
      CursosModel.findCurso(ins).option.transact(xa).unsafeRunSync shouldBe Some(ins)
    }

    it("delete a Curso from the Database") {
      Given("a certain Curso to delete from the Database")
      val del = Curso("basesdatos", 1, "Bases de Datos", "Zechinelli")
      When("the Curso is correctly deleted")
      CursosModel.deleteCurso(del).withUniqueGeneratedKeys[Curso]("clave","secc","titulo", "prof").transact(xa).unsafeRunSync
      Then("searchign for that Curso should return None")
      CursosModel.findCurso(del).option.transact(xa).unsafeRunSync shouldBe None
    }

  }

}

class CursosActivosOpTest extends FunSpec with Matchers with GivenWhenThen with DBTest {

  describe("CursosActivos Model"){

    it("get all Active Courses") {
      val res = CursosActivosModel.getAllActiveCourses().to[List].transact(xa).unsafeRunSync
      res should not be empty
    }

    it("get all current Active Courses") {
      val res = CursosActivosModel.getAllCurrentActiveCourses().to[List].transact(xa).unsafeRunSync
      res shouldBe empty
    }

    it("find a specific Curso Activo") {
      Given("a specific Curso Activo to search")
      val s = CursoActivo("#7f0", 3, "4173 Raven Park")
      When("the Curso Activo is retrieved")
      val res = CursosActivosModel.findCursoActivo(s).option.transact(xa).unsafeRunSync
      Then("the result should be of type Some(CursoActivo)")
      res shouldBe defined
      And("contain the specified Curso Activo")
      res shouldBe Some(s)
    }

    it("find Cursos Activos by a specific Clave") {
      Given("a Clave to search Cursos Activos by")
      val c = "#7f0"
      When("the List of Cursos Activos by Clave is retrieved")
      val res = CursosActivosModel.findCursosActivoByClave(c).to[List].transact(xa).unsafeRunSync
      Then("the resuslt should be a non empty List")
      res should not be empty
      res shouldBe a [List[_]]
      And("of type Cursos Activos")
      res.head shouldBe a [CursoActivo]

    }

    it("find Cursos Activos by a specific Seccion"){
      Given("a Seccion to search Cursos Activos by")
      val s = 3
      When("the List of Cursos Activos by Seccion is retrieved")
      val res = CursosActivosModel.findCursosActivoBySecc(s).to[List].transact(xa).unsafeRunSync
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
      val res = CursosActivosModel.findCursosActivoByPeriodo(t).to[List].transact(xa).unsafeRunSync
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
      CursosActivosModel.insertCursoActivo(ins).withUniqueGeneratedKeys[CursoActivo]("clave", "secc", "titulo").transact(xa).unsafeRunSync
      Then("searching for that Curso Activo should return that Curso Activo")
      CursosActivosModel.findCursoActivo(ins).option.transact(xa).unsafeRunSync shouldBe Some(ins)
    }

    it("delete a Curso Activo from the Database"){
      Given("a certain Curso Activo to delete into the Database")
      val ins = CursoActivo("#d86", 109, "5 Dakota Way")
      When("the Curso Activo is correctly inserted")
      CursosActivosModel.deleteCursoActivo(ins).withUniqueGeneratedKeys[CursoActivo]("clave", "secc", "titulo").transact(xa).unsafeRunSync
      Then("searching for that Curso Activo should return that Curso Activo")
      CursosActivosModel.findCursoActivo(ins).option.transact(xa).unsafeRunSync shouldBe None
    }

  }

}