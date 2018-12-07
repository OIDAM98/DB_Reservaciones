package reservaciones.view

import java.sql.{Timestamp}
import java.time.{LocalDate, LocalDateTime, LocalTime}

import javax.swing.{ JOptionPane}
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

import scala.swing._
import reservaciones.model._

object ModReserv extends SimpleSwingApplication{

  var selectionDate: LocalDate = null
  var selectionTime: LocalTime = null
  var selectionDateIni: LocalDate = null
  var selectionDateFin: LocalDate = null
  var selectionTimeIni: LocalTime = null
  var selectionTimeFin: LocalTime = null

  val desde = new Label("Desde:")
  val hasta = new Label("Hasta:")

  var comboDate:ComboBox[LocalDate] = null
  var comboTime:ComboBox[LocalTime] = null
  var comboDateIni: ComboBox[LocalDate] = null
  var comboDateFin: ComboBox[LocalDate] = null
  var comboTimeIni: ComboBox[LocalTime] = null
  var comboTimeFin: ComboBox[LocalTime] = null

  var selectDate:Button = null
  var selectDateTime:Button = null

  def top = new MainFrame {

    peer.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    override def closeOperation() { dispose() }

    title = "Reservación de Salas"

    val reserv = Connection.executeListQuery(ReservacionesModel.getAllActiveReservaciones())

    val dates = reserv.map {
      case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => Array(fechaini, fechafin)
    }

    val timesRes = reserv.map {
      case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => Array(fechaini.toLocalDateTime.toLocalTime, fechafin.toLocalDateTime.toLocalTime)
    }

    val times = (7 to 21).map(h => LocalTime.of(h, 0))

    val validDates = dates.flatten.map(v => v.toLocalDateTime.toLocalDate).toSet.toList
    val validTimes = timesRes.flatten.toSet.toList

    comboDate = new ComboBox(validDates)
    comboDate.visible = false
    comboTime = new ComboBox(validTimes)
    comboTime.visible = false

    val currPer = Connection.executeQuery(PeriodosModel.getCurrentPeriod())
    val dateIni = currPer.get.fechaini.toLocalDateTime.toLocalDate
    val dateFin = currPer.get.fechafin.toLocalDateTime.toLocalDate

    val allDates = (0L to dateFin.toEpochDay - dateIni.toEpochDay).map{
      d => dateIni.plusDays(d)
    }

    comboDateIni = new ComboBox(allDates)
    comboDateIni.visible = false
    comboDateFin = new ComboBox(allDates)
    comboDateFin.visible = false

    comboTimeIni = new ComboBox(times)
    comboTimeIni.visible = false
    comboTimeFin = new ComboBox(times)
    comboTimeFin.visible = false

    selectDate = new Button( Action("Anular Día y Hora") {
      selectionDate = comboDate.selection.item
      selectionTime = comboTime.selection.item

      deleteDayTime
    })
    selectDate.visible = false

    selectDateTime = new Button( Action("Anular Periodo") {
      selectionDateIni = comboDateIni.selection.item
      selectionDateFin = comboDateFin.selection.item
      selectionTimeIni = comboTimeIni.selection.item
      selectionTimeFin = comboTimeFin.selection.item

      deletePeriod

    })
    selectDateTime.visible = false

    preferredSize = new Dimension(350, 350)
    contents = new BoxPanel(Orientation.Vertical){
      contents += new Button( Action("Anular dada fecha y hora") {
        comboTimeIni.visible = false
        comboTimeFin.visible = false
        comboDateIni.visible = false
        comboDateFin.visible = false
        selectDateTime.visible = false
        desde.visible = false
        hasta.visible = false


        comboDate.visible = true
        selectDate.visible = true
        comboTime.visible = true
      })
      contents += new Button( Action("Anular dado periodo de tiempo") {
        comboDate.visible = false
        selectDate.visible = false
        comboTime.visible = false

        comboTimeIni.visible = true
        comboTimeFin.visible = true
        comboDateIni.visible = true
        comboDateFin.visible = true
        selectDateTime.visible = true
        desde.visible = true
        hasta.visible = true

      } )


      contents += comboDate
      contents += comboTime
      contents += selectDate
      contents += comboDateIni
      contents += comboTimeIni
      contents += comboDateFin
      contents += comboTimeFin
      contents += selectDateTime
    }

  }

  def deletePeriod() = {
    if(selectionDateIni != null && selectionDateFin != null && selectionTimeIni != null && selectionTimeFin != null) {
      if(((selectionDateFin.toEpochDay - selectionDateIni.toEpochDay) < 0) || selectionTimeIni.isAfter(selectionTimeFin)) {
        JOptionPane.showMessageDialog(null, "Input Error, check dates and times", "Error", JOptionPane.ERROR_MESSAGE)
      }
      else{
        val ini = Timestamp.valueOf(LocalDateTime.of(selectionDateIni, selectionTimeIni))
        val fin = Timestamp.valueOf(LocalDateTime.of(selectionDateFin, selectionTimeFin))
        try{
          Connection.executeUpdate(Reservacion, ReservacionesModel.deleteFromPeriodo(ini, fin))
          JOptionPane.showMessageDialog(null, "Borrado exitoso", "Confirmacion", JOptionPane.INFORMATION_MESSAGE)
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

        comboDateIni.visible = false
        comboDateFin.visible = false
        comboTimeIni.visible = false
        comboTimeFin.visible = false

        selectDateTime.visible = false
      }

    }
  }

  def deleteDayTime() = {

    val d = Timestamp.valueOf(LocalDateTime.of(selectionDate, selectionTime))

    try{
      Connection.executeUpdate(Reservacion, ReservacionesModel.deleteFromDia(d))
      JOptionPane.showMessageDialog(null, "Borrado exitoso", "Confirmacion", JOptionPane.INFORMATION_MESSAGE)
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

    comboDate.visible = false
    comboTime.visible = false
    selectDate.visible = false


  }


}





