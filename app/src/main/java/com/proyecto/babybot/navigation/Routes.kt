package com.proyecto.babybot.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val TRIALINFO = "trialinfo"
    const val FORUM = "forum"
    const val DAILYLOG = "dailylog"
    const val CHATBOT = "chatbot"
    const val SETTINGS = "setings"
    const val POST_DETAIL = "post_detail/{postId}"

    fun createPostDetailRoute(postId: Int): String {
        return "post_detail/$postId"
    }
}
