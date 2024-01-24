package river.exertion.sac.astro.base

import river.exertion.sac.astro.base.AspectCelestial
import river.exertion.sac.astro.base.AspectType

data class CelestialAspectModifier(val aspectCelestialFirst : AspectCelestial, val aspectCelestialSecond : AspectCelestial, val aspectType : AspectType, val modifier : Int)