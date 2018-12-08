package reservaciones.view

import java.sql.{Timestamp}
import java.time.{LocalDate, LocalDateTime, LocalTime}

import javax.swing.{ JOptionPane}
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

import scala.swing._
import reservaciones.model._

object ModHorario extends SimpleSwingApplication{

  val map = Map(
    "Lunes" -> 1,
    "Martes" -> 2,
    "Miercoles" -> 3,
    "Jueves" -> 4,
    "Viernes" -> 5,
    "Sabado" -> 6,
    "Domingo" -> 7
  )

  var selectionDate: LocalDate = null
  var selectionDay: String = null
  var selectionSalon: String = null
  var selectionHoursIni: Int = 0
  var selectionMinIni: Int = 0
  var selectionHoursFin: Int = 0
  var selectionMinFin: Int = 0

  val desde = new Label("Desde:"){
    visible = false
  }
  val hasta = new Label("Hasta:"){
    visible = false
  }

  var comboDate:ComboBox[LocalDate] = null
  var comboDay: ComboBox[String] = null
  var comboSalon: ComboBox[String] = null
  var comboHoursIni: ComboBox[Int] = null
  var comboMinIni: ComboBox[Int] = null
  var comboHoursFin: ComboBox[Int] = null
  var comboMinFin: ComboBox[Int] = null

  var selectDate:Button = null
  var selectDay:Button = null

  def top = new MainFrame {

    peer.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    override def closeOperation() { dispose() }

    title = "Reservación de Salas"

    val currPer = Connection.executeQuery(PeriodosModel.getCurrentPeriod())
    val dateIni = currPer.get.fechaini.toLocalDateTime.toLocalDate
    val dateFin = currPer.get.fechafin.toLocalDateTime.toLocalDate

    val today = LocalDate.now()

    val allDates = (0L to dateFin.toEpochDay - dateIni.toEpochDay)
      .map(d => dateIni.plusDays(d))
      .filter(d => today isBefore d)

    comboDate = new ComboBox(allDates)
    comboDate.visible = false

    val days = map.keys.toArray.sortBy(i => map(i))

    comboDay = new ComboBox(days)
    comboDay.visible = false

    val salones = Connection.executeListQuery(SalonesModel.getAllClassrooms()).map{
      case Salon(idsalon, capacidad, tipo) => idsalon
    }
    comboSalon = new ComboBox(salones)

    comboHoursIni = new ComboBox((7 to 21))
    comboHoursIni.visible = false
    comboMinIni = new ComboBox((10 to 50 by 10))
    comboMinIni.visible = false
    comboHoursFin = new ComboBox((7 to 21))
    comboHoursFin.visible = false
    comboMinFin = new ComboBox((10 to 50 by 10))
    comboMinFin.visible = false

    selectDate = new Button( Action("Reservar Salon Día y Hora") {
      selectionSalon = comboSalon.selection.item
      selectionDate = comboDate.selection.item
      selectionHoursIni = comboHoursIni.selection.item
      selectionMinIni = comboMinFin.selection.item
      selectionHoursFin = comboHoursFin.selection.item
      selectionMinFin = comboMinFin.selection.item

      reservDateTime
    })
    selectDate.visible = false

    selectDay = new Button( Action("Reservar Salon Día de la Semana") {
      selectionSalon = comboSalon.selection.item
      selectionDay = comboDay.selection.item
      selectionHoursIni = comboHoursIni.selection.item
      selectionMinIni = comboMinIni.selection.item
      selectionHoursFin = comboHoursFin.selection.item
      selectionMinFin = comboMinFin.selection.item

      reservDay

    })
    selectDay.visible = false

    preferredSize = new Dimension(350, 350)

    contents = new GridPanel(15, 1){
      contents += new Button( Action("Insertar por Fecha") {
        comboDay.visible = false
        selectDay.visible = false
        comboSalon.enabled = false


        comboDate.visible = true
        desde.visible = true
        comboHoursIni.visible = true
        comboMinIni.visible = true
        hasta.visible = true
        comboHoursFin.visible = true
        comboMinFin.visible = true
        selectDate.visible = true
      })
      contents += new Button( Action("Insertar por Dia de la Semana") {
        comboDate.visible = false
        selectDate.visible = false
        comboSalon.enabled = false

        comboDay.visible = true
        desde.visible = true
        comboHoursIni.visible = true
        comboMinIni.visible = true
        hasta.visible = true
        comboHoursFin.visible = true
        comboMinFin.visible = true
        selectDay.visible = true
        desde.visible = true

      } )

      contents += comboSalon
      contents += comboDate
      contents += desde
      contents += comboHoursIni
      contents += comboMinIni
      contents += hasta
      contents += comboHoursFin
      contents += comboMinFin
      contents += selectDate
      contents += comboDay
      contents += selectDay
    }

  }

  def reservDay() = {

    if((selectionHoursFin + selectionMinFin) <= (selectionHoursIni + selectionMinIni) ){

    }
    else {
      try{
        val day = map(selectionDay)
        val sal = Connection.executeQuery(SalonesModel.findSalon(selectionSalon)).get
        val name = JOptionPane.showInputDialog("Introduce quien desea reservar")
        ReservacionesModel.insertReservSpecificDay(sal, day, (selectionHoursIni, selectionMinIni), (selectionHoursFin, selectionMinFin), None, name)
        JOptionPane.showMessageDialog(null, "Insertado exitoso", "Confirmacion", JOptionPane.INFORMATION_MESSAGE)
      }
      catch {
        case e: java.sql.SQLException => {
          JOptionPane.showMessageDialog(null,
            s"${e.getErrorCode}: + ${e.getMessage}",
            "SQLError",
            JOptionPane.ERROR_MESSAGE
          )
        }
      }

      comboSalon.enabled = true

      comboDay.visible = false
      desde.visible = false
      comboHoursIni.visible = false
      comboMinIni.visible = false
      hasta.visible = false
      comboHoursFin.visible = false
      comboMinFin.visible = false
      selectDay.visible = false
      desde.visible = false
    }

  }

  def reservDateTime() = {

    val d = Timestamp.valueOf(LocalDateTime.of(selectionDate, LocalTime.of(selectionHoursIni, selectionMinIni)))

    try{
      val sal = Connection.executeQuery(SalonesModel.findSalon(selectionSalon)).get
      val name = JOptionPane.showInputDialog("Introduce quien desea reservar")
      Connection.executeUpdate(Reservacion, ReservacionesModel.insertReserv(sal,d, selectionHoursFin, selectionMinFin, name))
      JOptionPane.showMessageDialog(null, "Insertado exitoso", "Confirmacion", JOptionPane.INFORMATION_MESSAGE)
    }
    catch {
      case e: java.sql.SQLException => {
        JOptionPane.showMessageDialog(null,
          s"${e.getErrorCode}: + ${e.getMessage}",
          "SQLError",
          JOptionPane.ERROR_MESSAGE
        )
      }
    }

    comboSalon.enabled = true

    comboDate.visible = false
    desde.visible = false
    comboHoursIni.visible = false
    comboMinIni.visible = false
    hasta.visible = false
    comboHoursFin.visible = false
    comboMinFin.visible = false
    selectDate.visible = false

  }

}
