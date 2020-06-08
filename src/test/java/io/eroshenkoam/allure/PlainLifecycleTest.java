package io.eroshenkoam.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.util.ResultsUtils;

import java.util.Arrays;
import java.util.UUID;

public class PlainLifecycleTest {

    private static final AllureLifecycle LIFECYCLE = Allure.getLifecycle();

    /**
     * Должно быть одинаковым между разными запусками
     */
    private static final String HISTORY_ID = "WithoutFrameworkTest.main()";

    public static void main(String[] args) {
        final AssertionError error = new AssertionError("some error here");

        final String testCaseUUID = UUID.randomUUID().toString();
        LIFECYCLE.scheduleTestCase(new TestResult()
                .setUuid(testCaseUUID)
                .setName("My favorite test")
                .setStart(System.currentTimeMillis())
                .setHistoryId(HISTORY_ID)
        );
        LIFECYCLE.startTestCase(testCaseUUID);

        // добавим параметры в тест
        LIFECYCLE.updateTestCase(result -> {
            result.setParameters(Arrays.asList(
                    new Parameter().setName("User").setValue("eroshenkoam"),
                    new Parameter().setName("Role").setValue("admin")
            ));
        });

        // добавим лейбочки в тест
        LIFECYCLE.updateTestCase(result -> {
            result.getLabels().add(ResultsUtils.createLabel("feature", "First Feature"));
            result.getLabels().add(ResultsUtils.createLabel("story", "First Story"));
        });

        // добавим ссылок в сам тест
        LIFECYCLE.updateTestCase(result -> {
            result.getLinks().add(ResultsUtils.createLink("AE-1", "AE-1", "https://jira.company.com/AE-1", "jira"));
            result.getLinks().add(ResultsUtils.createLink("AE-2", "AE-2", "https://jira.company.com/AE-2", "jira"));
        });

        // добавим шаг в тест
        final String stepUUID = UUID.randomUUID().toString();
        LIFECYCLE.startStep(stepUUID, new StepResult()
                .setName("Первый шаг")
                .setStart(System.currentTimeMillis())
        );

        // добавим параметры в шаг
        LIFECYCLE.updateStep(result -> {
            result.setParameters(Arrays.asList(
                    new Parameter().setName("Login").setValue("eroshenkoam"),
                    new Parameter().setName("Password").setValue(UUID.randomUUID().toString())
            ));
        });

        // добавим аттачменты в шаг
        LIFECYCLE.addAttachment("Текстовые данные", "text/plain", "txt", "данные".getBytes());

        // завершаем шаг
        LIFECYCLE.updateStep(result -> {
            result.setStop(System.currentTimeMillis());
            ResultsUtils.getStatus(error).ifPresent(result::setStatus);
            ResultsUtils.getStatusDetails(error).ifPresent(result::setStatusDetails);
        });
        LIFECYCLE.stopStep(stepUUID);

        // если тест прошел усрешно
        LIFECYCLE.updateTestCase(result -> {
            result.setStatus(Status.PASSED);
            result.setStop(System.currentTimeMillis());
        });

        // в случае ошибки, нужно ее обработать
        LIFECYCLE.updateTestCase(result -> {
            ResultsUtils.getStatus(error).ifPresent(result::setStatus);
            ResultsUtils.getStatusDetails(error).ifPresent(result::setStatusDetails);
        });

        LIFECYCLE.stopTestCase(testCaseUUID);
        LIFECYCLE.writeTestCase(testCaseUUID);
    }

}
