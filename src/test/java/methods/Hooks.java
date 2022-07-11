package methods;

import application.Initializer;
import base.BaseInitializer;
import baseinitializers.StepImplementationBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import env.DriverUtil;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SelfHealing.HistoricalPropertiesUtil;
import utils.browser.ScreenShotMethods;
import utils.helpers.testdatahelpers.TestDataHandler;

import static etaf.helperutils.constants.Constants.CLOSE_BROWSER_AFTER_TEST;
import static etaf.helperutils.constants.Constants.UPDATE_HISTORICAL_PROPERTIES;
import static etaf.helperutils.filehelper.GlobalProperties.getConfigProperties;
import static etaf.helperutils.filehelper.JarPropertiesHelper.dataUtilsBaseJson;
import static etaf.helperutils.filehelper.JarPropertiesHelper.selfHealingPropJson;
import static etaf.helperutils.filehelper.JarPropertiesHelper.stepDefPropJson;



public class Hooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);

    @After(order = 2)//executes before Order=0
    public void afterScenario(Scenario scenario) throws Throwable {
        scenario.attach(((TakesScreenshot) DriverUtil.getDefaultDriver()).getScreenshotAs(OutputType.BYTES), "image/png","image");

        if (getConfigProperties().getProperty(CLOSE_BROWSER_AFTER_TEST).equalsIgnoreCase("true"))
            DriverUtil.closeDriver();

        if (getConfigProperties().getProperty(UPDATE_HISTORICAL_PROPERTIES).equalsIgnoreCase("true"))
            HistoricalPropertiesUtil.flushHistoricalProperties();

        if (HistoricalPropertiesUtil.getHealedElements().size() > 0)
            HistoricalPropertiesUtil.updateObjectRepository();
    }

    @AfterStep("@stepwise_screenshots")
    public void afterStep(Scenario scenario) throws Throwable {
        scenario.attach(((TakesScreenshot) DriverUtil.getDefaultDriver()).getScreenshotAs(OutputType.BYTES), "image/png","test");

    }

    @AfterStep("@desktop_screenshots")
    public void takeDesktopScreenshot(Scenario scenario) {
        scenario.attach(ScreenShotMethods.getScreenShotMethods().takeDesktopScreenshot(), "image/png", "test");
    }

    @Before("not @etl")
    public void JarInitialize() throws Exception {
        WebDriver driver = DriverUtil.getDefaultDriver();
        new StepImplementationBase(driver, stepDefPropJson(), dataUtilsBaseJson(), selfHealingPropJson());
    }

    @Before("@etl")
    public void initialize() throws Exception {
        Initializer initializer = new Initializer();
        new BaseInitializer(null, dataUtilsBaseJson(), selfHealingPropJson());
        ObjectMapper mapper = new ObjectMapper();
        try {
            initializer.setEtlDataHandler(mapper.writeValueAsString(TestDataHandler.traverseMap));
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to convert Test Data files to json objects for etl core libraries instance");
        }
    }
}
