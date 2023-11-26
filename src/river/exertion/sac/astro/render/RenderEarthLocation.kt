package river.exertion.sac.astro.render

import kotlinx.datetime.LocalDateTime
import river.exertion.sac.Constants
import river.exertion.sac.astro.base.EarthLocation
import river.exertion.sac.console.state.EntryState

object RenderEarthLocation {

    fun getEarthLongitudeLabel() = Constants.SYM_EARTH_LONG + "(" + Constants.SYM_EARTH + ")"
    fun getUTCLongitudeLabel() = Constants.SYM_EARTH_LONG + "(" + Constants.SYM_UTC + ")"
    fun getEarthLatitudeLabel() = Constants.SYM_EARTH_LAT + "(" + Constants.SYM_EARTH + ")"
    fun getEarthAltitudeLabel() = Constants.SYM_EARTH_ALT + "(" + Constants.SYM_EARTH + ")"
    fun getEarthTimezoneLabel() = Constants.SYM_TZ + "(" + Constants.SYM_EARTH + ")" //used for manually-input timezone
    fun getEarthUTCTimeLabel() = Constants.SYM_EMPTY_HGLASS + "(" + Constants.SYM_UTC + ")"
    fun getEarthUTCDateLabel() = Constants.SYM_GREG_CALENDAR + "(" + Constants.SYM_UTC + ")"
    fun getEarthLocalTimeLabel() = Constants.SYM_EMPTY_HGLASS + "(" + Constants.SYM_TZ + ")"
    fun getEarthLocalDateLabel() = Constants.SYM_GREG_CALENDAR + "(" + Constants.SYM_TZ + ")"

    fun getRenderTimeLabel(dateTime : LocalDateTime, dateTimeLabel : String) = dateTimeLabel + ": " +
            dateTime.hour.toString().padStart(2, '0') + EntryState.TIME_ENTRY.getDelim() +
            dateTime.minute.toString().padStart(2, '0') + EntryState.TIME_ENTRY.getDelim() +
            dateTime.second.toString().padStart(2, '0')

    fun getRenderLocalTimeLabel(localDateTime : LocalDateTime) = getRenderTimeLabel(localDateTime, getEarthLocalTimeLabel())

    fun getRenderUTCTimeLabel(utcDateTime : LocalDateTime) = getRenderTimeLabel(utcDateTime, getEarthUTCTimeLabel())

    fun getRenderDateLabel(dateTime : LocalDateTime, dateTimeLabel : String) = dateTimeLabel + ": " +
            dateTime.year.toString().padStart(4, '0') + EntryState.DATE_ENTRY.getDelim() +
            dateTime.monthNumber.toString().padStart(2, '0') + EntryState.DATE_ENTRY.getDelim() +
            dateTime.dayOfMonth.toString().padStart(2, '0')

    fun getRenderLocalDateLabel(localDateTime : LocalDateTime) = getRenderDateLabel(localDateTime, getEarthLocalDateLabel())

    fun getRenderUTCDateLabel(utcDateTime : LocalDateTime) = getRenderDateLabel(utcDateTime, getEarthUTCDateLabel())

    fun getRenderLocalTimezoneLabel(timeZoneOffsetString : String) = getEarthTimezoneLabel() + ": " +
            Constants.SYM_UTC + timeZoneOffsetString

    fun getRenderUTCTimezoneLabel() = getUTCLongitudeLabel() + ":" + "0.0".padStart(7, ' ')

    fun getRenderLocalLatitudeLabel(latitude : Double) = getEarthLatitudeLabel() + "[-S]:" +
            latitude.toString().padStart(10, ' ')

    fun getRenderLocalLongitudeLabel(longitude : Double) = getEarthLongitudeLabel() + "[-W]:" +
            longitude.toString().padStart(10, ' ')


    fun getRenderEarthLocationDataFirstLine(earthLocation: EarthLocation) : String {

        val timeZoneOffsetString = earthLocation.getTimezoneOffsetString()
        val postTzPad = 10 - timeZoneOffsetString.length // "+3.0" == 6, "-3.0" == 6, "-12.0" == 5

        return " " + getRenderLocalTimeLabel(earthLocation.localDateTime) + " ".padStart(6, ' ') +
                getRenderLocalDateLabel(earthLocation.localDateTime) + " ".padStart(6, ' ') +
                getRenderLocalTimezoneLabel(earthLocation.getTimezoneOffsetString()) + " ".padStart(postTzPad, ' ') +
                getRenderLocalLatitudeLabel(earthLocation.latitude)
    }

    fun getRenderEarthLocationDataSecondLine(earthLocation: EarthLocation) : String {

        return " " + getRenderUTCTimeLabel(earthLocation.utcDateTime) + " ".padStart(6, ' ') +
                getRenderUTCDateLabel(earthLocation.utcDateTime) + " ".padStart(7, ' ') +
                getRenderUTCTimezoneLabel() + " ".padStart(6, ' ') +
                getRenderLocalLongitudeLabel(earthLocation.longitude)
    }

}