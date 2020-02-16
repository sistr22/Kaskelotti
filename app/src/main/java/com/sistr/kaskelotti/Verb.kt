package com.sistr.kaskelotti

import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.database.getStringOrNull
import java.util.*

enum class Tense {
    VARTALO,
    PRESENT,
    IMPERFECT
}

enum class Pronoun {
    MINA,
    SINA,
    HAN,
    ME,
    TE,
    HE
}

val kpt_vahva = arrayOf(
    "kk", "pp", "tt",
    "k", "p", "t",
    "nk", "nt", "mp", "lt", "rt"
)

val kpt_heikko = arrayOf(
    "k", "p", "t",
    "", "v", "d",
    "ng", "nn", "mm", "ll", "rr"
)

val dipthongs = arrayOf(
    "ai",
    "ei",
    "oi",
    "ui",
    "yi",
    "äi",
    "öi",
    "au",
    "eu",
    "iu",
    "ou",
    "ey",
    "iy",
    "äy",
    "öy",
    "ie",
    "uo",
    "yö"
)

val vowels = arrayOf(
    'a',
    'e',
    'i',
    'o',
    'u',
    'y',
    'ö',
    'ä'
)

fun isVowel(c: Char): Boolean {
    return vowels.contains(c)
}

fun isConson(c: Char): Boolean {
    return !vowels.contains(c)
}

fun divideInSyllables(word: String): MutableList<String> {
    val result = LinkedList<String>()
    var lastChars = LinkedList<Pair<Char, Int> >()
    var beginingSyllable = 0
    for(idx in word.indices) {
        val c = word[idx]
        if(lastChars.size == 5)
            lastChars.removeFirst()
        lastChars.addLast(Pair(c, idx))

        if(lastChars.size >= 3 && isVowel(lastChars[lastChars.lastIndex].first)
            && isConson(lastChars[lastChars.lastIndex-1].first)
            && isVowel(lastChars[lastChars.lastIndex-2].first)) {
            result.add(word.substring(beginingSyllable, lastChars[lastChars.lastIndex-1].second))
            beginingSyllable = lastChars[lastChars.lastIndex-1].second
            lastChars = LinkedList(lastChars.subList(lastChars.lastIndex-1, lastChars.size))
        } else if(lastChars.size >= 4 && isVowel(lastChars[lastChars.lastIndex].first)
            && isConson(lastChars[lastChars.lastIndex-1].first)
            && isConson(lastChars[lastChars.lastIndex-2].first)
            && isVowel(lastChars[lastChars.lastIndex-3].first)) {
            result.add(word.substring(beginingSyllable, lastChars[lastChars.lastIndex-1].second))
            beginingSyllable = lastChars[lastChars.lastIndex-1].second
            lastChars = LinkedList(lastChars.subList(lastChars.lastIndex-1, lastChars.size))
        } else if(lastChars.size >= 5 && isVowel(lastChars[lastChars.lastIndex].first)
            && isConson(lastChars[lastChars.lastIndex-1].first)
            && isConson(lastChars[lastChars.lastIndex-2].first)
            && isConson(lastChars[lastChars.lastIndex-3].first)
            && isVowel(lastChars[lastChars.lastIndex-4].first)) {
            result.add(word.substring(beginingSyllable, lastChars[lastChars.lastIndex-1].second))
            beginingSyllable = lastChars[lastChars.lastIndex-1].second
            lastChars = LinkedList(lastChars.subList(lastChars.lastIndex-1, lastChars.size))
        } else if(lastChars.size >= 2 && isVowel(lastChars[lastChars.lastIndex].first)
            && isVowel(lastChars[lastChars.lastIndex-1].first)
            && lastChars[lastChars.lastIndex].first != lastChars[lastChars.lastIndex-1].first
            && !dipthongs.contains("${lastChars[lastChars.lastIndex-1].first}${lastChars[lastChars.lastIndex].first}")) {
            result.add(word.substring(beginingSyllable, lastChars[lastChars.lastIndex].second))
            beginingSyllable = lastChars[lastChars.lastIndex].second
            lastChars = LinkedList(lastChars.subList(lastChars.lastIndex, lastChars.size))
        }
    }
    if(beginingSyllable != word.length) {
        result.add(word.substring(beginingSyllable))
    }
    return result
}

fun applyKPT(word: String): String {
    val syllables = divideInSyllables(word)
    val index = kpt_vahva.indexOfFirst { it == "${syllables[syllables.lastIndex-1].last()}${syllables.last()[0]}" }
    if(index != -1) {
        if(kpt_heikko[index].length == 2) {
            syllables[syllables.lastIndex-1] = syllables[syllables.lastIndex-1].dropLast(1) + kpt_heikko[index][0]
            syllables[syllables.lastIndex] = kpt_heikko[index][1] + syllables[syllables.lastIndex].removeRange(0, 1)
        } else if(kpt_heikko[index].length == 1) {
            syllables[syllables.lastIndex-1] = syllables[syllables.lastIndex-1].dropLast(1)
            syllables[syllables.lastIndex] = kpt_heikko[index][0] + syllables[syllables.lastIndex].removeRange(0, 1)
        }
    } else {
        val idx = kpt_vahva.indexOfFirst { it == "${syllables.last()[0]}" }
        if(idx != -1) {
            syllables[syllables.lastIndex] = kpt_heikko[idx] + syllables[syllables.lastIndex].removeRange(0, 1)
        }
    }


    return syllables.joinToString("")
}

class Conjugator(verb: Verb) {
    private val _verb: Verb

    init {
        _verb = verb
    }

    fun conjugate(tense: Tense, pronoun: Pronoun? = null): Spannable {
        return when(tense) {
            Tense.VARTALO -> vartalo()
            Tense.PRESENT -> present(pronoun!!)
            else -> throw Exception("conjugate: Unknown tense: ${tense}")
        }
    }

    private fun vartalo(): Spannable {
        return when(_verb.verbityypi) {
            1 -> {
                SpannableStringBuilder( _verb.aInfinitiivi.dropLast(1))
            }
            2 -> {
                SpannableStringBuilder( _verb.aInfinitiivi.dropLast(2))
            }
            3 -> {
                SpannableStringBuilder( _verb.aInfinitiivi.dropLast(2) + 'e')
            }
            4 -> {
                SpannableStringBuilder( _verb.aInfinitiivi.dropLast(2) + (if(_verb.usePilkut()) 'ä' else 'a'))
            }
            5 -> {
                SpannableStringBuilder( _verb.aInfinitiivi.dropLast(1) + "se")
            }
            else -> {
                throw Exception("vartalo: Unknown verbityypi: ${_verb.verbityypi}")
            }
        }
    }

    private fun present(pronoun: Pronoun): Spannable {
        when (_verb.verbityypi) {
            1 -> {
                return when(pronoun) {
                    Pronoun.MINA -> {
                        val stringBuilder = SpannableStringBuilder(applyKPT(_verb.vartalo.toString()).plus('n'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.SINA -> {
                        val stringBuilder = SpannableStringBuilder(applyKPT(_verb.vartalo.toString()).plus('t'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.HAN -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus(_verb.vartalo.last()))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.ME -> {
                        val stringBuilder = SpannableStringBuilder(applyKPT(_verb.vartalo.toString()).plus("mme"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.TE -> {
                        val stringBuilder = SpannableStringBuilder(applyKPT(_verb.vartalo.toString()).plus("tte"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.HE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus(if(_verb.usePilkut()) "vät" else "vat"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                }
            }
            2 -> {
                return when(pronoun) {
                    Pronoun.MINA -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus('n'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.SINA -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus('t'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.HAN -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString())
                        stringBuilder

                    }
                    Pronoun.ME -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus("mme"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.TE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus("tte"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.HE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus(if(_verb.usePilkut()) "vät" else "vat"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                }
            }
            3 -> {
                return when(pronoun) {
                    Pronoun.MINA -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus('n'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.SINA -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus('t'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.HAN -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus(_verb.vartalo.last()))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.ME -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus("mme"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.TE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus("tte"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.HE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus(if(_verb.usePilkut()) "vät" else "vat"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                }
            }
            4 -> {
                return when(pronoun) {
                    Pronoun.MINA -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus('n'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.SINA -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus('t'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.HAN -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus( _verb.vartalo.last()))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.ME -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus("mme"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.TE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus("tte"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.HE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus(if(_verb.usePilkut()) "vät" else "vat"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                }
            }
            5 -> {
                return when(pronoun) {
                    Pronoun.MINA -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus('n'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.SINA -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus('t'))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.HAN -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus(_verb.vartalo.last()))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-1, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder

                    }
                    Pronoun.ME -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus("mme"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.TE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus("tte"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                    Pronoun.HE -> {
                        val stringBuilder = SpannableStringBuilder(_verb.vartalo.toString().plus(if(_verb.usePilkut()) "vät" else "vat"))
                        stringBuilder.setSpan(
                            StyleSpan(BOLD),
                            stringBuilder.length-3, stringBuilder.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        stringBuilder
                    }
                }
            }
            else -> {
                throw Exception("present: Unknown verbityypi: ${_verb.verbityypi}")
            }
        }
    }
}

class Verb(cursor: Cursor) {
    val aInfinitiivi: String
    val vartalo: Spannable
    val verbityypi: Int
    val tenses: EnumMap<Tense, Array<Pair<Spannable, Boolean>>>

    init {
        cursor.moveToFirst()
        aInfinitiivi = cursor.getString(2)
        verbityypi = cursor.getInt(1)
        val conjugator = Conjugator(this)
        vartalo = if(cursor.getStringOrNull(3) != null) SpannableString(cursor.getStringOrNull(3)) else conjugator.conjugate(Tense.VARTALO)

        tenses = EnumMap(Tense::class.java)
        tenses[Tense.PRESENT] = arrayOf(
            Pair(if(cursor.getStringOrNull(5) != null) SpannableString(cursor.getStringOrNull(5)) else conjugator.conjugate(Tense.PRESENT, Pronoun.MINA), cursor.getStringOrNull(5) != null),
            Pair(if(cursor.getStringOrNull(6) != null) SpannableString(cursor.getStringOrNull(6)) else conjugator.conjugate(Tense.PRESENT, Pronoun.SINA), cursor.getStringOrNull(6) != null),
            Pair(if(cursor.getStringOrNull(7) != null) SpannableString(cursor.getStringOrNull(7)) else conjugator.conjugate(Tense.PRESENT, Pronoun.HAN), cursor.getStringOrNull(7) != null),
            Pair(if(cursor.getStringOrNull(8) != null) SpannableString(cursor.getStringOrNull(8)) else  conjugator.conjugate(Tense.PRESENT, Pronoun.ME), cursor.getStringOrNull(8) != null),
            Pair(if(cursor.getStringOrNull(9) != null) SpannableString(cursor.getStringOrNull(9)) else  conjugator.conjugate(Tense.PRESENT, Pronoun.TE), cursor.getStringOrNull(9) != null),
            Pair(if(cursor.getStringOrNull(10) != null) SpannableString(cursor.getStringOrNull(10)) else  conjugator.conjugate(Tense.PRESENT, Pronoun.HE), cursor.getStringOrNull(10) != null)
        )
    }

    fun usePilkut(): Boolean {
        return !aInfinitiivi.any { it == 'a' || it == 'o' || it == 'u' }
    }
}