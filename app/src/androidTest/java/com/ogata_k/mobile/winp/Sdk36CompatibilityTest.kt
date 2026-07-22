package com.ogata_k.mobile.winp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ogata_k.mobile.winp.presentation.activity.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Sdk36CompatibilityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testActivityLaunches() {
        // Basic test to ensure the activity launches without crashing on API 36
        composeTestRule.waitForIdle()
    }
}
