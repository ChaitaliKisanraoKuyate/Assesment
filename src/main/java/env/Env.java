package env;

import etaf.helperutils.filehelper.GlobalProperties;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.http.W3CHttpCommandCodec;
import org.openqa.selenium.remote.http.W3CHttpResponseCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;

import static etaf.helperutils.constants.Constants.*;
import static etaf.helperutils.filehelper.GlobalProperties.getConfigProperties;

/**
 * The type Env.
 */
public class Env {
    private static final Logger LOGGER = LoggerFactory.getLogger(Env.class);
    private static final GlobalProperties configProperties = getConfigProperties();

    /**
     * Attach to existing session.
     *
     * @param driver the driver
     * @return the remote web driver
     */
    public static RemoteWebDriver attachToExistingSession(RemoteWebDriver driver) {
        HttpCommandExecutor executor = (HttpCommandExecutor) driver.getCommandExecutor();
        URL url = executor.getAddressOfRemoteServer();
        SessionId session_id = driver.getSessionId();
        RemoteWebDriver newDriver = Env.createDriverFromSession(session_id, url);
        return newDriver;
    }

    /**
     * Create driver for existing session.
     *
     * @param sessionId        the session id
     * @param command_executor the command executor
     * @return the remote web driver
     */
    public static RemoteWebDriver createDriverFromSession(final SessionId sessionId, URL command_executor) {
        CommandExecutor executor = new HttpCommandExecutor(command_executor) {

            @Override
            public Response execute(Command command) throws IOException {
                Response response = null;
                if (command.getName() == "newSession") {
                    response = new Response();
                    response.setSessionId(sessionId.toString());
                    response.setStatus(0);
                    response.setValue(Collections.<String, String>emptyMap());

                    try {
                        Field commandCodec = null;
                        commandCodec = this.getClass().getSuperclass().getDeclaredField("commandCodec");
                        commandCodec.setAccessible(true);
                        commandCodec.set(this, new W3CHttpCommandCodec());

                        Field responseCodec = null;
                        responseCodec = this.getClass().getSuperclass().getDeclaredField("responseCodec");
                        responseCodec.setAccessible(true);
                        responseCodec.set(this, new W3CHttpResponseCodec());
                    } catch (NoSuchFieldException e) {
                        LOGGER.error(e.getMessage());
                    } catch (IllegalAccessException e) {
                        LOGGER.error(e.getMessage());
                    }

                } else {
                    response = super.execute(command);
                }
                return response;
            }
        };

        return new RemoteWebDriver(executor, new DesiredCapabilities());
    }

    /**
     * Set chrome driver capabilities.
     *
     * @return the chrome options
     */
    public static ChromeOptions setChromeDriverCapabilities() {
        ChromeOptions options = new ChromeOptions();

        if (configProperties.getProperty(ACCESSIBILITY).equals("true")) {

            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        }
        if (configProperties.getProperty(HEADLESS).equalsIgnoreCase("true")) {
            options.addArguments("--headless");
        }

        options.setCapability("takesScreenshot", configProperties.getProperty(TAKES_SCREENSHOT));
        options.setAcceptInsecureCerts(true);

        options.setExperimentalOption("useAutomationExtension", configProperties.getProperty(CHROMEUSEAUTOMATIONEXTENSION).equals("true"));

        options.addArguments("start-maximized");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-gpu");

        String downloadFilepath = configProperties.getProperty("downloadFileLocation");
        if (downloadFilepath.charAt(2) != ':') {
            downloadFilepath = System.getProperty("user.dir") + FILE_SEPARATOR + downloadFilepath;

        }
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);
        options.setExperimentalOption("prefs", chromePrefs);
        return options;
    }

    /**
     * Set common desired capabilities for all browsers.
     *
     * @param browser the browser
     * @return the desired capabilities
     */
    public static DesiredCapabilities setCommonDesiredCapabilities(String browser) {
        DesiredCapabilities capabilities = null;
        if (browser.equalsIgnoreCase("ie") || browser.toLowerCase().startsWith("internet")) {
            capabilities = DesiredCapabilities.internetExplorer();
        } else {
            capabilities = new DesiredCapabilities();
        }
        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability("takesScreenshot", configProperties.getProperty(TAKES_SCREENSHOT));
        return capabilities;
    }

    /**
     * Set firefox options.
     *
     * @return the firefox options
     */
    public static FirefoxOptions setFFOptions() {
        FirefoxOptions options = new FirefoxOptions();

        if (configProperties.getProperty(ACCESSIBILITY).equals("true")) {

            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        }
        if (configProperties.getProperty(HEADLESS).equalsIgnoreCase("true")) {
            options.addArguments("--headless");
        }
        options.setCapability("takesScreenshot", configProperties.getProperty(TAKES_SCREENSHOT));
        options.setAcceptInsecureCerts(true);
        FirefoxProfile profile = new FirefoxProfile();
        String downloadFilepath = configProperties.getProperty("downloadFileLocation");
        if (downloadFilepath.charAt(2) != ':') {

            downloadFilepath = System.getProperty("user.dir") + FILE_SEPARATOR + downloadFilepath;

        }
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", downloadFilepath); //Set the last directory used for saving a file from the "What should (browser) do with this file?" dialog.
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf, application/octet-stream, application/x-gzip"); //list of MIME types to save to disk without asking what to use to open the file
        profile.setPreference("pdfjs.disabled", true);  // disable the built-in PDF viewer
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
        options.setProfile(profile);
        return options;
    }

    /**
     * Set ie driver capabilities.
     *
     * @return the internet explorer options
     */
    public static InternetExplorerOptions setIEDriverCapabilities() {
        InternetExplorerOptions ieOptions = new InternetExplorerOptions();
        ieOptions.setCapability("requireWindowFocus", true);

        ieOptions.setCapability("INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS", configProperties.getProperty(INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS));
        ieOptions.setCapability("ignoreProtectedModeSettings", configProperties.getProperty(IGNOREPROTECTEDMODESETTINGS).equals("true"));
        ieOptions.setCapability("ignoreZoomSetting", configProperties.getProperty(IGNOREZOOMSETTING).equals("true"));
        ieOptions.setCapability("initialBrowserUrl", configProperties.getProperty(INITIALBROWSERURL));
        ieOptions.setCapability("enablePersistentHover", configProperties.getProperty(ENABLEPERSISTENTHOVER).equals("true"));
        ieOptions.setCapability("enableElementCacheCleanup", configProperties.getProperty(ENABLEELEMENTCACHECLEANUP).equals("true"));
        ieOptions.setCapability("requireWindowFocus", configProperties.getProperty(REQUIREWINDOWFOCUS).equals("true"));
        ieOptions.setCapability("browserAttachTimeout", Integer.parseInt(configProperties.getProperty(BROWSERATTACHTIMEOUT)));
        ieOptions.setCapability("ie.ensureCleanSession", configProperties.getProperty(IE_ENSURECLEANSESSION).equals("true"));

        return ieOptions;
    }

    public Logger getLogger() {
        return LOGGER;
    }
}
