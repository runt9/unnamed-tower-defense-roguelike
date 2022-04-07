package com.runt9.untdrl.view.duringRun.game.enemy

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.runt9.untdrl.util.framework.ui.viewModel.ViewModel

class EnemyViewModel(val id: Int, val name: String, val texture: Texture, initialPosition: Vector2, initialRotation: Float) : ViewModel() {
    val position = Binding(initialPosition)
    val rotation = Binding(initialRotation)

    override fun toString() = "Enemy($id | $name)"
}
