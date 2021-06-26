package com.learnbible.utilities

import java.text.Normalizer

object LevenshteinDistance {
    private fun minimum(a: Int, b: Int, c: Int): Int {
        return Math.min(a, Math.min(b, c))
    }

    @JvmStatic
    fun computeLevenshteinDistance(str1In: String, str2In: String): Int {
        var str1 = str1In
        var str2 = str2In
        str1 = clearData(str1)
        str2 = clearData(str2)
        return computeLevenshteinDistance(str1.toCharArray(),
                str2.toCharArray())
    }

    fun clearData(strIn: String): String {

        //minusculas
        var str = strIn
        str = str.toLowerCase()

        //acentos
        str = Normalizer.normalize(str, Normalizer.Form.NFD)
        str = str.replace("[^\\p{ASCII}]".toRegex(), "")

        //eliminar caracteres especiales
        str = str.replace("[^A-Za-z0-9]".toRegex(), "")
        //str = str.replaceAll("[-+.^:,\\s?¿¡!-]","");
        return str
    }

    private fun computeLevenshteinDistance(str1: CharArray, str2: CharArray): Int {
        val distance = Array(str1.size + 1) { IntArray(str2.size + 1) }
        for (i in 0..str1.size) {
            distance[i][0] = i
        }
        for (j in 0..str2.size) {
            distance[0][j] = j
        }
        for (i in 1..str1.size) {
            for (j in 1..str2.size) {
                distance[i][j] = minimum(distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] +
                                if (str1[i - 1] == str2[j - 1]) 0 else 1)
            }
        }
        return distance[str1.size][str2.size]
    }
}