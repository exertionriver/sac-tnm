package river.exertion.sac.astro.value

import river.exertion.sac.astro.AspectCelestial
import river.exertion.sac.astro.base.AspectType
import river.exertion.sac.astro.SignElement
import river.exertion.sac.astro.SignMode
import river.exertion.sac.console.state.AnalysisState
import river.exertion.sac.console.state.ChartState
import river.exertion.sac.astro.state.StateAspect
import river.exertion.sac.astro.state.StateAspectType
import kotlin.math.abs

//fourth chart aspects are for the opposite of current chartType for compSyn charts
//if chartState == SYNASTRY_CHART, fourth chart aspects are aspects that the composite chart shares with the natal charts
//if chartState == COMPOSITE_CHART, fourth chart aspects are aspects that the synastry chart shares with the natal charts
data class ValueAspect (val stateAspect : StateAspect, val chartState: ChartState = ChartState.NATAL_CHART, val analysisState: AnalysisState = AnalysisState.NO_ANALYSIS, val characterModifier: Int = 0, val fourthChartAspect: Boolean = false) {

    val baseValue = getAspectBaseValue()

    //fun baseValue = baseValue
    fun getModValue() = if (chartState == ChartState.SYNASTRY_CHART) Value(getPositiveModValue() / 2, getNegativeModValue() / 2) else Value(getPositiveModValue(), getNegativeModValue())

    fun getPositiveBaseValue() = baseValue.positive
    fun getNegativeBaseValue() = baseValue.negative

    fun getBaseModNetValue() = Value(baseValue.positive + getModValue().positive, baseValue.negative + getModValue().negative)

    fun getAspectModifier() = when (analysisState) {
        AnalysisState.ROMANTIC_ANALYSIS -> getRomanticModifier()
        AnalysisState.CHARACTER_ANALYSIS -> characterModifier
        else -> 0
    }

    fun getSignFirstModifier() = when (analysisState) {
        AnalysisState.ELEMENT_ANALYSIS -> getElementModifier(stateAspect.signFirst.signElement).toInt()
        AnalysisState.MODE_ANALYSIS -> getModeModifier(stateAspect.signFirst.signMode).toInt()
        else -> 0
    }
    fun getSignSecondModifier() = when (analysisState) {
        AnalysisState.ELEMENT_ANALYSIS -> getElementModifier(stateAspect.signSecond.signElement).toInt()
        AnalysisState.MODE_ANALYSIS -> getModeModifier(stateAspect.signSecond.signMode).toInt()
        else -> 0
    }
    fun getAspectCelestialFirstModifier() = when (analysisState) {
        AnalysisState.PLANET_ANALYSIS -> getCelestialModifier(stateAspect.aspectCelestialFirst).toInt()
        else -> 0
    }
    fun getAspectCelestialSecondModifier() = when (analysisState) {
        AnalysisState.PLANET_ANALYSIS -> getCelestialModifier(stateAspect.aspectCelestialSecond).toInt()
        else -> 0
    }

    private fun getRomanticModifier() : Int {

        //romMod is stored as 'orb' in baseAspect
        return ValueRomanticAspects.romanticAspects.firstOrNull() {
            (   (   ( (it.aspectCelestialFirst == stateAspect.aspectCelestialFirst) && (it.aspectCelestialSecond == stateAspect.aspectCelestialSecond) ) ||
                    ( (it.aspectCelestialFirst == stateAspect.aspectCelestialSecond) && (it.aspectCelestialSecond == stateAspect.aspectCelestialFirst) ) )
                    && (it.aspectAngle == stateAspect.aspectAngle) )}?.orb?.toInt() ?: 0
    }

//    private fun getCharacterModifier() = characterModifier

    private fun getElementModifier(signElement : SignElement) : Double {
        val aspectCelestialFirstWeight = ValueAspectCelestial.fromName(stateAspect.aspectCelestialFirst.toString())!!.getWeight()
        val aspectCelestialSecondWeight = ValueAspectCelestial.fromName(stateAspect.aspectCelestialSecond.toString())!!.getWeight()
        val aspectCelestialBothWeight = aspectCelestialFirstWeight + aspectCelestialSecondWeight
        var returnWeight = 0.0

        if (stateAspect.signFirst.signElement == signElement) returnWeight += aspectCelestialFirstWeight
        if (stateAspect.signSecond.signElement == signElement) returnWeight += aspectCelestialSecondWeight

        return returnWeight / aspectCelestialBothWeight
    }

    private fun getModeModifier(signMode : SignMode) : Double {
        val aspectCelestialFirstWeight = ValueAspectCelestial.fromName(stateAspect.aspectCelestialFirst.toString())!!.getWeight()
        val aspectCelestialSecondWeight = ValueAspectCelestial.fromName(stateAspect.aspectCelestialSecond.toString())!!.getWeight()
        val aspectCelestialBothWeight = aspectCelestialFirstWeight + aspectCelestialSecondWeight
        var returnWeight = 0.0

        if (stateAspect.signFirst.signMode == signMode) returnWeight += aspectCelestialFirstWeight
        if (stateAspect.signSecond.signMode == signMode) returnWeight += aspectCelestialSecondWeight

        return returnWeight / aspectCelestialBothWeight
    }

    private fun getCelestialModifier(aspectCelestial: AspectCelestial) : Double {
        val aspectCelestialFirstWeight = ValueAspectCelestial.fromName(stateAspect.aspectCelestialFirst.toString())!!.getWeight()
        val aspectCelestialSecondWeight = ValueAspectCelestial.fromName(stateAspect.aspectCelestialSecond.toString())!!.getWeight()
        val aspectCelestialBothWeight = aspectCelestialFirstWeight + aspectCelestialSecondWeight
        var returnWeight = 0.0

        if (stateAspect.aspectCelestialFirst == aspectCelestial) returnWeight += aspectCelestialFirstWeight
        if (stateAspect.aspectCelestialSecond == aspectCelestial) returnWeight += aspectCelestialSecondWeight

        return returnWeight / aspectCelestialBothWeight
    }

    fun getPositiveModValue() : Int {

        var modPos = 0

        if (analysisState == AnalysisState.CHARACTER_ANALYSIS) return modPos

        modPos += when {
            (getAspectModifier() > 0) -> (baseValue.positive * abs(getAspectModifier()) * .25).toInt()
            (getAspectModifier() < 0) -> (baseValue.positive * -abs(getAspectModifier()) * .25).toInt()
            else -> 0
        }

        modPos += when {
            (baseValue.getNet() > 0) -> when {
                (getSignFirstModifier() > 0) -> (baseValue.positive * abs(getSignFirstModifier()) * .125).toInt()
                (getSignFirstModifier() < 0) -> (baseValue.positive * -abs(getSignFirstModifier()) * .125).toInt()
                else -> 0
            }
            else -> 0
        }
        modPos += when {
            (baseValue.getNet() > 0) -> when {
                (getSignSecondModifier() > 0) -> (baseValue.positive * abs(getSignSecondModifier()) * .125).toInt()
                (getSignSecondModifier() < 0) -> (baseValue.positive * -abs(getSignSecondModifier()) * .125).toInt()
                else -> 0
            }
            else -> 0
        }
        modPos += when {
            (baseValue.getNet() > 0) -> when {
                (getAspectCelestialFirstModifier() > 0) -> (baseValue.positive * abs(getAspectCelestialFirstModifier()) * .125).toInt()
                (getAspectCelestialFirstModifier() < 0) -> (baseValue.positive * -abs(getAspectCelestialFirstModifier()) * .125).toInt()
                else -> 0
            }
            else -> 0
        }
        modPos += when {
            (baseValue.getNet() > 0) -> when {
                (getAspectCelestialSecondModifier() > 0) -> (baseValue.positive * abs(getAspectCelestialSecondModifier()) * .125).toInt()
                (getAspectCelestialSecondModifier() < 0) -> (baseValue.positive * -abs(getAspectCelestialSecondModifier()) * .125).toInt()
                else -> 0
            }
            else -> 0
        }

        return modPos
    }

    fun getNegativeModValue() : Int {

        var modNeg = 0

        if (analysisState == AnalysisState.CHARACTER_ANALYSIS) return modNeg

        modNeg += when {
            (getAspectModifier() > 0) -> (baseValue.negative * -abs(getAspectModifier()) * .25).toInt()
            (getAspectModifier() < 0) -> (baseValue.negative * abs(getAspectModifier()) * .25).toInt()
            else -> 0
        }

        modNeg += when {
            (baseValue.getNet() < 0) -> when {
                (getSignFirstModifier() > 0) -> (baseValue.negative * -abs(getSignFirstModifier()) * .125).toInt()
                (getSignFirstModifier() < 0) -> (baseValue.negative * abs(getSignFirstModifier()) * .125).toInt()
                else -> 0
            }
            else -> 0
        }
        modNeg += when {
            (baseValue.getNet() < 0) -> when {
                (getSignSecondModifier() > 0) -> (baseValue.negative * -abs(getSignSecondModifier()) * .125).toInt()
                (getSignSecondModifier() < 0) -> (baseValue.negative * abs(getSignSecondModifier()) * .125).toInt()
                else -> 0
            }
            else -> 0
        }
        modNeg += when {
            (baseValue.getNet() < 0) -> when {
                (getAspectCelestialFirstModifier() > 0) -> (baseValue.negative * -abs(getAspectCelestialFirstModifier()) * .125).toInt()
                (getAspectCelestialFirstModifier() < 0) -> (baseValue.negative * abs(getAspectCelestialFirstModifier()) * .125).toInt()
                else -> 0
            }
            else -> 0
        }
        modNeg += when {
            (baseValue.getNet() < 0) -> when {
                (getAspectCelestialSecondModifier() > 0) -> (baseValue.negative * -abs(getAspectCelestialSecondModifier()) * .125).toInt()
                (getAspectCelestialSecondModifier() < 0) -> (baseValue.negative * abs(getAspectCelestialSecondModifier()) * .125).toInt()
                else -> 0
            }
            else -> 0
        }

        return modNeg
    }

    private fun getAspectBaseValue() : Value {

        if (ValueAspectType.fromName(stateAspect.aspectAngle.aspectType.name)!!.isNeutral()) return Value(0, 0)

        val weightFirst = ValueAspectCelestial.fromName(stateAspect.aspectCelestialFirst.toString())!!.getWeight()
        val weightSecond = ValueAspectCelestial.fromName(stateAspect.aspectCelestialSecond.toString())!!.getWeight()
        val weightAspect = StateAspectType.of(stateAspect.aspectAngle).getAspectAngleOrb(stateAspect.aspectOverlayState)

        //full weightAspect at orb = 0, down to 0 weightAspect at the cusp of the orb
        val weightOrbAspect = ((60 * weightAspect) - (60 * stateAspect.orb)) / (60 * weightAspect)

        //      debugging with lldb shows rounding error between AstroSWE and SAC -- e.g. aspectWeight for 4.9 in SAC is 4.89999999998 in AstroSWE, leading to rounding diffs
        var aspectValue = ( (weightFirst * weightSecond) / 2 * weightAspect * weightOrbAspect).toInt()

        //  halve for synastry chart
        if (chartState == ChartState.SYNASTRY_CHART) aspectValue /= 2

//        println (" orb)" + stateAspect.orb + ": 1W)" + weightFirst + " 2W)" + weightSecond + " aspectW)" + weightAspect + " orbAspectW)" + weightOrbAspect + " value)" + aspectValue )

        return when {
            //hard aspects to sun / moon midpoint are positive
            ( (stateAspect.aspectCelestialFirst == AspectCelestial.ASPECT_SUN_MOON_MIDPOINT
                    || stateAspect.aspectCelestialSecond == AspectCelestial.ASPECT_SUN_MOON_MIDPOINT) && (
                    (stateAspect.aspectAngle.aspectType == AspectType.CONJUNCTION)
                            || (stateAspect.aspectAngle.aspectType == AspectType.OPPOSITION)
                            || (stateAspect.aspectAngle.aspectType == AspectType.SEMISQUARE)
                            || (stateAspect.aspectAngle.aspectType == AspectType.SQUARE) ) ) -> Value(aspectValue, 0)
            //      https://en.wikipedia.org/wiki/Astrological_aspect#Conjunction
            //      In particular, conjunctions involving the Sun, Venus, and/or Jupiter, in any of the three possible conjunction combinations, are
            //      considered highly favourable, while conjunctions involving the Moon, Mars, and/or Saturn, again in any of the three possible
            //      conjunction combinations, are considered highly unfavourable.
            ( (stateAspect.aspectAngle.aspectType == AspectType.CONJUNCTION) &&
                    (stateAspect.aspectCelestialFirst == AspectCelestial.ASPECT_MOON && (stateAspect.aspectCelestialSecond == AspectCelestial.ASPECT_MARS || stateAspect.aspectCelestialSecond == AspectCelestial.ASPECT_SATURN) )
                    || (stateAspect.aspectCelestialFirst == AspectCelestial.ASPECT_MARS && (stateAspect.aspectCelestialSecond == AspectCelestial.ASPECT_MOON || stateAspect.aspectCelestialSecond == AspectCelestial.ASPECT_SATURN) )
                    || (stateAspect.aspectCelestialFirst == AspectCelestial.ASPECT_SATURN && (stateAspect.aspectCelestialSecond == AspectCelestial.ASPECT_MOON || stateAspect.aspectCelestialSecond == AspectCelestial.ASPECT_MARS) ) ) -> Value(0, -aspectValue)
            (ValueAspectType.fromName(stateAspect.aspectAngle.aspectType.name)!!.isPositive()) -> Value(aspectValue, 0)
            (ValueAspectType.fromName(stateAspect.aspectAngle.aspectType.name)!!.isNegative()) -> Value(0, -aspectValue)
            else -> Value(0, 0)
        }
    }

    override fun toString() = "ValueAspect($stateAspect $analysisState) : baseValue:${baseValue} modValue:${getModValue()} AspectMod:${getAspectModifier()} signFirstMod:${getSignFirstModifier()} signSecondMod:${getSignSecondModifier()} aspectCelFirstMod:${getAspectCelestialFirstModifier()} aspectCelSecondMod:${getAspectCelestialSecondModifier()}"

    companion object {

        fun getEmptyAspect(firstAspectCelestialOverride : AspectCelestial = AspectCelestial.ASPECT_CELESTIAL_NONE,
                           secondAspectCelestialOverride : AspectCelestial = AspectCelestial.ASPECT_CELESTIAL_NONE) = ValueAspect(
            StateAspect.getEmptyAspect())

        fun List<ValueAspect>.valueAspectReduceBase() : Value {
            val pos = this.sumOf { it.getPositiveBaseValue() }
            val neg = this.sumOf { it.getNegativeBaseValue() }

            return Value(pos, neg)
        }

        fun List<ValueAspect>.valueAspectReduceBaseModNet() : Value {

            var pos = 0
            var neg = 0

            this.forEach {
                val modNetValue = it.getBaseModNetValue()
                pos += modNetValue.positive
                neg += modNetValue.negative
            }

            return Value(pos, neg)
        }
/*
        fun List<ValueAspect>.getCharacterValue(curChart : ValueChart, chartValueType : ChartValueType) : Value {

            return this.filter { it.getAspectModifier().decodeChartStateType().contains(chartStateType) }.valueAspectReduceBaseModNet()
        }
*/
    }
}