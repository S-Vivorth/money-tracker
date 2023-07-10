package io.paraga.moneytrackerdev.utils.helper

import android.util.Log
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import kotlin.collections.ArrayList

//object ExcelHelper {
//    var list = ArrayList<ArrayList<String>>()
//    fun insertExcelToSqlite(sheet: Sheet, onSuccess: (ArrayList<ArrayList<String>>) -> Unit) {
//        val rit = sheet.rowIterator()
//        sheet.getRow(0).physicalNumberOfCells
//        var numberOfColumns = 0
//
//        // get number of maximum column
//        rit.forEach { row ->
//            if (numberOfColumns < row.lastCellNum) {
//                numberOfColumns = row.lastCellNum.toInt()
//            }
//        }
//
//        // init list to size of numberOfColumns
//        for (column in 0 until numberOfColumns) {
//            list.add(ArrayList())
//        }
//
//        val rowIterator = sheet.rowIterator()
//        while (rowIterator.hasNext()) {
//            val row = rowIterator.next()
//            for (column in 0 until numberOfColumns) {
//                row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING)
//                list[column].add(row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).stringCellValue)
//                Log.i("asdad","${row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).stringCellValue}")
//            }
//        }
//        onSuccess(list)
//    }
//}
object ExcelHelper {
    var list = ArrayList<ArrayList<String>>()
    fun insertExcelToSqlite(sheet: Sheet, onSuccess: (ArrayList<ArrayList<String>>) -> Unit) {
        val rit = sheet.rowIterator()
        sheet.getRow(0).physicalNumberOfCells
        var numberOfColumns = 0
        val tempList: ArrayList<Number> = ArrayList()
        // get number of maximum column
        rit.forEach { row ->
            if (numberOfColumns < row.lastCellNum) {
                numberOfColumns = row.lastCellNum.toInt()
            }
        }

        // init list to size of numberOfColumns
        for (column in 0 until numberOfColumns) {
            tempList.add(0)
            list.add(ArrayList())
        }

        val rowIterator = sheet.rowIterator()
        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            for (column in 0 until numberOfColumns) {
                row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING)
                val data = row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).stringCellValue
                if (data.isNotEmpty()) {
                    tempList[column] = 1
                }
                list[column].add(data)
            }
        }
        val iterator = tempList.iterator()
        var index = 0
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item == 0) {
                list.removeAt(index)
                index -= 1
            }
            index += 1
        }
        onSuccess(list)
    }
}