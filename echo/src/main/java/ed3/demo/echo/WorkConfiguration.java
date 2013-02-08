package ed3.demo.echo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Configuration for ED3 ECHO workflow
 *
 * @author mmceniry
 */
@XmlRootElement(name = "work-configuration")
public class WorkConfiguration {

    /**
     * Default ED3 workflow services endpoint
     */
    public static final String DEFAULT_WORKFLOW_ENDPOINT = "http://ed3test.itsc.uah.edu:80/ed3/dataservices/doworkflow.php";
    /**
     * Default ECHO granule search endpoint
     */
    public static final String DEFAULT_ECHO_ENDPOINT = "https://api.echo.nasa.gov/echo-esip/search/granule.atom";
    /**
     * Default user name (if user.name is not available)
     */
    public static final String DEFAULT_USER_NAME = "mmceniry";
    /**
     * Default email address domain
     */
    public static final String DEFAULT_USER_DOMAIN = "itsc.uah.edu";
    /**
     * ED3 workflow services endpoint
     */
    @XmlElement(name = "workflow-endpoint")
    public String workflowEndpoint;
    /**
     * ECHO granule search endpoint
     */
    @XmlElement(name = "echo-endpoint")
    public String echoEndpoint;
    /**
     * ECHO client ID
     */
    @XmlElement(name = "echo-client-id")
    public String echoClientId;

    /**
     * Populate with default values
     *
     * @see #DEFAULT_ECHO_ENDPOINT
     * @see #DEFAULT_WORKFLOW_ENDPOINT
     * @see #DEFAULT_USER_NAME
     * @see #DEFAULT_USER_DOMAIN
     */
    public WorkConfiguration() {
        this.workflowEndpoint = DEFAULT_WORKFLOW_ENDPOINT;
        this.echoEndpoint = DEFAULT_ECHO_ENDPOINT;
        this.echoClientId = System.getProperty("user.name", DEFAULT_USER_NAME) + "@" + DEFAULT_USER_DOMAIN;
    }
}
