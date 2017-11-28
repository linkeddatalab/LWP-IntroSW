## Linked Widget Platform (LWP)

### Prerequisites

* JDK 1.8+.
* Apache Maven 3+<sup>[1](#maven)</sup>.
* Apache Tomcat Server (tested with Tomcat 7, 8 and 9)<sup>[2](#tomcat)</sup>.

### LWP Installation Steps

1. Make sure that all prerequisites software are installed correctly. 
   * JDK check<sup>[3](#jdkcheck)</sup>
   * Maven check<sup>[4](#mvncheck)</sup>
   * (Optional) Tomcat user configuration for manager HTML<sup>[5](#userconfig)</sup>
2. Remove/rename the current `ROOT` folder in the apache tomcat server
   * We need the LWP UI to be in the root folder since it uses Google OAuth mechanism.​
3. Start your Tomcat server in the default port (`8080`). 
   * In case you can't use port `8080` for any reason, you'll need to customize the `config.properties` file inside your LWP UI (`<your-tomcat-installation-folder>/webapps/ROOT/config.properties`) and server (`<your-tomcat-installation-folder>/webapps/lw-server/config.properties`) - change `service` value's port into the correct one. 
   * However, these changes may cause Google related services (i.e., login and maps) to be disabled in your LWP installations.
4. Install/deploy the `ROOT.war` (LWP UI) and `lw-server.war` (LWP server) from the dist folder in your tomcat installation folder or via tomcat manager GUI.
5. Open http://localhost:8080 in your browser, and you should see an empty LWP instance.

### Example Widgets Installation Steps

1. Open `example-widgets` project (i.e., example-widgets folder of the distributed LWP) using your favorite IDE (or alternatively you can also go to the folder from your terminal)
2. Use maven to compile and install it (i.e., `mvn clean install`)
3. Install/deploy the generated .war file (e.g., `<your-project-folder>/target/example-widgets.war`) on your tomcat
4. Reopen http://localhost:8080 in your browser, go to Widgets tab on the left, and you should be able to see a set of example widgets (Turtle Loader, Turtle Loader with Param, Json Viewer, and RDF Merger).
5. Have fun playing with these widgets!

### Data Widget Creation Steps

*TBD*

### Process Widget Creation Steps

*TBD*

### Mashup Creation Steps

*TBD*

### Links
<a name="maven">1</a>: [Apache Maven website](https://maven.apache.org/)<br/>
<a name="tomcat">2</a>: [Apache Tomcat website](https://tomcat.apache.org/)<br/>
<a name="jdkcheck">2</a>: [How to tell if JRE or JDK is installed](https://stackoverflow.com/questions/22539779/how-to-tell-if-jre-or-jdk-is-installed)<br/>
<a name="mvncheck">2</a>: [https://maven.apache.org/install.html](https://maven.apache.org/install.html)<br/>
<a name="userconfig">2</a>: [tomcat users configuration example](https://examples.javacodegeeks.com/enterprise-java/tomcat/tomcat-users-xml-configuration-example/)<br/>
