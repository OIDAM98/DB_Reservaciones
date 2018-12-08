package reservaciones.view

import java.awt.Color
import java.sql.{Time, Timestamp}
import java.time.{LocalDate, LocalDateTime, LocalTime}

import javax.swing.{DefaultComboBoxModel, JOptionPane}
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

import scala.swing.Swing.LineBorder
import scala.swing._
import reservaciones.model._

object ReservSearch extends SimpleSwingApplication{

  var info = new Table(){
    showGrid = false
    border = LineBorder(Color.BLACK)
  }

  var selectionOption:String = null
  var selectionSpecific:String = null;

  var comboSpecific:ComboBox[String] = null
  var comboOptions:ComboBox[String] = null

  var selectOption:String = null

  var search:Button = null

  var label: Label = new Label("")

  def top = new MainFrame {

    peer.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    override def closeOperation() { dispose() }

    title = "Consulta de Horarios de Salas"

    label.visible = false

    val data = Connection.executeListQuery(SalonesModel.getAllClassrooms())

    preferredSize = new Dimension(350, 350)
    contents = new BoxPanel(Orientation.Vertical){

      search = new Button( Action("Buscar segun criterio") {
        selectionSpecific = comboSpecific.selection.item
        showRooms
      } )
      search.visible = false

      comboOptions = new ComboBox(ColumnInfo.getSalones())

      comboSpecific = new ComboBox(List())
      comboSpecific.visible = false

      contents += comboOptions

      contents += new Button( Action("Criterio de seleccion") {
        selectionOption = comboOptions.selection.item

        var mod:DefaultComboBoxModel[String] = null

        selectionOption match {
          case "idsalon" => {
            val opts = data.map{
              case Salon(idsalon, capacidad, tipo) => idsalon
            }.distinct.toArray

            mod = new DefaultComboBoxModel[String](opts)
          }

          case "capacidad" => {
            val opts = data.map{
              case Salon(idsalon, capacidad, tipo) => capacidad.toString
            }.distinct.toArray

            mod = new DefaultComboBoxModel[String](opts)
          }

          case "tipo" => {
            val opts = data.map{
              case Salon(idsalon, capacidad, tipo) => tipo
            }.distinct.toArray

            mod = new DefaultComboBoxModel[String](opts)
          }
        }

        comboSpecific.peer.setModel(mod)
        comboSpecific.revalidate()

        comboSpecific.visible = true
        search.visible = true

        comboOptions.enabled = false
      })

      contents += comboSpecific
      contents += search
      contents += label
      contents += new ScrollPane(info)
    }

  }

  def showRooms() = {

    var toSearch: doobie.Query0[Salon] = null

    selectionOption match {
      case "idsalon" => toSearch = SalonesModel.findSalon(selectionSpecific)
      case "capacidad" => toSearch = SalonesModel.findSalonesByCap(selectionSpecific.toInt)
      case "tipo" => toSearch = SalonesModel.findSalonesByTipo(selectionSpecific)
    }

    val rowData = Connection.executeListQuery(toSearch).map{
      case Salon(idsalon, capacidad, tipo) => Array(idsalon, capacidad, tipo)
    }.toArray

    val columns = ColumnInfo.getSalones()

    val toUpdate = new Table(rowData, columns)

    label = new Label(s"Search result of $selectionOption:")
    label.visible = true

    info.model = toUpdate.model

    info.revalidate
    info.repaint

    comboOptions.enabled = true
    comboSpecific.visible = false
    search.visible = false

  }


}
