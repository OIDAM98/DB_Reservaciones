package reservaciones.model

import doobie.implicits._

case class Salon(idsalon: String, capacidad:Int, tipo: String){
  require(idsalon.length <=  8)

  override val toString = s"$idsalon, $capacidad, $tipo"
}

object SalonesModel extends SearchableClassroom

trait SearchableClassroom{

  def getAllClassrooms() =
    sql"select * from salones"
      .query[Salon]

  def findSalon(toFind: Salon) =
    toFind match {
      case Salon(idsalon, capacidad, tipo) => sql"select * from salones where idsalon = $idsalon and capacidad = $capacidad and tipo = $tipo"
        .query[Salon]
    }

  def findSalon(id: String) =
    sql"select * from salones where idsalon = $id"
    .query[Salon]

  def findSalonesByCap(cap: Int) =
    sql"select * from salones where capacidad = $cap"
      .query[Salon]

  def findSalonesByTipo(tipo: String) =
    sql"select * from salones where tipo = $tipo"
      .query[Salon]
}