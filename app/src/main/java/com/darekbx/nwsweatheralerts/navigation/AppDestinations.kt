package com.darekbx.shoppinglist.navigation

interface AppDestinations {
    val route: String
}

object MapDestination : AppDestinations {
    override val route = "map"
}

object ListDestination : AppDestinations {
    override val route = "list"
}