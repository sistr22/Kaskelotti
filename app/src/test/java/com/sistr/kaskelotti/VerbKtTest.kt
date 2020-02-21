package com.sistr.kaskelotti

import android.database.MatrixCursor
import android.os.Build
import org.junit.Test

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
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

    @Test
    fun verbPuhua() {
        val mockedCursor = MatrixCursor(arrayOf("_id", "tyypi", "a_infinitiivi", "vartalo", "_id", "mina", "sina", "han", "me", "te", "he"))
        mockedCursor.addRow(arrayOf("0", "1", "puhua", null, 0, null, null, null, null, null, null))

        val puhua = Verb(mockedCursor)
        val expectedResults = arrayOf("puhun", "puhut", "puhuu", "puhumme", "puhutte", "puhuvat")
        for(i in expectedResults.indices) {
            val tmp = puhua.tenses[Tense.PRESENT]!![i].first.toString()
            assert(tmp.compareTo(expectedResults[i]) == 0) { "Expected: ${expectedResults[i]} received: $tmp" }
        }
    }

    @Test
    fun verbHerattaa() {
        val mockedCursor = MatrixCursor(arrayOf("_id", "tyypi", "a_infinitiivi", "vartalo", "_id", "mina", "sina", "han", "me", "te", "he"))
        mockedCursor.addRow(arrayOf("0", "4", "herätä", null, 0, null, null, null, null, null, null))

        val verb = Verb(mockedCursor)
        val expectedResults = arrayOf("herään", "heräät", "herää", "heräämme", "heräätte", "heräävät")
        for(i in expectedResults.indices) {
            val tmp = verb.tenses[Tense.PRESENT]!![i].first.toString()
            assert(tmp.compareTo(expectedResults[i]) == 0) { "Expected: ${expectedResults[i]} received: $tmp" }
        }
    }
}