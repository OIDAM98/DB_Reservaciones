package reservaciones.model

import doobie.implicits._

case class Salon(idsalon: String, capacidad:Int, tipo: String){
  require(idsalon.length <=  8)

  override val toString = s"$idsalon $capacidad $tipo"
}

object SalonesModel extends SearchableClassroom with InsertableClassroom with DeletableClassroom

trait SearchableClassroom{

  def getAllClassrooms() =
    sql"select * from salones"
      .query[Salon]

  def findSalon(toFind: Salon) =
    toFind match {
      case Salon(idsalon, capacidad, tipo) => sql"select * from salones where idsalon = $idsalon and capacidad = $capacidad and tipo = $tipo"
        .query[Salon]
    }

  def findSalonesByCap(cap: Int) =
    sql"select * from salones where capacidad = $cap"
      .query[Salon]

  def findSalonesByTipo(tipo: String) =
    sql"select * from salones where tipo = $tipo"
      .query[Salon]
}

trait InsertableClassroom {

  def insertSalon(toIns: Salon) =
    toIns match {
      case Salon(id, cap, tipo) => sql"insert into salones (idsalon, capacidad, tipo) values ($id, $cap, $tipo)"
        .update
    }

}

trait DeletableClassroom {
  def deleteSalon(toDel: Salon) =
    toDel match {
      case Salon(idsalon, capacidad, tipo) => sql"delete from salones where idsalon = $idsalon and capacidad = $capacidad and tipo = $tipo"
        .update
    }
}