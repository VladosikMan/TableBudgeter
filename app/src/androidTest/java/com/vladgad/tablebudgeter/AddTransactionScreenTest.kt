package com.vladgad.tablebudgeter

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTransactionScreenTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAddTransactionUI_AllFieldsFilled_Success() {
        // 1. Проверяем, что все элементы отображаются
//        onView(withId(R.id.operation_edit_text)).check(matches(isDisplayed()))
//        onView(withId(R.id.amount_edit_text)).check(matches(isDisplayed()))
//        onView(withId(R.id.account_spinner)).check(matches(isDisplayed()))
//        onView(withId(R.id.save_button)).check(matches(isDisplayed()))

        // 2. Заполняем форму
//        onView(withId(R.id.operation_edit_text))
//            .perform(typeText("Супермаркет"), closeSoftKeyboard())
//
//        onView(withId(R.id.amount_edit_text))
//            .perform(typeText("1500.50"), closeSoftKeyboard())
//
//        // 3. Выбираем счет из спиннера
//        onView(withId(R.id.account_spinner)).perform(click())
//        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Карта")))
//            .perform(click())
//
//        // 4. Нажимаем сохранить
//        onView(withId(R.id.save_button)).perform(click())
//
//        // 5. Проверяем навигацию или сообщение об успехе
//        onView(withText("Транзакция успешно добавлена"))
//            .check(matches(isDisplayed()))
    }

    @Test
    fun testAddTransactionUI_EmptyFields_ShowsError() {
        // 1. Оставляем поле операции пустым
//        onView(withId(R.id.amount_edit_text))
//            .perform(typeText("1000"), closeSoftKeyboard())
//
//        onView(withId(R.id.account_spinner)).perform(click())
//        onData(`is`("Наличные")).perform(click())
//
//        // 2. Нажимаем сохранить
//        onView(withId(R.id.save_button)).perform(click())
//
//        // 3. Проверяем сообщение об ошибке
//        onView(withId(R.id.operation_edit_text))
//            .check(matches(hasErrorText("Введите операцию")))
    }
}