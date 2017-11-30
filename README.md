## Linked Widget Platform (LWP)

### Prerequisites

* JDK 1.8+.
* Apache Maven 3+<sup>[1](#maven)</sup>.
* Apache Tomcat Server (tested with Tomcat 7, 8 and 9)<sup>[2](#tomcat)</sup>.

### LWP Installation Steps

1. Download the latest release of LWP for Intro of SW course from the release tab (v1.1.0-beta)
2. Make sure that all prerequisites software are installed correctly.
   * JDK check<sup>[3](#jdkcheck)</sup>
   * Maven check<sup>[4](#mvncheck)</sup>
   * (Optional) Tomcat user configuration for manager HTML<sup>[5](#userconfig)</sup>
3. Remove/rename the current `ROOT` folder in the apache tomcat server
   * We need the LWP UI to be in the root folder since it uses Google OAuth mechanism.​
4. Start your Tomcat server in the default port (`8080`). 
   * In case you can't use port `8080` for any reason, you'll need to customize the `config.properties` file inside your LWP UI (`<your-tomcat-installation-folder>/webapps/ROOT/config.properties`) and server (`<your-tomcat-installation-folder>/webapps/lw-server/config.properties`) - change `service` value's port into the correct one. 
   * However, these changes may cause Google related services (i.e., login and maps) to be disabled in your LWP installations.
5. Install/deploy the `lw-server.war` (LWP server) and `ROOT.war` (LWP UI) from the git release tab (v1.1.0-beta) in your tomcat webapp folder `<your-tomcat-installation-folder>/webapps/` or via tomcat manager GUI. 
6. Open http://localhost:8080 in your browser, and you should see an empty LWP isnstance.

### Example Widgets Installation Steps

1. Open `example-widgets` project (i.e., example-widgets folder of the distributed LWP) using your favorite IDE (or alternatively you can also go to the folder from your terminal)
2. Use maven to compile and install it (i.e., `mvn clean install`)
3. Install/deploy the generated .war file (e.g., `<your-project-folder>/target/example-widgets.war`) on your tomcat webapp folder, together with `ROOT` and `example-widgets` war files.
4. Reopen http://localhost:8080 in your browser, go to Widgets/Widget Collection/Mashup tab on the left, and you should be able to see a set of example widgets/collections/mashups (e.g., Turtle Loader, Turtle Loader with Param, Json Viewer, and RDF Merger).
5. You can modify, add widgets by modifying the `example-widgets` project and redo steps 1-4.
6. Have fun playing with these widgets!

### Widgets development notes

Linked Widgets (or simply widgets) can be classified into two types based on the environment that they’re executed on: Server Widgets and Client Widgets.

1. **Server Widget**. Server widget can be written in any programming language and executed as native applications on any platforms (e.g., PC, server, mobile devices, etc.) - as long as they’re accessible through the internet.
2. **Client Widget**. Widgets that are executed in the local context of a web browser environment. Intermediate and final data are deleted when the web browser closed.

Widgets can also classified based on their functions

1. **Data Widget**. For getting data from single / multiple data sources. It does not have any input node, only output node exists. It can be a client/server widget. 
2. **Processing Widget**. Typically used for processing data from data widgets and conducting processing of the data. It contains both input and output nodes. It can be client or server widgets. 
3. **Visualization Widget**. It is use for showing results in a web browser. It typically created as as client widgets

To develop a widget, you can start by taking a look on the following files: 

1. `<example-widgets-source-folder>/main/java/org/linkedwidgets/example/widget/server/ServerWidgetRegistry.java` for server widgets
   * Take a look on the classes that extends ServerWidgetJob class and the respective html files on `<example-widgets-folder>/webapp/html/server/`.
2. and `<example-widgets-source-folder>/main/java/org/linkedwidgets/example/widget/client/ClientWidgetRegistry.java` and their respective html/JS files for client widgets on `<example-widgets-folder>/webapp/html/client/`.

### Mashup Creation Steps

Please take a look on the Video 1-3 and 6 of the Introduction to LWP<sup>[6](#video)</sup>. Some of the explanation on the video might differ from what you will experience with the platform, as LWP is now being refactored quite heavily. Also note that we will not use any advanced features of semantic search and widget annotations (Video 4-5) for the assignment purposes.

As a final note, if you have any questions regarding the platform, please don't hesitate to contact us!

### Links
<a name="maven">1</a>: [Apache Maven website](https://maven.apache.org/)<br/>
<a name="tomcat">2</a>: [Apache Tomcat website](https://tomcat.apache.org/)<br/>
<a name="jdkcheck">3</a>: [How to tell if JRE or JDK is installed](https://stackoverflow.com/questions/22539779/how-to-tell-if-jre-or-jdk-is-installed)<br/>
<a name="mvncheck">4</a>: [Maven installation instructions](https://maven.apache.org/install.html)<br/>
<a name="userconfig">5</a>: [Tomcat users configuration example](https://examples.javacodegeeks.com/enterprise-java/tomcat/tomcat-users-xml-configuration-example/)<br/>
<a name="video">6</a>: [Introduction Video of LWP](http://bit.ly/2mPmDvF)<br/>
