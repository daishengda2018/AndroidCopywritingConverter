package com.mrcd

import jxl.Sheet
import jxl.Workbook
import org.dom4j.*
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import org.dom4j.tree.DefaultElement
import org.xml.sax.SAXParseException
import java.io.File
import java.io.FileWriter

/**
 *
 * 安卓国际化多语文案自动转 string.xml 程序。一键增加、修改文案
 *
 * Create by im_dsd 2020/6/30 13:35
 */
internal class Converter(private val excelPath: String, private val outPutPath: String) {

    fun convert() {
        val excelFile = File(excelPath)
        if (!excelFile.exists()) {
            throw NoSuchFileException(file = excelFile, reason = "excel 文件不存在，请检查参数")
        }

        println("============= start convert =============\n")

        for (index in 0 until SHEET_COUNT) {
            // 转换 sheet， 转换个数可配置。
            val sheet = Workbook.getWorkbook(excelFile).getSheet(index)
            for (column in FIRST_COLUMN until sheet.columns) {
                convertColumn(sheet, column)
            }
        }

        println("\n============= convert complete =============")
    }

    private fun convertColumn(sheet: Sheet, column: Int) {
        val langCode = sheet.getCell(column, 0).contents
        if (langCode.isEmpty()) {
            return
        }
        val xmlFile = loadXmlFile(langCode)
        val xmlDoc = readXmlFile(xmlFile)
        val rootElement = xmlDoc.rootElement ?: DocumentHelper.createElement("resources")
        merge(parserExcel(sheet, column), parserXmlDoc(xmlDoc)).forEach { e -> rootElement.add(e) }
        xmlDoc.rootElement = rootElement
        xmlDoc.xmlEncoding = "UTF-8"
        output(xmlDoc, xmlFile)
    }

    /**
     * 合并文案
     */
    private fun merge(
        excelElementMap: LinkedHashMap<String, String>,
        xmlElementMap: LinkedHashMap<String, Element>
    ): ArrayList<Element> {
        // 替换老文案
        xmlElementMap.keys.forEach() { key ->
            if (excelElementMap.containsKey(key)) {
                xmlElementMap[key]?.text = excelElementMap[key]
                excelElementMap.remove(key)
            }
            xmlElementMap[key]?.text = Utils.escapeContent(xmlElementMap[key]?.text)
        }

        // 添加新文案
        val resultList = ArrayList<Element>(excelElementMap.size)
        excelElementMap.forEach { (key, value) ->
            val newElement = DefaultElement(QName("string"))
            newElement.addAttribute("name", key)
            newElement.text = Utils.escapeContent(value.trim())
            resultList.add(newElement)
        }

        return resultList
    }

    private fun readXmlFile(xmlFile: File): Document {
        try {
            return SAXReader().read(xmlFile)
        } catch (ignore: SAXParseException) {
        } catch (ignore: DocumentException) {
        }
        return DocumentHelper.createDocument()
    }

    @Suppress("ComplexCondition")
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

    private fun parserXmlDoc(doc: Document): LinkedHashMap<String, Element> {
        val map = LinkedHashMap<String, Element>()
        doc.rootElement
            ?.elementIterator()
            ?.forEachRemaining { element ->
                map[element.attribute("name").value.trim()] = element
            }
        return map
    }

    private fun loadXmlFile(langCode: String): File {
        val dirPath = outPutPath + "/values${buildDirName(langCode)}"
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

    /**
     * 一些特殊的名字
     */
    private fun buildDirName(langCode: String): String {
        return if (langCode == "en") "" else "-${if (langCode == "id") "in" else langCode}"
    }

    private fun output(doc: Document, outputFile: File) {
        val format = OutputFormat()
        // 开头缩进
        format.setIndentSize(4)
        format.isNewlines = true
        format.isTrimText = true
        format.encoding = doc.xmlEncoding

        val writer = XMLWriter(FileWriter(outputFile), format)
        // 禁止解析特殊标签，会通过 parserExcel 方法解析
        writer.isEscapeText = false
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

        /**
         * sheet 数量
         */
        private const val SHEET_COUNT = 1
    }
}