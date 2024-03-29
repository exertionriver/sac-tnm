package swe

import TestEarthLocations
import org.junit.jupiter.api.Test
import river.exertion.sac.swe.Julday
import kotlin.test.assertEquals

class TestJulday {

    @Test
    fun testGetJulianTimeDecimal() {
        val testUTC = TestEarthLocations.sfeEarthLocation.utcDateTime

        println("test utc: $testUTC")

        val uniTimeDec = Julday.getJulianTimeDecimal(testUTC, Julday.UNIVERSAL_TIME)
        println("julday universal time: $uniTimeDec")
        assertEquals("2458735.8813", "%1.4f".format(uniTimeDec))

        val terTimeDec = Julday.getJulianTimeDecimal(testUTC, Julday.TERRESTRIAL_TIME)
        println("julday terrestrial time: $terTimeDec")
        assertEquals("2458735.8821", "%1.4f".format(terTimeDec))
    }
}