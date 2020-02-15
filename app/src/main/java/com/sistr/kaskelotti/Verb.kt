package com.sistr.kaskelotti

import android.database.Cursor
import androidx.core.database.getStringOrNull
import java.util.*

enum class Tense {
    VARTALO,
    PRESENT,
    IMPERFECT
}

enum class Pronoum {
    MINA,
    SINA,
    HAN,
    ME,
    TE,
    HE
}

class Conjugator(verb: Verb) {
    private val _verb: Verb

    init {
        _verb = verb
    }

    fun conjugate(tense: Tense, pronoum: Pronoum? = null): String {
        when(tense) {
            Tense.VARTALO -> return vartalo()
            else -> throw Exception("conjugate: Unknow tense: ${tense}")
        }
    }

    private fun vartalo(): String {
        when(_verb.verbityypi) {
            1 -> {
                return _verb.aInfinitiivi.dropLast(1)
            }
            2 -> {
                return _verb.aInfinitiivi.dropLast(1)
            }
            3 -> {
                return _verb.aInfinitiivi.dropLast(1)
            }
            4 -> {
                return _verb.aInfinitiivi.dropLast(1)
            }
            5 -> {
                return _verb.aInfinitiivi.dropLast(1)
            }
            else -> {
                throw Exception("vartalo: Unknow verbityypi: ${_verb.verbityypi}")
            }
        }
    }
}

class Verb(cursor: Cursor) {
    val aInfinitiivi: String
    val vartalo: String
    val verbityypi: Int
    val tenses: EnumMap<Tense, Array<Pair<String, Boolean>>>

    init {
        cursor.moveToFirst()
        aInfinitiivi = cursor.getString(2)
        verbityypi = cursor.getInt(1)
        val conjugator = Conjugator(this)
        vartalo = cursor.getStringOrNull(3) ?: conjugator.conjugate(Tense.VARTALO)
    }
    init {
        tenses = EnumMap(Tense::class.java)
        tenses[Tense.PRESENT] = arrayOf(
            Pair("olen", false),
            Pair("olet", false),
            Pair("on", true),
            Pair("olemme", false),
            Pair("olette", false),
            Pair("olevat", true)
        )
    }
}