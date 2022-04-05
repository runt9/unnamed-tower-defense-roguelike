package com.runt9.untdrl

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.runt9.untdrl.config.ApplicationConfiguration
import com.runt9.untdrl.config.Injector
import com.runt9.untdrl.config.inject

object UnTdRlLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        Injector.initStartupDeps()
        Lwjgl3Application(inject<UnTdRlGame>(), inject<ApplicationConfiguration>())
    }
}
