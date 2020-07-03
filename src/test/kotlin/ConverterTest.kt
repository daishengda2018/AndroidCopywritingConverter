import com.mrcd.MainKt
import com.mrcd.Utils
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 *
 * Create by im_dsd 2020/7/1 13:32
 */

class ConverterTest {

    @Test(expected = IllegalArgumentException::class)
    fun testNotArg() {
        val array = Array<String>(0) { "" }
        MainKt.main(array)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testOneArg() {
        val array = arrayOf("")
        MainKt.main(array)
    }

    @Test(expected = NoSuchFileException::class)
    fun testErrorExcelPath() {
        val array = arrayOf("", "src/test/resources")
        MainKt.main(array)
    }

    @Test
    fun testStringEscapeUtils() {
        Assert.assertEquals(Utils.escapeContent("  "), "")
        Assert.assertEquals(Utils.escapeContent(" A  "), " A")
        Assert.assertEquals(Utils.escapeContent("'"), "\\'")
        Assert.assertEquals(Utils.escapeContent("\'"), "\\'")
        Assert.assertEquals(Utils.escapeContent("\\\""), "\\\"")
        Assert.assertEquals(Utils.escapeContent("\""), "\"")
        Assert.assertEquals(Utils.escapeContent("<"), "&lt;")
        Assert.assertEquals(Utils.escapeContent(">"), "&gt;")
        Assert.assertEquals(Utils.escapeContent("&"), "&amp;")

        val originStr = "  单引号：' \' AT：@, \\@ 双引号： \"， < > & "
        val destStr = "  单引号：\\' \\' AT：&#064;, &#064; 双引号： \"， &lt; &gt; &amp;"
        Assert.assertEquals(Utils.escapeContent(originStr), destStr)
    }

    @Test
    fun testLocal() {
        val array = arrayOf("./src/test/resources/copy_writer.xls", "src/test/resources")
        MainKt.main(array)
        val fileEn = File("src/test/resources/values/strings.xml")
        Assert.assertTrue(fileEn.exists() && fileEn.isFile)
    }

    @Test
    fun absoluteTest() {
        val array = arrayOf(
            "./src/test/resources/copy_writer.xls",
            "/Users/im_dsd/Documents/work_room/CopywritingConverter/src/test/resources"
        )
        MainKt.main(array)
        val fileEn = File("src/test/resources/values/strings.xml")
        Assert.assertTrue(fileEn.exists() && fileEn.isFile)
        testContent()
    }

    private fun testContent() {
        val fileKeyList = arrayOf("en", "in", "ta", "ar", "te")
        for (key in fileKeyList) {
            val destEnFile = File("src/test/resources/dest/$key.xml")
            val srcEnFile = File("src/test/resources/values${if (key == "en") "" else "-$key"}/strings.xml")
            Assert.assertTrue(destEnFile.length() == srcEnFile.length())
            Assert.assertEquals(destEnFile.readText(), destEnFile.readText())
        }
    }
}