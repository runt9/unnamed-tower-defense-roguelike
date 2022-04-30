package com.runt9.untdrl.util.ext

import com.badlogic.gdx.graphics.Texture
import com.runt9.untdrl.model.TextureDefinition
import ktx.assets.async.AssetStorage

fun AssetStorage.loadTexture(def: TextureDefinition): Texture = this[def.assetFile]
