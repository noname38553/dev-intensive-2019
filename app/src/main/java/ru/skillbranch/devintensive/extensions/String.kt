package ru.skillbranch.devintensive.extensions

fun String.truncate(chars: Int = 16): String{
    if(this.toString().trimEnd().length <= chars){
        return this.toString().trimEnd() 
    }
    else{
        return this.substring(0,chars).trimEnd() + "..."
    }
}

fun String.stripHtml(): String?{
    return this.replace(Regex("<[^<]*?>|&(.)+;"),"").replace(Regex("[^\\S\\r\\n]+")," ")
}