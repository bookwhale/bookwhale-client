package com.example.bookwhale.screen.article

import com.example.bookwhale.model.article.DetailArticleModel

sealed class ModifyArticleState {
    object Uninitialized : ModifyArticleState()

    object Loading : ModifyArticleState()

    data class LoadSuccess(
        val article : DetailArticleModel
    ) : ModifyArticleState()//

    object ModifySuccess : ModifyArticleState()

    data class Error(
        val code : String?
    ) : ModifyArticleState()
}