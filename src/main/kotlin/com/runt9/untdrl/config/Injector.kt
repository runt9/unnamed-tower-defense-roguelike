package com.runt9.untdrl.config

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ai.GdxAI
import com.runt9.untdrl.UnTdRlGame
import com.runt9.untdrl.service.ChunkGenerator
import com.runt9.untdrl.service.RandomizerService
import com.runt9.untdrl.service.asset.AssetLoader
import com.runt9.untdrl.service.asset.SkinLoader
import com.runt9.untdrl.service.duringRun.EnemyService
import com.runt9.untdrl.service.duringRun.IndexedGridGraph
import com.runt9.untdrl.service.duringRun.LootService
import com.runt9.untdrl.service.duringRun.MineService
import com.runt9.untdrl.service.duringRun.ProjectileService
import com.runt9.untdrl.service.duringRun.ResearchService
import com.runt9.untdrl.service.duringRun.RunInitializer
import com.runt9.untdrl.service.duringRun.RunServiceRegistry
import com.runt9.untdrl.service.duringRun.RunStateService
import com.runt9.untdrl.service.duringRun.SpawnerService
import com.runt9.untdrl.service.duringRun.TickerRegistry
import com.runt9.untdrl.service.duringRun.TowerService
import com.runt9.untdrl.util.framework.event.EventBus
import com.runt9.untdrl.util.framework.ui.DialogManager
import com.runt9.untdrl.view.duringRun.DuringRunInputController
import com.runt9.untdrl.view.duringRun.DuringRunScreen
import com.runt9.untdrl.view.duringRun.game.DuringRunGameController
import com.runt9.untdrl.view.duringRun.ui.DuringRunUiController
import com.runt9.untdrl.view.duringRun.ui.faction.ManageFactionDialogController
import com.runt9.untdrl.view.duringRun.ui.loot.LootDialogController
import com.runt9.untdrl.view.duringRun.ui.menu.MenuDialogController
import com.runt9.untdrl.view.duringRun.ui.research.ResearchDialogController
import com.runt9.untdrl.view.duringRun.ui.runEnd.RunEndDialogController
import com.runt9.untdrl.view.duringRun.ui.shop.ShopDialogController
import com.runt9.untdrl.view.factionSelect.FactionSelectController
import com.runt9.untdrl.view.loading.LoadingScreenController
import com.runt9.untdrl.view.mainMenu.MainMenuScreenController
import com.runt9.untdrl.view.settings.SettingsDialogController
import ktx.inject.Context
import ktx.inject.register

object Injector : Context() {
    fun initStartupDeps() = register {
        bindSingleton<UnTdRlGame>()
        bindSingleton<PlayerSettingsConfig>()
        bindSingleton<ApplicationConfiguration>()
        bindSingleton<EventBus>()
        bindSingleton<AssetConfig>()
        bindSingleton<SkinLoader>()
        bindSingleton<AssetLoader>()
        bindSingleton<ApplicationInitializer>()
    }

    fun initGdxDeps() = register {
        bindSingleton(Gdx.app)
        bindSingleton(Gdx.audio)
        bindSingleton(Gdx.files)
        bindSingleton(Gdx.gl)
        bindSingleton(Gdx.graphics)
        bindSingleton(Gdx.input)
        bindSingleton(Gdx.net)
    }

    fun initRunningDeps() = register {
        bindSingleton(InputMultiplexer())
        bindSingleton(GdxAI.getTimepiece())

        bindSingleton<RunServiceRegistry>()
        bindSingleton<RunStateService>()

        bindSingleton<IndexedGridGraph>()
        bindSingleton<RandomizerService>()
        bindSingleton<ChunkGenerator>()
        bindSingleton<TowerService>()
        bindSingleton<TickerRegistry>()
        bindSingleton<EnemyService>()
        bindSingleton<ProjectileService>()
        bindSingleton<MineService>()
        bindSingleton<SpawnerService>()
        bindSingleton<LootService>()
        bindSingleton<ResearchService>()

        bindSingleton<RunInitializer>()

        bindSingleton<DialogManager>()
        bindSingleton<LoadingScreenController>()
        bindSingleton<MainMenuScreenController>()
        bindSingleton<FactionSelectController>()
        bindSingleton<SettingsDialogController>()
        bindSingleton<RunEndDialogController>()
        bindSingleton<DuringRunGameController>()
        bindSingleton<DuringRunUiController>()
        bindSingleton<DuringRunInputController>()
        bindSingleton<DuringRunScreen>()
        bindSingleton<MenuDialogController>()
        bindSingleton<LootDialogController>()
        bindSingleton<ShopDialogController>()
        bindSingleton<ResearchDialogController>()
        bindSingleton<ManageFactionDialogController>()
    }
}
