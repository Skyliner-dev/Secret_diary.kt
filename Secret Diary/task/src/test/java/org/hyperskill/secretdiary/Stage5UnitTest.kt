package org.hyperskill.secretdiary

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.hyperskill.secretdiary.internals.AbstractUnitTest
import org.hyperskill.secretdiary.internals.CustomClockSystemShadow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.manifest.AndroidManifest
import org.robolectric.manifest.IntentFilterData
import org.robolectric.res.Fs
import java.io.File

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomClockSystemShadow::class])
class Stage5UnitTest : AbstractUnitTest<LoginActivity>(LoginActivity::class.java) {

    private val tvTitle by lazy {
        val tvTitle = activity.findViewByString<TextView>("tvTitle")

        val messageWrongTitleText = "The title should be \"Secret Diary\""
        assertEquals(messageWrongTitleText, "Secret Diary", tvTitle.text.toString())

        tvTitle
    }

    private val etPin by lazy {
        val etPin = activity.findViewByString<EditText>("etPin")

        val messageEtPinWrongHint = "etPin should have a hint property with \"Enter PIN\" value"
        assertEquals(messageEtPinWrongHint, "Enter PIN", etPin.hint)

        etPin
    }

    private val btnLogin by lazy {
        val btnLogin = activity.findViewByString<Button>("btnLogin")

        val messageBtnLoginWrongText = "The text of btnLogin should be \"Log In\""
        assertEquals(messageBtnLoginWrongText, "Log In", btnLogin.text.toString())

        btnLogin
    }


    @Test
    fun testShouldCheckTextViewTitle() {
        testActivity {
            tvTitle
        }
    }


    @Test
    fun test00_checkEditTextPin() {
        testActivity {
            etPin
        }
    }

    @Test
    fun test01_checkButtonLogin() {
        testActivity {
            btnLogin
        }
    }


    @Test
    fun test02_checkLauncher() {
        testActivity {

            // ensure all views used on test are initialized with initial state
            tvTitle
            etPin
            btnLogin
            //

            val relativePathString = "src/main/AndroidManifest.xml"
            val absolutePathString = File(relativePathString).absolutePath
            val manifestPath = Fs.fromUrl(absolutePathString)
            val appManifest = AndroidManifest(manifestPath, null, null)

            val activityData =
                appManifest.getActivityData("org.hyperskill.secretdiary.LoginActivity")
            val intentFilters: MutableList<IntentFilterData> = activityData.intentFilters
            val data = intentFilters.getOrNull(0)
                ?: throw AssertionError("No intent filter declared for LoginActivity on AndroidManifest.xml")


            assertTrue(
                "Wrong manifest intent-filter property for LoginActivity, expected to contain action android.intent.action.MAIN",
                data.actions.contains("android.intent.action.MAIN")
            )
            assertTrue(
                "Wrong manifest intent-filter property for LoginActivity, expected to contain category android.intent.category.LAUNCHER",
                data.categories.contains("android.intent.category.LAUNCHER")
            )
        }
    }

    @Test
    fun test03_checkNavigation() {
        val mainActivityIntent = testActivity {
            tvTitle
            etPin
            btnLogin

            etPin.setText("1234")
            btnLogin.clickAndRun()

            val nextStartedActivityIntent = shadowActivity.nextStartedActivity
                ?: throw AssertionError("No Intent for starting activity found")

            val expectedStartedActivityShortClassName = ".MainActivity"
            val actualStartedActivityShortClassName =
                nextStartedActivityIntent.component?.shortClassName ?: "null"
            assertEquals(
                "The next Activity was not the expected",
                expectedStartedActivityShortClassName,
                actualStartedActivityShortClassName
            )

            return@testActivity nextStartedActivityIntent
        }

        activityController.pause()
        activityController.stop()
        val mainActivityUnityTest =
            object : AbstractUnitTest<MainActivity>(MainActivity::class.java) {}

        mainActivityUnityTest.testActivity(mainActivityIntent) { mainActivity ->
            val tvDiary = mainActivity.findViewByString<TextView>("tvDiary")
            val etNewWriting = mainActivity.findViewByString<EditText>("etNewWriting")
            val btnSave = mainActivity.findViewByString<Button>("btnSave")
            val btnUndo = mainActivity.findViewByString<Button>("btnUndo")
        }
    }


    @Test
    fun test04_checkLoginActivityFinished() {
        testActivity {
            tvTitle
            etPin
            btnLogin

            etPin.setText("1234")
            btnLogin.clickAndRun()

            val messageActivityNotFinishing =
                "Login Activity should be destroyed on successful login action"
            val userIntentFlags = shadowActivity.nextStartedActivity.flags
            val hasCorrectFlags = userIntentFlags and (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) == (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            assertTrue(messageActivityNotFinishing, activity.isFinishing or hasCorrectFlags)
        }
    }


    @Test
    fun test05_checkWrongPinErrorMessage() {
        testActivity {
            tvTitle
            etPin
            btnLogin

            etPin.setText("123")
            btnLogin.clickAndRun()

            val expectedEtPinError = "Wrong PIN!"
            val actualEtPinError = etPin.error.toString()
            val messageWrongEtPinError =
                "If the PIN is incorrect, the error message \"Wrong PIN!\" should be shown!"
            assertEquals(messageWrongEtPinError, expectedEtPinError, actualEtPinError)
        }
    }

    @Test
    fun test06_checkWrongPinActivityBehavior() {
        testActivity {
            tvTitle
            etPin
            btnLogin

            etPin.setText("123")
            btnLogin.clickAndRun()

            val expectedNextStartedActivityIntent = null
            val actualNextStartedActivityIntent = shadowActivity.nextStartedActivity
            val messageWrongIntent = "MainActivity should not start if the PIN is incorrect"
            assertEquals(
                messageWrongIntent,
                expectedNextStartedActivityIntent,
                actualNextStartedActivityIntent
            )
        }
    }
}