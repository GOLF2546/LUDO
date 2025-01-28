package models
import scala.collection.mutable.ArrayBuffer

sealed trait Slot {
  def types: String
}


object Slot {
    case object Start extends Slot {
        var block: Array[Int] = Array.fill(4)(4)
        val types: String = "Start"
    }
    case object End extends Slot {
        var block: Array[Int] = Array.fill(4)(0)
        val types: String = "End"
    }
    case object FLine extends Slot {
        var block: Array[Int] = Array.fill(20)(0)
        val types: String = "FLine"
    }
    case object Normal extends Slot {
        var block: Array[Int] = Array.fill(52)(0)
        val types: String = "Normal"
    }
}
