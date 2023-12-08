package astro.value

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.Test
import river.exertion.sac.astro.state.*
import river.exertion.sac.astro.value.ValueChart
import river.exertion.sac.component.SACComponent
import river.exertion.sac.console.state.*

object TestValueChart {

    @Test
    fun testValueChart() {
        //noon UTC with offset for Profile timezone
        val timeZoneOffsetHour = 12 + SACComponent.sacCelestialSnapshot.refEarthLocation.getTimezoneOffsetInt()

/*
        val synProfile = Profile("testProfile"
            , EarthLocation(refProfile.earthLocation.longitude, refProfile.earthLocation.latitude, refProfile.earthLocation.altitude, refProfile.earthLocation.timeZone
                , synUTC.toLocalDateTime(TimeZone.UTC) ) )
*/
//        println("location: ${refProfile.celestialSnapshot.refEarthLocation}")

//        println(refProfile.celestialSnapshot.getAspectCelestialLongitudeMap().forEach { println("aspect / long: ${it.key} / ${it.value}")})

        val stateChart = StateChart(SACComponent.sacCelestialSnapshot, SACComponent.sacCelestialSnapshot, ChartState.NATAL_CHART
            , AspectsState.ALL_ASPECTS, TimeAspectsState.TIME_ASPECTS_ENABLED, AspectOverlayState.ASPECT_NATCOMP_OVERLAY_DEFAULT)

/*        val stateChart = StateChart(refProfile.celestialSnapshot, synProfile.celestialSnapshot, ChartState.NATAL_CHART
            , AspectsState.ALL_ASPECTS, TimeAspectsState.TIME_ASPECTS_ENABLED, AspectOverlayState.ASPECT_NATCOMP_OVERLAY_DEFAULT)
*/
        stateChart.getStateAspects().forEach { println("stateAspect: $it")}

        val valueChart = ValueChart(stateChart, AnalysisState.NO_ANALYSIS)

        valueChart.getValueAspects().forEach { println("valueAspect: $it")}

        println("base: ${valueChart.getBaseValue().positive}, ${valueChart.getBaseValue().negative}, = ${valueChart.getBaseValue().getNet()}, ${valueChart.getBaseValue().getConsonance()}, ${valueChart.getBaseValue().getStimulation()}")

    }

    @Test
    fun testValueChartAnalysis() {

        val refCelestialAspectMap = SACComponent.sacCelestialSnapshot.getAspectCelestialLongitudeMap()

        refCelestialAspectMap.entries.forEach {
            println("ac:${it.key}, long:${it.value}")
        }

        val stateAspects = StateChart.getAspects(SACComponent.sacCelestialSnapshot, SACComponent.sacCelestialSnapshot, ChartState.NATAL_CHART
            , AspectsState.ALL_ASPECTS, TimeAspectsState.TIME_ASPECTS_ENABLED, AspectOverlayState.ASPECT_NATCOMP_OVERLAY_DEFAULT)

        var valueChart = ValueChart(stateAspects, ChartState.NATAL_CHART, AnalysisState.NO_ANALYSIS)
        println("NO_ANALYSIS aspects for ${SACComponent.sacCelestialSnapshot.refEarthLocation.tag}")
        valueChart.getValueAspects().forEach { println(it) }
        println("base:${valueChart.getBaseValue()} mod:${valueChart.getModValue()}")

        valueChart = ValueChart(stateAspects, ChartState.NATAL_CHART, AnalysisState.CHARACTER_ANALYSIS)
        println("CHARACTER_ANALYSIS aspects for ${SACComponent.sacCelestialSnapshot.refEarthLocation.tag}")
        valueChart.getValueAspects().forEach { println(it) }
        println("base:${valueChart.getBaseValue()} mod:${valueChart.getModValue()}")

        valueChart = ValueChart(stateAspects, ChartState.NATAL_CHART, AnalysisState.ROMANTIC_ANALYSIS)
        println("ROMANTIC_ANALYSIS aspects for ${SACComponent.sacCelestialSnapshot.refEarthLocation.tag}")
        valueChart.getValueAspects().forEach { println(it) }
        println("base:${valueChart.getBaseValue()} mod:${valueChart.getModValue()}")

        valueChart = ValueChart(stateAspects, ChartState.NATAL_CHART, AnalysisState.PLANET_ANALYSIS)
        println("PLANET_ANALYSIS aspects for ${SACComponent.sacCelestialSnapshot.refEarthLocation.tag}")
        valueChart.getValueAspects().forEach { println(it) }
        println("base:${valueChart.getBaseValue()} mod:${valueChart.getModValue()}")

        valueChart = ValueChart(stateAspects, ChartState.NATAL_CHART, AnalysisState.ELEMENT_ANALYSIS)
        println("ELEMENT_ANALYSIS aspects for ${SACComponent.sacCelestialSnapshot.refEarthLocation.tag}")
        valueChart.getValueAspects().forEach { println(it) }
        println("base:${valueChart.getBaseValue()} mod:${valueChart.getModValue()}")

        valueChart = ValueChart(stateAspects, ChartState.NATAL_CHART, AnalysisState.MODE_ANALYSIS)
        println("MODE_ANALYSIS aspects for ${SACComponent.sacCelestialSnapshot.refEarthLocation.tag}")
        valueChart.getValueAspects().forEach { println(it) }
        println("base:${valueChart.getBaseValue()} mod:${valueChart.getModValue()}")
    }
}