package com.mrcd

import jxl.Sheet
import jxl.Workbook
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.QName
import org.dom4j.bean.BeanElement
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import java.io.File
import java.io.FileWriter


/**
 * 将原始xml读取到 LinedHashMap 中，将 excel 放到 HashMap 中，遍历 LinedHashMap 打印原始结果，如果 HashMap 有相同的 key
 * 打印 HashMap 中的 value 并 remove，最后打印 HashMap 剩余的值
 *
 * todo 特殊字符的转换
 * Create by im_dsd 2020/6/30 13:35
 */
internal class Converter(private val excelPath: String, private val outPutPath: String) {

    fun convert() {
        val excelFile = File(excelPath)
        if (!excelFile.exists()) {
            throw NoSuchFileException(file = excelFile, reason = "excel 文件不存在，请检查参数")
        }

        println("------- 开始转换 ------\n")

        for (index in 0 until SHEET_COUNT) {
            // 转换 sheet， 转换个数可配置。
            val sheet = Workbook.getWorkbook(excelFile).getSheet(index)
            for (column in FIRST_COLUMN until sheet.columns) {
                convertColumn(sheet, column)
            }
        }

        println("-------转换完成 ------\n")
    }

    private fun convertColumn(sheet: Sheet, column: Int): Document {
        val langCode = sheet.getCell(column, 0).contents
        val xmlFile = loadXmlFile(langCode)
        val xmlElementMap = parserXml(xmlFile)
        val excelElementMap = parserExcel(sheet, column)
        val document = createXmlDocument(excelElementMap, xmlElementMap)
        output(document, xmlFile)
        return document
    }

    private fun createXmlDocument(
        excelElementMap: LinkedHashMap<String, String>,
        xmlElementMap: LinkedHashMap<String, Element>
    ): Document {
        val document = DocumentHelper.createDocument()
        val root = DocumentHelper.createElement("resources")
        document.rootElement = root
        document.xmlEncoding = "UTF-8"
        excelElementMap.forEach { (key, value) ->
            if (xmlElementMap.containsKey(key)) {
                xmlElementMap[key]?.text = value
            } else {
                val newElement = BeanElement(QName(value))
                newElement.text = value
                xmlElementMap[key] = newElement
            }
            root.add(xmlElementMap[key])
        }
        return document
    }

    private fun parserExcel(sheet: Sheet, column: Int): LinkedHashMap<String, String> {
        val excelElementMap = LinkedHashMap<String, String>()
        for (row in FIRST_ROW until sheet.rows) {
            val key = sheet.getCell(LANGUAGE_TITLE_ROW, row).contents
            val value = sheet.getCell(column, row).contents
            if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                continue
            }
            excelElementMap[key.trim()] = value.trim()
        }
        return excelElementMap
    }

    private fun parserXml(xmlFile: File): LinkedHashMap<String, Element> {
        val document = SAXReader().read(xmlFile)
        val map = LinkedHashMap<String, Element>()
        document.rootElement.elementIterator()
            .forEachRemaining { element -> map[element.name] = element }
        return map
    }

    private fun loadXmlFile(langCode: String): File {
        val dirPath = outPutPath + "value${buildDirName(langCode)}"
        val dirFile = File(dirPath)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        val file = File(dirPath, "strings.xml")
        if (!file.exists() || !file.isFile) {
            file.createNewFile()
        }
        println(file.parent)
        return file
    }

    private fun buildDirName(langCode: String): String {
        return if (langCode == "en") "" else "-${if (langCode == "id") "in" else langCode}"
    }

    private fun output(doc: Document, outputFile: File) {
        val format = OutputFormat.createPrettyPrint()
        format.encoding = doc.xmlEncoding
        val writer = XMLWriter(FileWriter(outputFile), format)
        writer.write(doc)
        writer.flush()
        writer.close()
    }

    companion object {
        /**
         * 文案启始列索引
         */
        private const val FIRST_COLUMN = 2

        /**
         * 语言标题行索引
         */
        private const val LANGUAGE_TITLE_ROW = 0

        /**
         * 文案启始行索引
         */
        private const val FIRST_ROW = LANGUAGE_TITLE_ROW + 1

        private const val SHEET_COUNT = 1;
    }
}

