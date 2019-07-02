package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {
        if((fullName == null) or (fullName?.trim() == "")){
            return Pair(null, null)
        } else {
            val parts: List<String>? = fullName?.trim()?.split(" ")

            val firstName: String? = parts?.getOrNull(0)
            val lastName: String? = parts?.getOrNull(1)

            return Pair(firstName, lastName)
        }
    }

    fun transliteration(payload: String, divider: String = " "): String {
        payload.trim()
        var text: String = ""
        var char2: String
        for (char1 in payload.toCharArray()) {
            when (char1.toString()) {
                " " -> char2 = divider
                "а" -> char2 = "a"
                "б" -> char2 = "b"
                "в" -> char2 = "v"
                "г" -> char2 = "g"
                "д" -> char2 = "d"
                "е", "ё", "э" -> char2 = "e"
                "ж" -> char2 = "zh"
                "з" -> char2 = "z"
                "и", "й", "ы" -> char2 = "i"
                "к" -> char2 = "k"
                "л" -> char2 = "l"
                "м" -> char2 = "m"
                "н" -> char2 = "n"
                "о" -> char2 = "o"
                "п" -> char2 = "p"
                "р" -> char2 = "r"
                "с" -> char2 = "s"
                "т" -> char2 = "t"
                "у" -> char2 = "u"
                "ф" -> char2 = "f"
                "х" -> char2 = "h"
                "ц" -> char2 = "c"
                "ч" -> char2 = "ch"
                "ш", "щ" -> char2 = "sh"
                "ъ", "ь" -> char2 = ""
                "ю" -> char2 = "yu"
                "я" -> char2 = "ya"
                "А" -> char2 = "A"
                "Б" -> char2 = "B"
                "В" -> char2 = "V"
                "Г" -> char2 = "G"
                "Д" -> char2 = "D"
                "Е", "Ё", "Э" -> char2 = "E"
                "Ж" -> char2 = "Zh"
                "З" -> char2 = "Z"
                "И", "Й", "Ы" -> char2 = "I"
                "К" -> char2 = "K"
                "Л" -> char2 = "L"
                "М" -> char2 = "M"
                "Н" -> char2 = "N"
                "О" -> char2 = "O"
                "П" -> char2 = "P"
                "Р" -> char2 = "R"
                "С" -> char2 = "S"
                "Т" -> char2 = "T"
                "У" -> char2 = "U"
                "Ф" -> char2 = "F"
                "Х" -> char2 = "H"
                "Ц" -> char2 = "C"
                "Ч" -> char2 = "Ch"
                "Ш", "Щ" -> char2 = "Sh"
                "Ъ", "Ь" -> char2 = ""
                "Ю" -> char2 = "Yu"
                "Я" -> char2 = "Ya"
                else -> char2 = char1.toString()
            }
            text = "$text$char2"
        }
        return text
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        var f: String? = ""
        var l: String? = ""
        if ((firstName != null) and (firstName?.trim() != "")) f = firstName?.trim()?.substring(0, 1)?.toUpperCase()
        if ((lastName != null) and  (lastName?.trim() != "")) l = lastName?.trim()?.substring(0, 1)?.toUpperCase()
        if("$f$l" == "") return null else return "$f$l"

    }

    fun declination(N: Long, period: String): String {
        var my_text: String = "$N $period"
        when (period) {
            "MINUTE" -> when (N) {
                1L, 21L, 31L, 41L -> my_text = "$N минуту"
                2L, 3L, 4L, 22L, 23L, 24L, 32L, 33L, 34L, 42L, 43L, 44L -> my_text = "$N минуты"
                else -> my_text = "$N минут"
            }
            "HOUR" -> when (N) {
                1L, 21L -> my_text = "$N час"
                2L, 3L, 4L, 22L -> my_text = "$N часа"
                else -> my_text = "$N часов"
            }
            "DAY" -> {
                if (N.toString().length == 1) {
                    when (N) {
                        1L -> my_text = "$N день"
                        2L, 3L, 4L -> my_text = "$N дня"
                        else -> my_text = "$N дней"
                    }
                } else {
                    var NN = N
                    if (N.toString().length == 3) NN = N.toString().substring(1, 2).toLong()
                    when (NN) {
                        22L, 23L, 24L, 32L, 33L, 34L, 42L, 43L, 44L, 52L, 53L, 54L, 62L, 63L, 64L, 72L, 73L, 74L, 82L, 83L, 84L, 92L, 93L, 94L -> my_text =
                            "$N дня"
                        21L, 31L, 41L, 51L, 61L, 71L, 81L, 91L -> my_text = "$N день"
                        else -> my_text = "$N дней"
                    }
                }
            }
        }

        return my_text


    }

}