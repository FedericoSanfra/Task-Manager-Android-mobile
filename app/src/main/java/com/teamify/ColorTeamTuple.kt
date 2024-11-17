package com.teamify


import androidx.compose.ui.graphics.Color
import co.yml.charts.common.model.Point

data class ColorTeamTuple(var selected: Boolean, var color: Color, var teamName: String)

data class PointsData(var points: List<Point>)

fun PointsData.getList(): List<Point>{
    return points
}