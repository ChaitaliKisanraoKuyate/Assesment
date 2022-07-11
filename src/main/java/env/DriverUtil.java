package env;

import etaf.helperutils.filehelper.FileUtils;
import etaf.helperutils.filehelper.GlobalProperties;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.PhantomJsDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.ErrorHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static etaf.helperutils.constants.Constants.*;
import static etaf.helperutils.filehelper.GlobalProperties.getConfigProperties;


public class DriverUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverUtil.class);
    private static final GlobalProperties configProperties = getConfigProperties();
    private static WebDriver driver;
    private static WebDriverWait wait;


    /**
     * Close web driver.
     */
    public static void closeDriver() {
        if (driver != null) {
            try {
                //driver.close();
                //driver.quit();

            } catch (NoSuchMethodError nsme) {
                LOGGER.debug(nsme.getMessage() + " error was thrown while closing the driver");// in case quit fails
            } catch (NoSuchSessionException nsse) {
                LOGGER.debug(nsse.getMessage() + " error was thrown while closing the driver");// in case close fails
            } catch (SessionNotCreatedException snce) {
                LOGGER.debug(snce.getMessage() + " error was thrown while closing the driver");// in case close fails
            }
            driver = null;
        }
    }

    /**
     * Get default web driver. Singleton implementation that creates only one instance and reuses the same throughout the session
     *
     * @return the web driver
     */
    public static WebDriver getDefaultDriver() {
        if (driver != null && ((RemoteWebDriver) driver).getSessionId() != null) {
            return driver;
        }

        driver = chooseDriver();

        return driver;
    }

    /**
     * create a web driver based on the browser and configurations set in config.properties file
     *
     * @return the web driver
     */
    private static WebDriver chooseDriver() {
        WebDriverManager.globalConfig().setCachePath(DRIVERPATH);
        PhantomJsDriverManager.globalConfig().setCachePath(DRIVERPATH);
        String preferredBrowser = configProperties.getProperty(BROWSER);
        boolean grid = configProperties.getProperty("grid").equalsIgnoreCase("true");
        DesiredCapabilities capabilities = Env.setCommonDesiredCapabilities(preferredBrowser);
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
        RemoteWebDriver driver = null;
        String extension = System.getProperty("os.name").toLowerCase().startsWith("windows") ? ".exe" : "";
        switch (preferredBrowser.toLowerCase()) {

            case "ch":
            case "chrome":
                ChromeOptions chromeOptions = Env.setChromeDriverCapabilities();
                if (grid) {
                    DesiredCapabilities caps = DesiredCapabilities.chrome();
                    caps.merge(capabilities);
                    caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

                    try {
                        driver = new RemoteWebDriver(new URL(configProperties.getProperty("gridURL")), caps);
                    } catch (MalformedURLException e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    ChromeDriverService service = null;
                    List<String> extensionList = new ArrayList<>();
                    extensionList.add(extension);
                    FileUtils fileUtils = new FileUtils();
                    List<File> files = fileUtils.search(DRIVERPATH, extensionList, preferredBrowser);
                    if (files.size() > 0) {
                        service = new ChromeDriverService.Builder()
                                .withSilent(true)
                                .usingAnyFreePort()
                                .usingDriverExecutable(files.get(files.size() - 1))
                                .build();
                    } else {
                        try {
                            WebDriverManager.chromedriver().setup();
                            service = new ChromeDriverService.Builder()
                                    .withSilent(true)
                                    .usingAnyFreePort()
                                    .build();
                        } catch (io.github.bonigarcia.wdm.config.WebDriverManagerException wdm) {
                            LOGGER.error("Error: The system is unable to connect to the server to fetch a chromedriver and a local copy of webdriver cannot be found" + wdm.getMessage());
                        }
                    }
                    chromeOptions.merge(capabilities);
                    Objects.requireNonNull(service, "Driver service returned null");
                    driver = new ChromeDriver(service, chromeOptions);
                }
                break;

            case "phantomjs":
                capabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.PHANTOMJS);
                if (grid) {
                    try {
                        driver = new RemoteWebDriver(new URL(configProperties.getProperty("gridURL")), capabilities);
                    } catch (MalformedURLException e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    PhantomJSDriverService servicePJS = new PhantomJSDriverService.Builder()
                            .usingAnyFreePort()
                            .usingPhantomJSExecutable(new File(PHANTOMJSDRIVER_PATH))
                            .build();
                    driver = new PhantomJSDriver(servicePJS, capabilities);
                }
                break;
            case "safari":
                SafariOptions safariOptions = new SafariOptions();
                safariOptions.merge(capabilities);
                if (grid) {
                    capabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.SAFARI);
                    try {
                        driver = new RemoteWebDriver(new URL(configProperties.getProperty("gridURL")), capabilities);
                    } catch (MalformedURLException e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    //Todo Create Env.setSafariOptions()
                    SafariDriverService service = new SafariDriverService.Builder()
                            .usingDriverExecutable(new File(SAFARIDRIVER_PATH))
                            .usingAnyFreePort()
                            .build();
                    driver = new SafariDriver(service, safariOptions);
                }
                break;
            case "ie":
            case "internetexplorer":
                if (grid) {
                    try {
                        driver = new RemoteWebDriver(new URL(configProperties.getProperty("gridURL")), capabilities);
                    } catch (MalformedURLException e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    InternetExplorerDriverService ieDriverService = null;
                    List<String> extensionList = new ArrayList<>();
                    extensionList.add(extension);
                    List<File> files = new FileUtils().search(DRIVERPATH, extensionList, preferredBrowser);
                    if (files.size() > 0) {
                        ieDriverService = new InternetExplorerDriverService.Builder()
                                .usingAnyFreePort()
                                .usingDriverExecutable(files.get(files.size() - 1))
                                .build();
                    } else {
                        try {
                            WebDriverManager.iedriver().setup();
                            System.setProperty(IEDRIVERPROPERTY, IEDRIVERSERVER_PATH);
                            ieDriverService = new InternetExplorerDriverService.Builder()
                                    .usingAnyFreePort()
                                    .build();
                        } catch (io.github.bonigarcia.wdm.config.WebDriverManagerException wdm) {
                            LOGGER.error("Error: The system is unable to connect to the server to fetch an IEDriverServer and a local copy of webdriver cannot be found" + wdm.getMessage());
                        }
                    }
                    driver = new InternetExplorerDriver(ieDriverService, Env.setIEDriverCapabilities());
                    //this is to reset Zoom level to 100% for IE.
                    driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, "0"));
                }
                break;
            case "ff":
            case "firefox":
            default:
                FirefoxOptions options = Env.setFFOptions();
                if (grid) {
                    DesiredCapabilities caps = DesiredCapabilities.firefox();
                    caps.merge(capabilities);
                    caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);

                    try {
                        driver = new RemoteWebDriver(new URL(configProperties.getProperty("gridURL")), caps);
                    } catch (MalformedURLException e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    GeckoDriverService geckoDriverService = null;
                    List<String> extensionList = new ArrayList<>();
                    extensionList.add(extension);
                    List<File> files = new FileUtils().search(DRIVERPATH, extensionList, "geckodriver");
                    if (files.size() > 0) {
                        geckoDriverService = new GeckoDriverService.Builder()
                                .usingAnyFreePort()
                                .usingDriverExecutable(files.get(files.size() - 1))
                                .build();
                    } else {
                        try {
                            WebDriverManager.firefoxdriver().setup();
                            geckoDriverService = new GeckoDriverService.Builder()
                                    .usingAnyFreePort()
                                    .build();

                        } catch (io.github.bonigarcia.wdm.config.WebDriverManagerException wdm) {
                            LOGGER.error("Error: The system is unable to connect to the server to fetch a geckodriver and a local copy of webdriver cannot be found" + wdm.getMessage());
                        }
                    }
                    Objects.requireNonNull(geckoDriverService, "Driver service returned null");
                    driver = new FirefoxDriver(geckoDriverService, options);
                }
                break;
        }
        ErrorHandler handler = new ErrorHandler();
        handler.setIncludeServerErrors(false);
        driver.setErrorHandler(handler);
        if (getConfigProperties().getProperty(USE_SESSION).equalsIgnoreCase("true"))
            Env.attachToExistingSession(driver);
        driver.manage().timeouts().setScriptTimeout(Long.parseLong(configProperties.getProperty(DEFAULT_WAIT)), TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }

    public Logger getLogger() {
        return LOGGER;
    }

}
