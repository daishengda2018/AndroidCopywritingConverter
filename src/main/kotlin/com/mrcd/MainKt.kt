package com.mrcd

/**
 * Create by im_dsd 2020/7/1 11:18
 */
class MainKt private constructor() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size < 2) {
                throw IllegalArgumentException("请输入必要参数")
            }
            Converter(args[0], args[1]).convert()
        }
    }
}