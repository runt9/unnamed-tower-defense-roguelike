package com.runt9.untdrl.view.duringRun.game.projectile

import com.runt9.untdrl.model.tower.Projectile
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class ProjectileViewModel(val projectile: Projectile) : ViewModel() {
    val texture = projectile.texture
    val position = Binding(projectile.position.cpy())
    val rotation = Binding(projectile.rotation)
}
