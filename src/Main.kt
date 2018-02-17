val map = LinkedHashMap<Char, Char>()

fun setupMap() {
    val normal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()-_[]{};':\",.<>/?"
    val inverse = "∀ᙠↃ◖ƎℲ⅁HIſ⋊⅂WᴎOԀΌᴚS⊥∩ᴧMX⅄Zɐqɔpǝɟƃɥıɾʞʃɯuodbɹsʇnʌʍxʎz12Ɛᔭ59Ɫ860¡@#$%^⅋*)(-‾][}{؛,:„'˙></¿"

    for(char in normal)
        map.put(char, inverse[normal.indexOf(char)])
}

fun main(args: Array<String>) {
    setupMap()

}