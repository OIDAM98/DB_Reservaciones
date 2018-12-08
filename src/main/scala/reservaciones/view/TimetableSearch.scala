
package reservaciones.view

import java.awt.Color
import java.sql.{Time, Timestamp}
import java.time.{LocalDate, LocalDateTime, LocalTime}

import javax.swing.JOptionPane
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

import scala.swing.Swing.LineBorder
import scala.swing._
import reservaciones.model._

object TimetableSearch extends SimpleSwingApplication{

  var info = new Table(){
    showGrid = false
    border = LineBorder(Color.BLACK)
  }

  var selectionDate: LocalDate = null
  var selectionDateIni: LocalDate = null
  var selectionDateFin: LocalDate = null
  var selectionTimeIni: LocalTime = null
  var selectionTimeFin: LocalTime = null

  var comboDate:ComboBox[LocalDate] = null
  var comboDateIni: ComboBox[LocalDate] = null
  var comboDateFin: ComboBox[LocalDate] = null
  var comboTimeIni: ComboBox[LocalTime] = null
  var comboTimeFin: ComboBox[LocalTime] = null

  var selectDate:Button = null
  var selectDateTime:Button = null

  var label: Label = new Label("")

  def top = new MainFrame {

    peer.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    override def closeOperation() { dispose() }

    title = "Consulta de Horarios"

    val dates = Connection.executeListQuery(ReservacionesModel.getAllActiveReservaciones()).map {
      case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => Array(fechaini, fechafin)
    }

    val times = (7 to 21).map(h => LocalTime.of(h, 0))

    val validDates = dates.flatten.map(v => v.toLocalDateTime.toLocalDate).toSet.toList

    comboDate = new ComboBox(validDates)
    comboDate.visible = false

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

    selectDate = new Button( Action("Buscar Día") {
      selectionDate = comboDate.selection.item
      timetableDay
    })
    selectDate.visible = false

    selectDateTime = new Button( Action("Buscar Dia y Hora") {
      selectionDateIni = comboDateIni.selection.item
      selectionDateFin = comboDateFin.selection.item
      selectionTimeIni = comboTimeIni.selection.item
      selectionTimeFin = comboTimeFin.selection.item

      timetablePeriod

    })
    selectDateTime.visible = false

    label.visible = false

    preferredSize = new Dimension(350, 350)
    contents = new BoxPanel(Orientation.Vertical){
      contents += new Button( Action("Horario de un Día") {
        comboTimeIni.visible = false
        comboTimeFin.visible = false
        comboDateIni.visible = false
        comboDateFin.visible = false
        selectDateTime.visible = false


        comboDate.visible = true
        selectDate.visible = true
      })
      contents += new Button( Action("Horario de un Periodo") {
        comboDate.visible = false
        selectDate.visible = false

        comboTimeIni.visible = true
        comboTimeFin.visible = true
        comboDateIni.visible = true
        comboDateFin.visible = true
        selectDateTime.visible = true

      } )

      contents += comboDate
      contents += selectDate
      contents += comboDateIni
      contents += comboTimeIni
      contents += comboDateFin
      contents += comboTimeFin
      contents += selectDateTime
      contents += label
      contents += new ScrollPane(info)
    }

  }

  def timetablePeriod() = {
    if(selectionDateIni != null && selectionDateFin != null && selectionTimeIni != null && selectionTimeFin != null) {
      if(((selectionDateFin.toEpochDay - selectionDateIni.toEpochDay) < 0) || selectionTimeIni.isAfter(selectionTimeFin)) {
        JOptionPane.showMessageDialog(null, "Input Error, check dates and times", "Error", JOptionPane.ERROR_MESSAGE)
      }
      else{
        val ini = Timestamp.valueOf(LocalDateTime.of(selectionDateIni, selectionTimeIni))
        val fin = Timestamp.valueOf(LocalDateTime.of(selectionDateFin, selectionTimeFin))
        val rowData = Connection.executeListQuery(ReservacionesModel.getTimetablePeriodo(ini, fin)).map{
          case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => Array(salon, fechaini.toString, fechafin.toString, clave.getOrElse("-"), secc.getOrElse("-"), periodo.getOrElse("-"), nombre).map(_.asInstanceOf[Any])
        }.toArray

        val columns = ColumnInfo.getReservaciones()

        val toUpdate = new Table(rowData, columns)


        info.model = toUpdate.model

        info.revalidate
        info.repaint

        label.text = s"Horario entre días: ${ini.toString} y ${fin.toString}"
        label.visible = true

        comboDateIni.visible = false
        comboDateFin.visible = false
        comboTimeIni.visible = false
        comboTimeFin.visible = false

        selectDateTime.visible = false
      }

    }
  }

  def timetableDay() = {

    if(selectionDate != null) {

      val rowData = Connection.executeListQuery(ReservacionesModel.getTimetableDia(Timestamp.valueOf(selectionDate.toString + " 00:00:00"))).map{
        case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => Array(salon, fechaini.toString, fechafin.toString, clave.getOrElse("-"), secc.getOrElse("-"), periodo.getOrElse("-"), nombre).map(_.asInstanceOf[Any])
      }.toArray

      val columns = ColumnInfo.getReservaciones()

      val toUpdate = new Table(rowData, columns)

      info.model = toUpdate.model

      info.revalidate
      info.repaint

      label.text = "Horario del día: " + selectionDate
      label.visible = true

      comboDate.visible = false
      selectDate.visible = false

    }

  }


}
