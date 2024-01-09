package river.exertion.sac.view

import com.badlogic.gdx.graphics.g2d.BitmapFont
import river.exertion.kcop.asset.AssetManagerHandler
import river.exertion.kcop.asset.view.ColorPalette
import river.exertion.kcop.view.KcopFont
import river.exertion.kcop.view.klop.IDisplayViewLayoutHandler
import river.exertion.kcop.view.layout.displayViewLayout.DVLayoutHandler
import river.exertion.sac.astro.base.AspectValueType
import river.exertion.sac.astro.base.ValueType
import river.exertion.sac.console.state.ChartState

object SACLayoutHandler : IDisplayViewLayoutHandler {

    var baseColorName = "darkGray"
    var baseColor = ColorPalette.of(baseColorName)
    var baseFontColor = ColorPalette.of("cornflowerBlue")
    var baseValuesFontColor = ColorPalette.of("paleGoldenrod")

    var highlightFontColor = baseFontColor.incr(2)

    var fireFontColor = ColorPalette.of("maroon")
    var earthFontColor = ColorPalette.of("olive")
    var airFontColor = ColorPalette.of("green")
    var waterFontColor = ColorPalette.of("navy")

    var positiveFontColor = ColorPalette.of("green")
    var negativeFontColor = ColorPalette.of("maroon")
    var neutralFontColor = ColorPalette.of("paleGoldenrod")
    var reversalFontColor = ColorPalette.of("yellow")

    var refEarthLocationFontColor = ColorPalette.of("teal")
    var synEarthLocationFontColor = ColorPalette.of("purple")

    var refCelestialSnapshotFontColor = refEarthLocationFontColor
    var synCelestialSnapshotFontColor = synEarthLocationFontColor
    var compCelesitalSnapshotFontColor = ColorPalette.of("olive")

    override fun build() {
        DVLayoutHandler.currentDvLayout = SACCelestialsHousesDVLayout.setLayout()
        DVLayoutHandler.currentFontSize = KcopFont.TEXT

        KcopFont.TEXT.font = AssetManagerHandler.getAssets<BitmapFont>().firstOrNull { it.data.name.contains("CODE2000") }.apply { this?.data?.setScale(KcopFont.TEXT.fontScale) }

        SACCelestialsHousesDVLayout.setContent()
        DVLayoutHandler.build()

    }

    override fun clearContent() {
        DVLayoutHandler.currentDvLayout.clearContent()
    }

    fun fontColor(valueType : ValueType) : ColorPalette {
        return when (valueType) {
            ValueType.POSITIVE -> positiveFontColor
            ValueType.NEGATIVE -> negativeFontColor
            ValueType.NEUTRAL -> neutralFontColor
            else -> reversalFontColor
        }
    }

    fun refFontColor(chartState: ChartState, aspectValueType: AspectValueType) : ColorPalette {

        return when (chartState) {
            ChartState.COMPOSITE_CHART -> compCelesitalSnapshotFontColor
            ChartState.ANALYSIS_CHART -> when (aspectValueType) {
                AspectValueType.APPRECIATION_REF -> refCelestialSnapshotFontColor
                AspectValueType.APPRECIATION_SYN -> synCelestialSnapshotFontColor
                AspectValueType.REF_NATAL_AFFINITY_REF -> refCelestialSnapshotFontColor
                AspectValueType.REF_NATAL_AFFINITY_SYN -> refCelestialSnapshotFontColor
                AspectValueType.SYN_NATAL_AFFINITY_REF -> refCelestialSnapshotFontColor
                AspectValueType.SYN_NATAL_AFFINITY_SYN -> synCelestialSnapshotFontColor
                AspectValueType.REF_NATAL_COMMONALITY_REF -> refCelestialSnapshotFontColor
                AspectValueType.REF_NATAL_COMMONALITY_SYN -> compCelesitalSnapshotFontColor
                AspectValueType.SYN_NATAL_COMMONALITY_REF -> compCelesitalSnapshotFontColor
                AspectValueType.SYN_NATAL_COMMONALITY_SYN -> synCelestialSnapshotFontColor
                AspectValueType.COMPATIBILITY_REF -> compCelesitalSnapshotFontColor
                AspectValueType.COMPATIBILITY_SYN -> refCelestialSnapshotFontColor
                else -> refCelestialSnapshotFontColor
            }
            else -> refCelestialSnapshotFontColor
        }
    }

    fun synFontColor(chartState: ChartState, aspectValueType: AspectValueType) : ColorPalette {

        return when (chartState) {
            ChartState.COMPOSITE_CHART -> compCelesitalSnapshotFontColor
            ChartState.NATAL_CHART -> refCelestialSnapshotFontColor
            ChartState.ANALYSIS_CHART -> when (aspectValueType) {
                AspectValueType.APPRECIATION_REF -> refCelestialSnapshotFontColor
                AspectValueType.APPRECIATION_SYN -> synCelestialSnapshotFontColor
                AspectValueType.REF_NATAL_AFFINITY_REF -> refCelestialSnapshotFontColor
                AspectValueType.REF_NATAL_AFFINITY_SYN -> synCelestialSnapshotFontColor
                AspectValueType.SYN_NATAL_AFFINITY_REF -> synCelestialSnapshotFontColor
                AspectValueType.SYN_NATAL_AFFINITY_SYN -> synCelestialSnapshotFontColor
                AspectValueType.REF_NATAL_COMMONALITY_REF -> refCelestialSnapshotFontColor
                AspectValueType.REF_NATAL_COMMONALITY_SYN -> compCelesitalSnapshotFontColor
                AspectValueType.SYN_NATAL_COMMONALITY_REF -> compCelesitalSnapshotFontColor
                AspectValueType.SYN_NATAL_COMMONALITY_SYN -> synCelestialSnapshotFontColor
                AspectValueType.COMPATIBILITY_REF -> compCelesitalSnapshotFontColor
                AspectValueType.COMPATIBILITY_SYN -> synCelestialSnapshotFontColor
                else -> synCelestialSnapshotFontColor
            }
            else -> synCelestialSnapshotFontColor
        }
    }

}