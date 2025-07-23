package com.andres.notes.master.ui.screens.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.andres.notes.master.MainActivity
import com.andres.notes.master.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun enterScreen_showsWelcomeMessage() {
        val expectedTitle = composeRule.activity.getString(R.string.welcome_banner_title)
        val expectedBody = composeRule.activity.getString(R.string.welcome_banner_content)

        composeRule.apply {
            onNodeWithText(text = expectedTitle).assertIsDisplayed()
            onNodeWithText(text = expectedBody).assertIsDisplayed()
        }
    }

    @Test
    fun mainFab_showsSelectionOptions() {
        composeRule.apply {
            onNodeWithTag(testTag = "mainFab").performClick()
            onNodeWithTag(testTag = "fabTextNote").assertIsDisplayed()
            onNodeWithTag(testTag = "fabChecklist").assertIsDisplayed()
        }
    }
}