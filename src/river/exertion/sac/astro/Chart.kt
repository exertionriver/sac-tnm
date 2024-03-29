package river.exertion.sac.astro

import river.exertion.sac.astro.Aspect.Companion.aspectFromCelestialAspect
import river.exertion.sac.astro.AspectValue.sortFilterValueAspects
import river.exertion.sac.astro.ChartValue.getChartBaseValue
import river.exertion.sac.astro.ChartValue.getChartModValue
import river.exertion.sac.astro.ChartValue.getChartNetValue
import river.exertion.sac.astro.base.*
import river.exertion.sac.console.state.*
import river.exertion.sac.swe.SwiLib
import river.exertion.sac.view.SACInputProcessor

class Chart (val chartAspects : List<Aspect>, var firstCelestialSnapshot: CelestialSnapshot, val chartState: ChartState = ChartState.NATAL_CHART) {

    constructor(firstCelestialSnapshot: CelestialSnapshot, secondCelestialSnapshot: CelestialSnapshot
                , chartState: ChartState, aspectsState: AspectsState, timeAspectsState: TimeAspectsState, aspectOverlayState: AspectOverlayState
                , analysisState: AnalysisState
    ) : this (
        getAspects(
            if (chartState == ChartState.COMPOSITE_CHART) {
                CelestialSnapshot.getCompositeSnapshot(firstCelestialSnapshot, secondCelestialSnapshot)
            } else {
                firstCelestialSnapshot
            },
            if (chartState == ChartState.COMPOSITE_CHART) {
                CelestialSnapshot.getCompositeSnapshot(firstCelestialSnapshot, secondCelestialSnapshot)
            } else {
                secondCelestialSnapshot
            },
            chartState,
            aspectsState,
            timeAspectsState,
            aspectOverlayState,
            analysisState
        ), if (chartState == ChartState.COMPOSITE_CHART) {
            CelestialSnapshot.getCompositeSnapshot(firstCelestialSnapshot, secondCelestialSnapshot)
        } else {
            firstCelestialSnapshot
        },
        chartState
    )

    //natal--no need for second snapshot
    constructor(firstCelestialSnapshot: CelestialSnapshot
            , chartState: ChartState
            , aspectsState: AspectsState
            , timeAspectsState: TimeAspectsState
            , aspectOverlayState: AspectOverlayState
        , analysisState: AnalysisState
    ) : this (firstCelestialSnapshot, firstCelestialSnapshot, chartState, aspectsState, timeAspectsState, aspectOverlayState, analysisState)

    val baseValue = getChartBaseValue()
    val modValue = getChartModValue()
    fun netValue() = getChartNetValue()

    fun getAspects() : List<Aspect> =
        chartAspects.filter { it.aspectType != AspectType.ASPECT_NONE }.sortedBy { it.aspectCelestialSecond }.sortedBy { it.aspectCelestialFirst }

    companion object {

        fun getAspects(firstCelestialSnapshot : CelestialSnapshot, secondCelestialSnapshot: CelestialSnapshot = firstCelestialSnapshot,
                       chartState: ChartState, aspectsState: AspectsState, timeAspectsState: TimeAspectsState, aspectOverlayState: AspectOverlayState, analysisState: AnalysisState
        ) : List<Aspect> {

            val firstCelestialAspectMap = firstCelestialSnapshot.getAspectCelestialLongitudeMap().filterKeys { !it.isTimeAspect || (timeAspectsState == TimeAspectsState.TIME_ASPECTS_ENABLED) }
            val secondCelestialAspectMap = secondCelestialSnapshot.getAspectCelestialLongitudeMap().filterKeys { !it.isTimeAspect || (timeAspectsState == TimeAspectsState.TIME_ASPECTS_ENABLED) }

            val returnAspects : MutableList<Aspect> = ArrayList()
            var returnAspectsIdx = 0

            for (firstCelestialAspectEntry in firstCelestialAspectMap) {
                for (secondCelestialAspectEntry in secondCelestialAspectMap) {

                    if ( (chartState != ChartState.SYNASTRY_CHART)
                        && (firstCelestialAspectEntry.key.ordinal >= secondCelestialAspectEntry.key.ordinal) ) continue

                    if ( ( firstCelestialAspectEntry.key == AspectCelestial.ASPECT_ASCENDANT)
                        && (secondCelestialAspectEntry.key == AspectCelestial.ASPECT_MIDHEAVEN)) continue

                    if ( ( firstCelestialAspectEntry.key == AspectCelestial.ASPECT_MIDHEAVEN)
                        && (secondCelestialAspectEntry.key == AspectCelestial.ASPECT_ASCENDANT)) continue

                    val aspect = Aspect(
                        firstCelestialAspectEntry.key
                        , firstCelestialAspectEntry.value
                        , secondCelestialAspectEntry.key
                        , secondCelestialAspectEntry.value
                        , aspectsState, aspectOverlayState, chartState, analysisState
                    )

                    if (aspect.aspectType != AspectType.ASPECT_NONE)
                        returnAspects.add(returnAspectsIdx++, aspect)

                }
            }

            return if (analysisState == AnalysisState.ROMANTIC_ANALYSIS)
                returnAspects.plus(
                    getExtendedAspects(firstCelestialSnapshot, secondCelestialSnapshot, chartState, aspectsState, timeAspectsState, aspectOverlayState, analysisState)
                )
            else returnAspects
        }

        fun getExtendedAspects(firstCelestialSnapshot : CelestialSnapshot, secondCelestialSnapshot : CelestialSnapshot
                               , chartState: ChartState, aspectsState: AspectsState, timeAspectsState: TimeAspectsState, aspectOverlayState: AspectOverlayState, analysisState : AnalysisState
        ) : List<Aspect> {

            val firstCelestialAspectMap = firstCelestialSnapshot.getAspectCelestialLongitudeMap(includeExtendedAspects = true).filterKeys { !it.isTimeAspect || (timeAspectsState == TimeAspectsState.TIME_ASPECTS_ENABLED) }
            val secondCelestialAspectMap = secondCelestialSnapshot.getAspectCelestialLongitudeMap(includeExtendedAspects = true).filterKeys { !it.isTimeAspect || (timeAspectsState == TimeAspectsState.TIME_ASPECTS_ENABLED) }

            val returnAspects : MutableList<Aspect> = mutableListOf()

            var firstExtendedCelestialLongitude : Double?
            var secondExtendedCelestialLongitude : Double?
            var aspect : Aspect

            for(extendedAspect in RomExtAspects.extendedCelestialAspectModifiers) {
                firstExtendedCelestialLongitude = firstCelestialAspectMap[extendedAspect.aspectCelestialFirst]

//                print("checking ${extendedAspect.aspectCelestialFirst}")
                if (firstExtendedCelestialLongitude == null) {
//                    println(": not found")

                    continue
                }

                secondExtendedCelestialLongitude = when (extendedAspect.aspectCelestialSecond) {
                    AspectCelestial.ASPECT_SUN_MOON_MIDPOINT ->
                        secondCelestialAspectMap[AspectCelestial.ASPECT_SUN_MOON_MIDPOINT]
                    AspectCelestial.ASPECT_ASCENDANT ->
                        secondCelestialAspectMap[AspectCelestial.ASPECT_ASCENDANT]
                    //TODO: implement separate 'contains' logic for an aspectCelestial in a house
                    AspectCelestial.ASPECT_FIRST_HOUSE -> SwiLib.midpoint(
                        secondCelestialSnapshot.refCelestialHouseData[CelestialHouse.HOUSE_1_ASC.ordinal],
                        secondCelestialSnapshot.refCelestialHouseData[CelestialHouse.HOUSE_2.ordinal]
                    )
                    AspectCelestial.ASPECT_SEVENTH_HOUSE -> SwiLib.midpoint(
                        secondCelestialSnapshot.refCelestialHouseData[CelestialHouse.HOUSE_7.ordinal],
                        secondCelestialSnapshot.refCelestialHouseData[CelestialHouse.HOUSE_8.ordinal]
                    )
                    else -> secondCelestialAspectMap[extendedAspect.aspectCelestialSecond]
                }

//                print(" against ${extendedAspect.aspectCelestialSecond}")
                if (secondExtendedCelestialLongitude == null) {
//                    println(": not found")

                    continue
                }

                aspect = Aspect(
                        extendedAspect.aspectCelestialFirst
                        , firstExtendedCelestialLongitude
                        , extendedAspect.aspectCelestialSecond
                        , secondExtendedCelestialLongitude
                        , aspectsState, aspectOverlayState, chartState, analysisState
                    )

                if (aspect.aspectType == extendedAspect.aspectType) {
//                    println(": found $aspect")

                    returnAspects.add(aspect)
                } else {
//                    println(": not found")
                }

                if (chartState == ChartState.SYNASTRY_CHART) {
                    //check the reverse
                    firstExtendedCelestialLongitude = when (extendedAspect.aspectCelestialSecond) {
                        AspectCelestial.ASPECT_SUN_MOON_MIDPOINT ->
                            secondCelestialAspectMap[AspectCelestial.ASPECT_SUN_MOON_MIDPOINT]
                        AspectCelestial.ASPECT_ASCENDANT ->
                            secondCelestialAspectMap[AspectCelestial.ASPECT_ASCENDANT]
                        AspectCelestial.ASPECT_FIRST_HOUSE -> SwiLib.midpoint(
                            secondCelestialSnapshot.refCelestialHouseData[CelestialHouse.HOUSE_1_ASC.ordinal],
                            secondCelestialSnapshot.refCelestialHouseData[CelestialHouse.HOUSE_2.ordinal]
                        )
                        AspectCelestial.ASPECT_SEVENTH_HOUSE -> SwiLib.midpoint(
                            secondCelestialSnapshot.refCelestialHouseData[CelestialHouse.HOUSE_7.ordinal],
                            secondCelestialSnapshot.refCelestialHouseData[CelestialHouse.HOUSE_8.ordinal]
                        )
                        else -> secondCelestialAspectMap[extendedAspect.aspectCelestialSecond]
                    }

//                    print("checking ${extendedAspect.aspectCelestialSecond}")
                    if (firstExtendedCelestialLongitude == null) {
//                        println(": not found")

                        continue
                    }

                    secondExtendedCelestialLongitude = secondCelestialAspectMap[extendedAspect.aspectCelestialFirst]

//                    println(" against ${extendedAspect.aspectCelestialFirst}")
                    if (secondExtendedCelestialLongitude == null) {
//                        println(": not found")

                        continue
                    }

                    aspect = Aspect(
                            extendedAspect.aspectCelestialSecond
                            , firstExtendedCelestialLongitude
                            , extendedAspect.aspectCelestialFirst
                            , secondExtendedCelestialLongitude
                            , aspectsState, aspectOverlayState, chartState, analysisState
                        )

                    if (aspect.aspectType == extendedAspect.aspectType) {

                        returnAspects.add(aspect)
                    }
                }

            }

            return returnAspects
        }

        fun getCharacterAspects(firstNatalChart : Chart, secondNatalChart : Chart) : List<Aspect> {

            val returnAspects = mutableListOf<Aspect>()

            val synChart = Chart(
                firstNatalChart.firstCelestialSnapshot
                , secondNatalChart.firstCelestialSnapshot
                , ChartState.SYNASTRY_CHART
                , (SACInputProcessor.aspectsStateMachine.currentState as AspectsState)
                , (SACInputProcessor.timeAspectsStateMachine.currentState as TimeAspectsState)
                , AspectOverlayState.toggleState(ChartState.COMPOSITE_CHART, (SACInputProcessor.aspectOverlayStateMachine.currentState as AspectOverlayState) )
                , (SACInputProcessor.analysisStateMachine.currentState as AnalysisState)
            )

            val compChart = Chart(
                firstNatalChart.firstCelestialSnapshot
                , secondNatalChart.firstCelestialSnapshot
                , ChartState.COMPOSITE_CHART
                , (SACInputProcessor.aspectsStateMachine.currentState as AspectsState)
                , (SACInputProcessor.timeAspectsStateMachine.currentState as TimeAspectsState)
                , AspectOverlayState.toggleState(ChartState.SYNASTRY_CHART, (SACInputProcessor.aspectOverlayStateMachine.currentState as AspectOverlayState) )
                , (SACInputProcessor.analysisStateMachine.currentState as AnalysisState)
            )

            val firstNatalAspects = firstNatalChart.getAspects()
            val secondNatalAspects = secondNatalChart.getAspects()
            val compAspects = compChart.getAspects()
            val synAspects = synChart.getAspects()

            val firstNatalCelestialAspects = firstNatalAspects.map { it.celestialAspect() }
            val secondNatalCelestialAspects = secondNatalAspects.map { it.celestialAspect() }
            val compCelestialAspects = compAspects.map { it.celestialAspect() }
            val synCelestialAspects = synAspects.map { it.celestialAspect() }

            //Appreciation
            firstNatalCelestialAspects.filter {
                firstNatalCelestialAspect -> secondNatalCelestialAspects.contains(firstNatalCelestialAspect)
            }.forEach { appreciationAspect ->
                val firstAspect = firstNatalAspects.aspectFromCelestialAspect(appreciationAspect)
                val secondAspect = secondNatalAspects.aspectFromCelestialAspect(appreciationAspect)

                if (firstAspect != null && secondAspect != null) {
                    returnAspects.add(Aspect(firstAspect).apply {
                        this.aspectValueType = AspectValueType.APPRECIATION_REF
                    })
                    returnAspects.add(Aspect(secondAspect).apply {
                        this.aspectValueType = AspectValueType.APPRECIATION_SYN
                    })
                }
            }

            //Affinity
            firstNatalCelestialAspects.filter {
                firstNatalCelestialAspect -> synCelestialAspects.contains(firstNatalCelestialAspect)
            }.forEach { affinityAspect ->
                val firstAspect = firstNatalAspects.aspectFromCelestialAspect(affinityAspect)
                val secondAspect = synAspects.aspectFromCelestialAspect(affinityAspect)

                if (firstAspect != null && secondAspect != null) {
                    returnAspects.add(Aspect(firstAspect).apply {
                        this.aspectValueType = AspectValueType.REF_NATAL_AFFINITY_REF
                    })
                    returnAspects.add(Aspect(secondAspect).apply {
                        this.aspectValueType = AspectValueType.REF_NATAL_AFFINITY_SYN
                    })
                }
            }

            secondNatalCelestialAspects.filter {
                secondNatalCelestialAspect -> synCelestialAspects.contains(secondNatalCelestialAspect)
            }.forEach { affinityAspect ->
                val firstAspect = secondNatalAspects.aspectFromCelestialAspect(affinityAspect)
                val secondAspect = synAspects.aspectFromCelestialAspect(affinityAspect)

                if (firstAspect != null && secondAspect != null) {
                    returnAspects.add(Aspect(firstAspect).apply {
                        this.aspectValueType = AspectValueType.SYN_NATAL_AFFINITY_REF
                    })
                    returnAspects.add(Aspect(secondAspect).apply {
                        this.aspectValueType = AspectValueType.SYN_NATAL_AFFINITY_SYN
                    })
                }
            }

            //Commonality
            firstNatalCelestialAspects.filter {
                    firstNatalCelestialAspect -> compCelestialAspects.contains(firstNatalCelestialAspect)
            }.forEach { commonalityAspect ->
                val firstAspect = firstNatalAspects.aspectFromCelestialAspect(commonalityAspect)
                val secondAspect = compAspects.aspectFromCelestialAspect(commonalityAspect)

                if (firstAspect != null && secondAspect != null) {
                    returnAspects.add(Aspect(firstAspect).apply {
                        this.aspectValueType = AspectValueType.REF_NATAL_COMMONALITY_REF
                    })
                    returnAspects.add(Aspect(secondAspect).apply {
                        this.aspectValueType = AspectValueType.REF_NATAL_COMMONALITY_SYN
                    })
                }
            }

            secondNatalCelestialAspects.filter {
                    secondNatalCelestialAspect -> compCelestialAspects.contains(secondNatalCelestialAspect)
            }.forEach { commonalityAspect ->
                val firstAspect = secondNatalAspects.aspectFromCelestialAspect(commonalityAspect)
                val secondAspect = compAspects.aspectFromCelestialAspect(commonalityAspect)

                if (firstAspect != null && secondAspect != null) {
                    returnAspects.add(Aspect(firstAspect).apply {
                        this.aspectValueType = AspectValueType.SYN_NATAL_COMMONALITY_REF
                    })
                    returnAspects.add(Aspect(secondAspect).apply {
                        this.aspectValueType = AspectValueType.SYN_NATAL_COMMONALITY_SYN
                    })
                }
            }

            //Compatibility
            compCelestialAspects.filter {
                    compCelestialAspect -> synCelestialAspects.contains(compCelestialAspect)
            }.forEach { compatibilityAspect ->
                val firstAspect = compAspects.aspectFromCelestialAspect(compatibilityAspect)
                val secondAspect = synAspects.aspectFromCelestialAspect(compatibilityAspect)

                if (firstAspect != null && secondAspect != null) {
                    returnAspects.add(Aspect(firstAspect).apply {
                        this.aspectValueType = AspectValueType.COMPATIBILITY_REF
                    })
                    returnAspects.add(Aspect(secondAspect).apply {
                        this.aspectValueType = AspectValueType.COMPATIBILITY_SYN
                    })
                }
            }

            return returnAspects
        }
    }
}