package com.slateblua.cereal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slateblua.cereal.domain.model.AppSettings
import com.slateblua.cereal.domain.usecase.GetAppSettingsUseCase
import com.slateblua.cereal.ui.navigation.CerealAppNavGraph
import com.slateblua.cereal.ui.theme.CerealTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val getAppSettingsUseCase: GetAppSettingsUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by getAppSettingsUseCase().collectAsState(initial = AppSettings())
            val isDark = settings.isDarkMode

            CerealTheme(darkTheme = isDark) {
                CerealAppNavGraph()
            }
        }
    }
}