package com.mrcd

import org.apache.commons.text.StringEscapeUtils

/**
 * Create by im_dsd 2020/7/3 18:50
 */
internal object Utils {
    /**
     * 转译特殊字符
     */
    fun escapeContent(text: String?): String {
        // 1. 先过去尾部空格, 开头的空格有的是估计留下，所有不能统一处理
        var replace = text?.trimEnd() ?: ""
        // 2. 替换单引号，防止 StringEscapeUtils 修改老的文案
        replace = replace.replace("\\'", "'")
        // 3. 替换 @，，防止 StringEscapeUtils 修改老的文案
        replace = replace.replace("\\@", "@")
        // 4. 转译
        replace = StringEscapeUtils.escapeXml11(replace)
        // 5. 替换单引号编码为反斜杠转译，因为 Android 不识别 &apos;
        replace = replace.replace("&apos;", "\\'")
        // 6. 替换被 StringEscapeUtils 转移的双引号，因为 Android 不识别
        replace = replace.replace("&quot;", "\"")
        // 7. StringEscapeUtils 不处理 @ 需手动处理
        replace = replace.replace("@", "&#064;")
        return replace
    }
}