package com.project.agilugr.ui.views


import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.util.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.project.agilugr.R
import com.project.agilugr.ui.components.Header
import com.project.agilugr.utils.MainBackground
import com.project.agilugr.utils.headerBackground
import com.project.agilugr.utils.lightGrey
import java.time.format.DateTimeFormatter


const val WEIGHT_7DAY_WEEK = 1 / 7f

/**
 * Contains metadata information about a certain day
 *
 * @property day Indicates the day number (1-31)
 *
 * @property dayOfWeek Contains DayOfWeek information about the day (Monday-Sunday)
 *
 * @property day Information about the month the date belongs to
 *
 */
data class CalposeDate (val day: Int, val dayOfWeek: DayOfWeek, val month: YearMonth)


/**
 * Describes the various actions which can be triggered from within the Calpose renderer
 *
 * @property onClickedPreviousMonth Called upon pressing the previous month button
 *
 * @property onClickedNextMonth Called upon pressing the next month button
 *
 * @property onSwipedPreviousMonth Called upon swiping towards the previous month
 *
 * @property onSwipedNextMonth Called upon swiping towards the next month
 */
data class CalposeActions (
        val onClickedPreviousMonth: () -> Unit,
        val onClickedNextMonth: () -> Unit,
        val onSwipedPreviousMonth: () -> Unit = onClickedPreviousMonth,
        val onSwipedNextMonth: () -> Unit = onClickedNextMonth
)


/**
 * Describes the various widget types which can be rendered within Calpose
 *
 * @property header Widget which defines the header of the calendar (excl. the DayOfWeek row)
 *
 * @property headerDayRow Widget which defines the DayOfWeek row
 *
 * @property day Widget which defines the look of a day item in the currently selected month
 *
 * @property priorMonthDay Widget which defines the look of a day item of the previous month
 *
 * @property nextMonthDay Widget which defines the look of a day item of the next month
 *
 * @property headerContainer Widget which can define a container layout for the *header* & *headerDayRow*.
 *                           Useful if you want to place the header inside of a *Card* f.e.
 *
 * @property monthContainer Widget which can define a container layout for the month overview.
 *                          Can be used to change the background color, change animation of the
 *                          month overview
 *
 * @property weekContainer Widget which can define a container layout for the week. Can be
 *                          used to change the background color of the week or make dividers between them.
 */
data class CalposeWidgets(
        val header: @Composable (month: YearMonth, todayMonth: YearMonth, actions: CalposeActions) -> Unit,
        val headerDayRow: @Composable (headerDayList: Set<DayOfWeek>) -> Unit,
        val day: @Composable RowScope.(dayDate: CalposeDate, todayDate: CalposeDate) -> Unit,
        val priorMonthDay: @Composable RowScope.(dayDate: CalposeDate) -> Unit,
        val nextMonthDay: @Composable RowScope.(dayDate: CalposeDate) -> Unit = priorMonthDay,
        val headerContainer: @Composable (@Composable () -> Unit) -> Unit = { it() },
        val monthContainer: @Composable (@Composable () -> Unit) -> Unit = { it() },
        val weekContainer: @Composable (@Composable () -> Unit) -> Unit = { it() }
)

/**
 * Describes various properties used within Calpose
 *
 * @property changeMonthAnimation Set the animation type and properties when changing month
 *
 * @property changeMonthSwipeTriggerVelocity Define the velocity necessary to change month when swiping
 *
 */

data class CalposeProperties (
        val changeMonthAnimation: FiniteAnimationSpec<Float> = tween(durationMillis = 200),
        val changeMonthSwipeTriggerVelocity: Int = 300
)


class Calendario (val navController: NavController){
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun getView() {
        var month by remember { mutableStateOf(YearMonth.now()) }

        Box(modifier = Modifier
            .background(Color(MainBackground))
            .fillMaxSize()){
            
            Spacer(modifier = Modifier.height(100.dp))
            Box(modifier = Modifier
                .background(Color(headerBackground))
                .fillMaxWidth()
                .height(50.dp)
                .clip(
                    RoundedCornerShape(20.dp)
                )) {
                Column(

                    // Lo espaciamos algo respecto el extremo superior del telefono y respecto el borde izquierdo
                    modifier = Modifier
                        .padding(vertical = 0.dp, horizontal = 20.dp),
                ) {
                    Header().getComponent()

                }
            }
            Column() {
                DefaultCalendar(
                    month = month,
                    actions = CalposeActions(
                        onClickedPreviousMonth = { month = month.minusMonths(1) },
                        onClickedNextMonth = { month = month.plusMonths(1) }
                    )
                )
            }

        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DefaultCalendar(
        month: YearMonth,
        actions: CalposeActions
) {
        Calpose(
            month = month,
            actions = actions,
            widgets = CalposeWidgets(
                header = { month, todayMonth, actions ->
                    DefaultHeader(month, todayMonth, actions)
                },
                headerDayRow = { headerDayList ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(bottom = 16.dp),
                    ) {
                        headerDayList.forEach {
                            DefaultDay(
                                text = it.name.first().toString(),
                                modifier = Modifier.weight(WEIGHT_7DAY_WEEK),
                                style = TextStyle(color = Color.Gray)
                            )
                        }
                    }
                },
                day = { dayDate, todayDate ->
                    val isToday = dayDate == todayDate
                    val dayHasPassed = dayDate.day < todayDate.day
                    val isCurrentMonth = dayDate.month == todayDate.month

                    val widget: @Composable () -> Unit = {
                        val weight = if (isToday) 1f else WEIGHT_7DAY_WEEK
                        DefaultDay(
                            text = dayDate.day.toString(),
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(weight)
                                .fillMaxWidth(),
                            style = TextStyle(
                                color = when {
                                    isCurrentMonth && dayHasPassed -> Color.Gray
                                    isToday -> Color.White
                                    else -> Color.Black
                                },
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }

                    if (isToday) {
                        Column(
                            modifier = Modifier.weight(WEIGHT_7DAY_WEEK),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF7ABEB8))
                            ) {
                                widget()
                            }
                        }
                    } else widget()
                },
                priorMonthDay = { dayDate ->
                    DefaultDay(
                        text = dayDate.day.toString(),
                        style = TextStyle(color = Color(lightGrey)),
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .weight(WEIGHT_7DAY_WEEK)
                    )
                },
                headerContainer = { header ->
                    Card {
                        header()
                    }
                },
            )
        )
}


//You can Also do this without a library, you can put it inside onclick action

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calpose(
        modifier: Modifier = Modifier,
        month: YearMonth,
        actions: CalposeActions,
        widgets: CalposeWidgets,
        properties: CalposeProperties = CalposeProperties()
) {
    Crossfade(
            targetState = month,
            animationSpec = properties.changeMonthAnimation
    ) {
        CalposeStatic(
                modifier = modifier,
                month = it,
                actions = actions,
                widgets = widgets
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalposeStatic(
        modifier: Modifier = Modifier,
        month: YearMonth,
        actions: CalposeActions,
        widgets: CalposeWidgets,
        properties: CalposeProperties = CalposeProperties()
) {
    val todayMonth = remember { YearMonth.now() }

    Column(
            modifier = modifier.draggable(
                    orientation = Orientation.Horizontal,
                    state = DraggableState {},
                    onDragStopped = { velocity ->
                        if (velocity > properties.changeMonthSwipeTriggerVelocity) {
                            actions.onSwipedPreviousMonth()
                        } else if (velocity < -properties.changeMonthSwipeTriggerVelocity) {
                            actions.onSwipedNextMonth()
                        }
                    })
    ) {
        Box(modifier = Modifier
            .background(Color(headerBackground))
            .fillMaxWidth()
            .height(50.dp)
            .clip(
                RoundedCornerShape(20.dp)
            )) {
            Column(

                // Lo espaciamos algo respecto el extremo superior del telefono y respecto el borde izquierdo
                modifier = Modifier
                    .padding(vertical = 0.dp, horizontal = 20.dp),
            ) {
                Header().getComponent()

            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        CalposeHeader(month, todayMonth, actions, widgets)
        widgets.monthContainer { CalposeMonth(month, todayMonth, widgets) }
    }
}

@Composable
fun CalposeHeader(
        month: YearMonth,
        todayMonth: YearMonth,
        actions: CalposeActions,
        widgets: CalposeWidgets
) {
    widgets.headerContainer {
        Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            widgets.header(month, todayMonth, actions)
            widgets.headerDayRow(DayOfWeek.values().toSet())
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalposeMonth(month: YearMonth, todayMonth: YearMonth, widgets: CalposeWidgets) {

    val firstDayOffset = month.atDay(1).dayOfWeek.ordinal
    val monthLength = month.lengthOfMonth()
    val priorMonthLength = month.minusMonths(1).lengthOfMonth()
    val lastDayCount = (monthLength + firstDayOffset) % 7
    val weekCount = (firstDayOffset + monthLength) / 7
    val today = SimpleDateFormat("dd").format(Date(System.currentTimeMillis())).toInt()

    for (i in 0..weekCount) {
        widgets.weekContainer {
            CalposeWeek(
                    startDayOffSet = firstDayOffset,
                    endDayCount = lastDayCount,
                    monthWeekNumber = i,
                    weekCount = weekCount,
                    priorMonthLength = priorMonthLength,
                    today = CalposeDate(
                            day = today,
                            dayOfWeek = todayMonth.atDay(today).dayOfWeek,
                            month = todayMonth
                    ),
                    month = month,
                    widgets = widgets
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalposeWeek(
        startDayOffSet: Int,
        endDayCount: Int,
        monthWeekNumber: Int,
        weekCount: Int,
        priorMonthLength: Int,
        today: CalposeDate,
        month: YearMonth,
        widgets: CalposeWidgets
) {
    Row {
        var dayOfWeekOrdinal = 1
        if (monthWeekNumber == 0) {
            for (i in 1 .. startDayOffSet) {
                val priorDay = (priorMonthLength - (startDayOffSet - i))
                widgets.priorMonthDay(
                        this,
                        CalposeDate(
                                priorDay,
                                DayOfWeek.of(dayOfWeekOrdinal++),
                                month.minusMonths(1)
                        )
                )
            }
        }

        val endDay = when (monthWeekNumber) {
            0 -> 7 - startDayOffSet
            weekCount -> endDayCount
            else -> 7
        }

        for (i in 1..endDay) {
            val day = if (monthWeekNumber == 0) i else (i + (7 * monthWeekNumber) - startDayOffSet)
            widgets.day(
                    this,
                    CalposeDate(day, DayOfWeek.of(dayOfWeekOrdinal++), month),
                    today
            )
        }

        if (monthWeekNumber == weekCount && endDayCount > 0) {
            for (i in 0 until (7 - endDayCount)) {
                val nextMonthDay = i + 1
                widgets.nextMonthDay(
                        this, CalposeDate(
                        nextMonthDay,
                        DayOfWeek.of(dayOfWeekOrdinal++),
                        month.plusMonths(1)
                )
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DefaultHeader(
        month: YearMonth,
        todayMonth: YearMonth,
        actions: CalposeActions
) {
    val isCurrentMonth = todayMonth == month
    Row {
        leftIcon(actions = actions)

        Spacer(modifier = Modifier.weight(1f))


        DefaultMonthTitle(month = month,isCurrentMonth = isCurrentMonth)

        Spacer(modifier = Modifier.weight(1f))

        rightIcon(actions = actions)
    }
}

@Composable
fun leftIcon(actions:CalposeActions) {
    IconButton(modifier = Modifier
        .padding(16.dp)
        .size(20.dp)
        , onClick = { actions.onClickedPreviousMonth()}
    ) {
        Column(){
            Image(painter = painterResource(id = R.drawable.icono_izda), contentDescription ="IconoCalendario")
        }
    }
}

@Composable
fun rightIcon(actions:CalposeActions) {
    IconButton(modifier = Modifier
        .padding(16.dp)
        .size(20.dp)
        , onClick = { actions.onClickedNextMonth()}
    ) {
        Column(){
            Image(painter = painterResource(id = R.drawable.icono_dcha), contentDescription ="IconoCalendario")
        }
    }
}
@Composable
fun DefaultDay(
        text: String,
        modifier: Modifier = Modifier.padding(4.dp),
        style: TextStyle = TextStyle()
) {
    Text(
            text,
            modifier = modifier,
            textAlign = TextAlign.Center,
            style = style
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun DefaultMonthTitle(
        month: YearMonth,
        isCurrentMonth:Boolean = false,
        textStyle: TextStyle = TextStyle()
){
    val title = remember(month){
        val formatter = DateTimeFormatter.ofPattern("MMMM  yyyy")
        month.format(formatter)
    }

    Text(
            text = title,
            modifier = Modifier.padding(vertical = 8.dp),
            style = TextStyle(
                    fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 22.sp,
            ).merge(textStyle)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview("NonCurrentMonth",widthDp = 200,heightDp = 40)
@Composable
fun NonCurrentMonthPreview(){
    DefaultMonthTitle(month = YearMonth.of(2020,10), isCurrentMonth = false)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview("CurrentMonth",widthDp = 200,heightDp = 40)
@Composable
fun CurrentMonthPreview(){
    DefaultMonthTitle(month = YearMonth.of(2020,8), isCurrentMonth = true)
}