package river.exertion.sac.component

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import river.exertion.kcop.ecs.EngineHandler
import river.exertion.kcop.ecs.component.IComponent
import river.exertion.kcop.messaging.MessageChannelHandler
import river.exertion.kcop.asset.immersionTimer.ImmersionTimer
import river.exertion.kcop.asset.irlTime.IrlTime
import river.exertion.kcop.base.Id
import river.exertion.kcop.profile.Profile
import river.exertion.kcop.profile.asset.ProfileAsset
import river.exertion.kcop.profile.settings.PSCompStatus
import river.exertion.kcop.sim.narrative.NarrativeKlop
import river.exertion.kcop.sim.narrative.NarrativeKlop.NarrativeBridge
import river.exertion.kcop.sim.narrative.asset.NarrativeAsset
import river.exertion.kcop.sim.narrative.asset.NarrativeStateAsset
import river.exertion.kcop.sim.narrative.messaging.NarrativeComponentMessage
import river.exertion.kcop.sim.narrative.structure.ImmersionLocation
import river.exertion.kcop.sim.narrative.structure.ImmersionStatus
import river.exertion.kcop.sim.narrative.structure.Narrative
import river.exertion.kcop.sim.narrative.structure.NarrativeState
import river.exertion.kcop.view.layout.displayViewLayout.DVLayout
import river.exertion.kcop.view.layout.displayViewLayout.DVLayoutHandler
import river.exertion.kcop.view.KcopFont
import river.exertion.kcop.view.layout.DisplayView
import river.exertion.kcop.view.layout.StatusView
import river.exertion.sac.Constants
import river.exertion.sac.SweetAstroConsoleKlop
import river.exertion.sac.astro.base.CelestialSnapshot
import river.exertion.sac.astro.base.EarthLocation
import river.exertion.sac.astro.state.*
import river.exertion.sac.swe.CalcUt
import river.exertion.sac.swe.Houses
import river.exertion.sac.swe.Julday

class SACComponent : IComponent, Telegraph {

    var refProfile : Profile
        get() = ProfileAsset.currentProfileAsset.profile
        set(value) { ProfileAsset.currentProfileAsset.profile = value }

    var synProfile : Profile
        get() = ProfileAsset.currentProfileAsset.profile
        set(value) { ProfileAsset.currentProfileAsset.profile = value }

    override var componentId = Id.randomId()
    override var isInitialized = false

    fun sacRecalc() {
        sacEarthLocation.utcDateTime = sacUTCDateTime
        sacCelestialSnapshot.refEarthLocation = sacEarthLocation
        sacCelestialSnapshot.recalc()
        sacChart = StateChart(StateChart.getAspects(sacCelestialSnapshot, sacCelestialSnapshot, ChartState.NATAL_CHART
            , AspectsState.ALL_ASPECTS, TimeAspectsState.TIME_ASPECTS_ENABLED, AspectOverlayState.ASPECT_NATCOMP_OVERLAY_DEFAULT))
    }

    override fun initialize(initData: Any?) {

        super.initialize(initData)

        DisplayView.currentDisplayViewLayoutHandler = SweetAstroConsoleKlop.displayViewLayoutHandler()

        StatusView.clearStatuses()

        ProfileAsset.currentProfileAsset.profile.execSettings()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if (msg != null) {
            if (MessageChannelHandler.isType(NarrativeBridge, msg.message) && isInitialized) {
                val narrativeComponentMessage: NarrativeComponentMessage = MessageChannelHandler.receiveMessage(NarrativeBridge, msg.extraInfo)

             /*   when (narrativeComponentMessage.narrativeMessageType) {

                    NarrativeComponentMessage.NarrativeMessageType.RemoveBlockCumlTimer -> {
                        blockCumlTimer = false
                    }
                    NarrativeComponentMessage.NarrativeMessageType.ReplaceCumlTimer -> {
                        blockCumlTimer = false
                        IComponent.ecsInit<ImmersionTimerComponent>(initData = ImmersionTimerPair(instImmersionTimer, cumlImmersionTimer))
                    }
                    NarrativeComponentMessage.NarrativeMessageType.ReplaceBlockCumlTimer -> {
                        blockCumlTimer = true
                        IComponent.ecsInit<ImmersionTimerComponent>(initData = ImmersionTimerPair(blockInstImmersionTimer(), blockCumlImmersionTimer()))
                    }
                    NarrativeComponentMessage.NarrativeMessageType.Pause -> pause()
                    NarrativeComponentMessage.NarrativeMessageType.Unpause -> unpause()
                    NarrativeComponentMessage.NarrativeMessageType.Inactivate -> inactivate()
                    NarrativeComponentMessage.NarrativeMessageType.Next -> if (narrativeComponentMessage.promptNext != null) next(narrativeComponentMessage.promptNext)
                    NarrativeComponentMessage.NarrativeMessageType.Refresh -> {
                        this.narrativeState.blockFlags.clear()
                        this.changed = true
                    }
                }*/
                return true
            }
        }
        return false
    }

    companion object {
        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is SACComponent } != null
        fun getFor(entity : Entity) : SACComponent? = if (has(entity)) entity.components.first { it is SACComponent } as SACComponent else null

        //TODO: allow location definitions with lat / long / alt / tz
        //TODO: allow location default set in properties file
        //TODO: allow manual setting of sac lat, long, alt, tz
        var sacLatitude = Constants.LAT_TNM
        var sacLongitude = Constants.LON_TNM
        var sacAltitude = Constants.ALT_TNM
        var sacTimezone = TimeZone.of(Constants.TZ_MST)
        var sacUTCDateTime = IrlTime.utcLocalDateTime()

        var sacEarthLocation = EarthLocation(sacLongitude, sacLatitude, sacAltitude, sacTimezone, sacUTCDateTime)
        var sacCelestialSnapshot = CelestialSnapshot(sacEarthLocation)
        var sacChart = StateChart(StateChart.getAspects(sacCelestialSnapshot, sacCelestialSnapshot, ChartState.NATAL_CHART
            , AspectsState.ALL_ASPECTS, TimeAspectsState.TIME_ASPECTS_ENABLED, AspectOverlayState.ASPECT_NATCOMP_OVERLAY_DEFAULT))


        fun ecsInit() {
            //inactivate current narrative
//            MessageChannelHandler.send(NarrativeBridge, NarrativeComponentMessage(NarrativeComponentMessage.NarrativeMessageType.Inactivate))

            EngineHandler.replaceComponent<SACComponent>()
        }
    }
}