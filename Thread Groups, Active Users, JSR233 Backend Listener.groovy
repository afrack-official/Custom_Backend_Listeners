import org.apache.jmeter.threads.JMeterContextService
import java.net.HttpURLConnection
import java.net.URL
import org.apache.jmeter.samplers.SampleResult

// You can use this code to testplan level to collect thread groups active users

// Configuration: InfluxDB details
def influxHost = "http://localhost:8086" // Replace with your InfluxDB host and port
def influxDBName = "jmeter"               // Replace with your database name
def token = "ewn8j_BpN4Cgo5QI5jAOVnEIg_vA0_dAYrEBq04ea81FkLZN0X7n_rzV4aQO11xqoMm6kCXNU06kZZYe3CdKTQ=="              // Your InfluxDB token

// Get the current thread group
def currentThreadGroup = ctx.getThreadGroup()

// Retrieve active threads for the specific thread group
def threadGroupThreads = currentThreadGroup.getNumberOfThreads() // Get the number of threads in the current thread group
def threadGroupName = currentThreadGroup.getName() // Get the current thread group name

// Create InfluxDB line protocol format data
String influxData = "jmeter_metrics,application=jmeter,thread_group=${threadGroupName} active_users=${threadGroupThreads}"

// Send the data to InfluxDB
try {
    // Construct the URL with the appropriate parameters
    URL url = new URL("${influxHost}/api/v2/write?org=loa&bucket=${influxDBName}")
    HttpURLConnection conn = (HttpURLConnection) url.openConnection()
    conn.setRequestMethod("POST")
    conn.setDoOutput(true)
    conn.setRequestProperty("Authorization", "Token ${token}")
    conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8")

    // Write data to output stream
    conn.getOutputStream().write(influxData.getBytes("UTF-8"))
    conn.getOutputStream().flush()
    conn.getOutputStream().close()

    // Check the response code
    int responseCode = conn.getResponseCode()
    if (responseCode == 204) {
        log.info("Custom metric sent to InfluxDB successfully for thread group: ${threadGroupName}")
    } else {
        log.warn("Failed to send data to InfluxDB. Response code: " + responseCode)
    }
} catch (Exception e) {
    log.error("Error while sending data to InfluxDB: " + e.message)
}