package com.wwf.projectmanagement

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.wwf.projectmanagement.data.local.LocalSession
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    // The activity starts before @Before runs, so clear the persisted session before the class
    // and after each test instead.
    @After
    fun signOut() = clearSession()

    companion object {
        @BeforeClass
        @JvmStatic
        fun clearSession() {
            LocalSession(InstrumentationRegistry.getInstrumentation().targetContext).logout()
        }
    }

    @Test
    fun homePage_isPublicAndOffersLogin() {
        composeRule.onNodeWithText("WWF Project Platform").assertIsDisplayed()
        composeRule.onNodeWithText("Conservation in Action").assertIsDisplayed()
        composeRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun tappingLogin_opensLoginDialog() {
        composeRule.onNodeWithText("Login").performClick()
        composeRule.onNodeWithText("Sign in").assertIsDisplayed()
    }

    @Test
    fun projectPage_goToLogin_routesToHomeLogin_thenShowsTools() {
        openFirstProject()
        composeRule.onNodeWithText("Go to Login").performScrollTo().performClick()
        composeRule.onNodeWithText("Sign in").assertIsDisplayed()
        composeRule.onNodeWithText("Email").performTextInput("volunteer@example.org")
        composeRule.onNodeWithText("Password").performTextInput("secret")
        composeRule.onAllNodesWithText("Login").onLast().performClick()
        composeRule.onNodeWithText("Signed in as volunteer@example.org").assertIsDisplayed()

        openFirstProject()
        composeRule.onNodeWithText("Explore Dashboard").assertIsDisplayed()
        composeRule.onNodeWithText("Go to Login").assertDoesNotExist()
    }

    private fun openFirstProject() {
        composeRule.onRoot().performTouchInput { swipeLeft() }
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("View Straw Headed Bulbul").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("View Straw Headed Bulbul").performScrollTo().performClick()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("WWF Straw Headed Bulbul Survey Platform").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
