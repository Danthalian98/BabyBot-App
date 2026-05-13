package com.proyecto.babybot.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val REGISTER = "register"
    const val HOME = "home"
    const val TRIALINFO = "trialinfo"
    const val SUBSCRIPTIONS = "subscriptions"
    const val FORUM = "forum"
    const val POST_DETAIL = "post_detail/{postId}"
    const val DAILYLOG = "dailylog"
    const val CHATBOT = "chatbot"
    const val SETTINGS = "settings"
    const val SETTINGS_ACCOUNT = "settings_account"
    const val SETTINGS_SECURITY = "settings_security"
    const val SETTINGS_NOTIFICATIONS = "settings_notifications"
    const val SETTINGS_PRIVACY = "settings_privacy"
    const val SETTINGS_THEME = "settings_theme"
    const val SETTINGS_LANGUAGE = "settings_language"
    const val SETTINGS_ABOUT = "settings_about"
    const val NOTIFICATIONS = "notifications"
    const val EDIT_ACCOUNT_INFO = "edit_account_info/{mode}"

    fun createEditAccountInfoRoute(mode: Int): String {
        return "edit_account_info/$mode"
    }

    fun createPostDetailRoute(postId: Int): String {
        return "post_detail/$postId"
    }
}
