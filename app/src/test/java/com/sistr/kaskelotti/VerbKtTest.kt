package com.sistr.kaskelotti

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class VerbKtTest {

    @Test
    fun divideInSyllables() {
        val testWords = arrayOf(
            "kylä",
            "Liisa",
            "seinä",
            "Matti",
            "metsä",
            "ihme",
            "kyllä",
            "Tanska",
            "kartta",
            "munkki",
            "ruskea",
            "radio"
        )
        val expectedResults = arrayOf(
            arrayOf("ky", "lä"),
            arrayOf("Lii", "sa"),
            arrayOf("sei", "nä"),
            arrayOf("Mat", "ti"),
            arrayOf("met", "sä"),
            arrayOf("ih", "me"),
            arrayOf("kyl", "lä"),
            arrayOf("Tans", "ka"),
            arrayOf("kart", "ta"),
            arrayOf("munk", "ki"),
            arrayOf("rus", "ke", "a"),
            arrayOf("ra", "di", "o")
        )
        for(wordIdx in testWords.indices) {
            val res = divideInSyllables(testWords[wordIdx])
            assert(res.size == expectedResults[wordIdx].size) {"${testWords[wordIdx]} : Expected ${expectedResults[wordIdx].size} syllables but result contains ${res.size}: ${expectedResults[wordIdx].contentToString()} != $res"}
            for(i in res.indices) {
                assert(res[i].contentEquals(expectedResults[wordIdx][i])) {"Expected these syllables: ${expectedResults[wordIdx].contentToString()} but result is: $res"}
            }
        }

        assertEquals(4, 2 + 2)
    }

    @Test
    fun applyKPT() {
        val testWords = arrayOf(
            "nukku"
        )
        val expectedResults = arrayOf(
            "nuku"
        )
        for(idx in testWords.indices) {
            val res = applyKPT(testWords[idx])
            assert(res.contentEquals(expectedResults[idx])) {"Expected: ${expectedResults[idx]} received: $res"}
        }

    }
}