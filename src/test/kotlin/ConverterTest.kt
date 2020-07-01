import com.mrcd.MainKt
import org.junit.Test

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

    @Test(expected = NoSuchFileException::class)
    fun test() {
        val array = arrayOf("./src/test/resources/copy_writer.xls", "src/test/resources")
        MainKt.main(array)
    }
}