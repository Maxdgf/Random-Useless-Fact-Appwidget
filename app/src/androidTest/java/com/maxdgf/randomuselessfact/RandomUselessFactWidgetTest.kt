package com.maxdgf.randomuselessfact


import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.testing.unit.assertHasClickAction
import androidx.glance.testing.unit.assertHasText
import androidx.glance.testing.unit.hasTestTag
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.maxdgf.randomuselessfact.appwidget.SwitchToIdleStateAction
import com.maxdgf.randomuselessfact.appwidget.ui.FactLoadingErrorScreen
import com.maxdgf.randomuselessfact.appwidget.ui.FactLoadingScreen
import com.maxdgf.randomuselessfact.appwidget.ui.RandomUselessFactWidgetScreen

import org.junit.Test
import org.junit.runner.RunWith


private const val FACT_SOURCE = "Some factsSource"
private const val FACT_TEXT = "💡 Some useless fact"
private const val ERROR_MESSAGE = "Some error message"


@RunWith(AndroidJUnit4::class)
class RandomUselessFactWidgetTest {
    @Test
    fun testRandomFactWidgetScreenComposable() = runGlanceAppWidgetUnitTest {
        // Set the fact screen composable to test
        provideComposable {
            RandomUselessFactWidgetScreen(
                factText = FACT_TEXT,
                factSource = FACT_SOURCE,
                onRefreshFact = { println("refreshing...") }
            )
        }

        // Perform assertions

        onNode(hasTestTag("fact_text"))
            .assertHasText(FACT_TEXT)
        
        onNode(hasTestTag("fact_source"))
            .assertHasText(FACT_SOURCE)
        
        onNode(hasTestTag("refresh_button"))
            .assertHasClickAction()
    }
    
    @Test
    fun testRandomFactWidgetLoadingScreenComposable() = runGlanceAppWidgetUnitTest {
        // Set the fact loading screen composable to test
        provideComposable {
            FactLoadingScreen()
        }

        // Perform assertions
        
        onNode(hasTestTag("loading_text"))
            .assertHasText("Loading useful fact...")
    }
    
    @Test
    fun testRandomFactWidgetErrorScreenComposable() = runGlanceAppWidgetUnitTest {
        // Set the fact loading error screen composable to test
        provideComposable {
            FactLoadingErrorScreen(
                message = ERROR_MESSAGE,
                showLoadLastLoadedFactButton = true,
                onShowLastLoadedFact = actionRunCallback<SwitchToIdleStateAction>(),
                onRetry = { println("refreshing...") }
            )
        }

        // Perform assertions

        onNode(hasTestTag("error_message_text"))
            .assertHasText("Fact not uploaded, please retry or load latest fact!\nError: $ERROR_MESSAGE")

        onNode(hasTestTag("show_last_fact_button"))
            .assertExists()
            .assertHasClickAction()

        onNode(hasTestTag("show_last_fact_button_spacer"))
            .assertExists()

        onNode(hasTestTag("retry_button"))
            .assertHasClickAction()
    }
}