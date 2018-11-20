package modelTest

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.scalatest._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import reservaciones.model.{Salon, SalonesModel}
import org.scalacheck.Gen
import org.scalacheck.Prop.BooleanOperators
import org.scalatest._
import org.scalatest.prop.PropertyChecks

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
    finally xa = null
  }
}

class SalonesModelOpTest extends FunSpec with Matchers with GivenWhenThen with DBTest {

  describe("SalonesModel") {
    it("get all Salones from the Database"){
      val all = SalonesModel.getAllClassrooms().to[List].transact(xa).unsafeRunSync
      all should not be empty
    }

    it("find a specific Salon on the Database"){
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
      Given("a Salon to insert")
      val ins = Salon("ia-205", 50, "C")
      When("the Salon is inserted correctly to the Database")
      SalonesModel.insertSalon(ins).withUniqueGeneratedKeys("idsalon", "capacidad", "tipo").transact(xa).unsafeRunSync
      Then("searching for that Salon should return that salon")
      SalonesModel.findSalon(ins).option.transact(xa).unsafeRunSync shouldBe Some(ins)
    }

    it("delete a Salon from the Database"){
      Given("a Salon to insert")
      val del = Salon("ia-205", 50, "C")
      When("the Salon is deleted correctly from the Database")
      SalonesModel.deleteSalon(del).withUniqueGeneratedKeys("idsalon", "capacidad", "tipo").transact(xa).unsafeRunSync
      Then("searching for that Salon should return None")
      SalonesModel.findSalon(del).option.transact(xa).unsafeRunSync shouldBe None
    }

  }
}

class SalonesModelQueriesTest extends FunSuite with Matchers with IOChecker {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  test("Typecheck getAllClassrooms") {
    check(SalonesModel.getAllClassrooms())
  }

  test("Typecheck findSalon") {
    check(SalonesModel.findSalon(Salon("hola", 10, "SC")))
  }

  test("Typecheck findSalonesByCap") {
    check(SalonesModel.findSalonesByCap(50))
  }

  test("Typecheck findSalonesByTipo") {
    check(SalonesModel.findSalonesByTipo("SC"))
  }

  test("Typecheck insertSalon") {
    check(SalonesModel.insertSalon(Salon("ia-205",50,"C")))
  }

  test("Typecheck deleteSalon") {
    check(SalonesModel.deleteSalon(Salon("ia-205",50,"C")))
  }

}

class SalonesCheckPropierties extends PropSpec with PropertyChecks with Matchers{

  property("Salon is compatible with the Database (Domain Restrictions)") {
    forAll(Gen.alphaStr.retryUntil(_.length <= 8), Gen.posNum[Int], Gen.alphaUpperStr.retryUntil(_.length <= 3)) {
      (id: String, cap: Int, tip: String) => {
        (id.length < 0) ==> {
          val sal = Salon(id, cap, tip)
          sal.idsalon == id
          sal.capacidad == cap
          sal.tipo == tip
        }
      }
    }
  }

}

