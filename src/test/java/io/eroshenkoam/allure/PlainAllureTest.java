package io.eroshenkoam.allure;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import java.util.UUID;

import static io.qameta.allure.Allure.attachment;
import static io.qameta.allure.Allure.label;
import static io.qameta.allure.Allure.link;
import static io.qameta.allure.Allure.parameter;
import static io.qameta.allure.Allure.step;

public class PlainAllureTest {

    @Test
    @DisplayName("Название теста")
    public void testSomething() {
        parameter("Login", "eroshenkoam");
        parameter("Role", "admin");

        link("AE-1", "https://jira.company.com/AE-1", "jira");
        link("AE-2", "https://jira.company.com/AE-1", "jira");

        label("feature", "First Feature");
        label("story", "First Story");

        step("Step without execution");

        step("Step with attachment", () -> {
            attachment("Attachment", "content");
        });

        step("Step with nested step", () -> {
            step("Nested step");
        });

        step("Step with parameters", (context) -> {
            context.parameter("username", "eroshenkoam");
            context.parameter("password", UUID.randomUUID());
        });
    }

}
