package modelTest

import reservaciones.model.{Curso, CursoActivo, Salon}
import org.scalacheck.Gen
import org.scalacheck.Prop.BooleanOperators
import org.scalatest._
import org.scalatest.prop.PropertyChecks

class SalonCheckPropierties extends PropSpec with PropertyChecks with Matchers{

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


class CursoCheckPropierties extends PropSpec with PropertyChecks with Matchers{

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

class CursoActivoCheckPropierties extends PropSpec with PropertyChecks with Matchers {

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