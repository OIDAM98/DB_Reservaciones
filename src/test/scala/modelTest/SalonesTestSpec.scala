package modelTest



import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.specs2._
import org.specs2.mutable.Specification
import reservaciones.model.{Salon, SalonesModel}
import org.scalacheck.{Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators}

import scala.concurrent.ExecutionContext

object SalonesModelQueriesTest extends Specification with IOChecker {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )
  "Typecheck getAllClassrooms" in {
    check(SalonesModel.getAllClassrooms())
  }

  "Typecheck findSalon" in {
    check(SalonesModel.findSalon(Salon("hola", 10, "SC")))
  }

  "Typecheck findSalonesByCap" in {
    check(SalonesModel.findSalonesByCap(50))
  }

  "Typecheck findSalonesByTipo" in {
    check(SalonesModel.findSalonesByTipo("SC"))
  }

  "Typecheck insertSalon" in {
    check(SalonesModel.insertSalon(Salon("ia-205",50,"C")))
  }

}

object SalonesCheckPropierties extends Properties("Salon Simple") {
  property("Salones es compatible con la base de datos (Restricciones de Dominio)") =
    Prop.forAll(Gen.alphaStr.retryUntil(_.length <= 8 ), Gen.posNum[Int], Gen.alphaUpperStr.retryUntil(_.length <= 3)) {
      (id: String, cap: Int, tip: String) => {
        (id.length <= 8) ==> {
          val sal = Salon(id, cap, tip)
          sal.idsalon == id
          sal.capacidad == cap
          sal.tipo == tip
        }
      }


    }
}

