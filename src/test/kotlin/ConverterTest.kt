import com.mrcd.MainKt
import org.apache.commons.text.StringEscapeUtils
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
    fun test() {
        val array = arrayOf("./src/test/resources/copy_writer.xls", "src/test/resources")
        MainKt.main(array)
    }

    @Test
    fun absoluteTest() {
        val array = arrayOf(
            "./src/test/resources/copy_writer.xls",
            "/Users/im_dsd/Documents/work_room/WeShare-Android/chatroom/src/main/res"
        )
        MainKt.main(array)
    }

    @Test
    fun testStringEscapeUtils() {
        val file = File("src/test/resources/text")
        println(file.readLines()[0])
        println(StringEscapeUtils.unescapeXml("@him/her"))
        println(StringEscapeUtils.unescapeHtml4("&#064;"))
    }
}