package com.sistr.kaskelotti

import android.database.Cursor
import java.util.*

enum class Tense {
    PRESENT,
    IMPERFECT
}

class Verb(cursor: Cursor) {
    val aInfinitiivi = "olla"
    val vartalo = Pair("ole", false)
    val verbitipi = 3
    val tenses: EnumMap<Tense, Array<Pair<String, Boolean>>>

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