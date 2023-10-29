package river.exertion.sac.astro.state

import river.exertion.sac.astro.base.AspectCelestial
import river.exertion.sac.astro.base.AspectType

data class StateBaseAspect(val aspectCelestialFirst : AspectCelestial
       , val aspectCelestialSecond: AspectCelestial
       , val aspectType : AspectType
)
{
    companion object {
        fun List<StateAspect>.stateBaseAspects() : List<StateBaseAspect> = this.map { it.getStateBaseAspect() }
    }
}