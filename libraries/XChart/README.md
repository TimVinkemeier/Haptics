## XChart
A Simple Charting Library for Java

## Description
XChart is a light-weight and convenient library for plotting data.

Its focus is on simplicity and ease-of-use, requiring only two lines of code to save or display a basic default chart.

Usage is very simple: Create a Chart instance, add a series of data to it, and either save it or display it.

## Example

    double[] xData = new double[] { 0.0, 1.0, 2.0 };
    double[] yData = new double[] { 2.0, 1.0, 0.0 };

    // Create Chart
    Chart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

    // Show it
    new SwingWrapper(chart).displayChart();

    // Save it
    BitmapEncoder.savePNG(chart, "./Sample_Chart.png");
    
Now go ahead and [study some more examples](http://xeiam.com/xchart_examplecode.jsp), [download the thing](http://xeiam.com/xchart_changelog.jsp) and [provide feedback](https://github.com/timmolter/XChart/issues).

## Features
* No additional dependencies
* ~85KB Jar
* Line charts
* Scatter charts
* Area Charts
* Bar Charts
* Error bars
* Logarithmic axes
* Date and category X-Axis
* Multiple series
* Extensive customization
* Themes - XChart, GGPlot2, MATLAB
* Right-click, Save-As...
* User-defined axes range
* Custom legend placement
* CSV import and export

## Getting Started
### Non-Maven
Download Jar: http://xeiam.com/xchart_changelog.jsp

### Maven
The XChart release artifacts are hosted on Maven Central.

Add the XChart library as a dependency to your pom.xml file:

    <dependency>
        <groupId>com.xeiam.xchart</groupId>
        <artifactId>xchart</artifactId>
        <version>2.2.0</version>
    </dependency>

For snapshots, add the following to your pom.xml file:

    <repository>
      <id>sonatype-oss-snapshot</id>
      <snapshots/>
      <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
    
    <dependency>
      <groupId>com.xeiam</groupId>
      <artifactId>xchart</artifactId>
      <version>2.2.1-SNAPSHOT</version>
    </dependency>

## Building
mvn clean package  
mvn javadoc:aggregate  

## Running Demo
    cd /path/to/xchart-demo/jar/
    java -cp xchart-demo-2.2.0.jar:xchart-2.2.0.jar com.xeiam.xchart.demo.XChartDemo

## Bugs
Please report any bugs or submit feature requests to [XChart's Github issue tracker](https://github.com/timmolter/XChart/issues).  

## Continuous Integration
[![Build Status](https://travis-ci.org/timmolter/XChart.png?branch=develop)](https://travis-ci.org/timmolter/XChart.png)  
[Build History](https://travis-ci.org/timmolter/XChart/builds)  

## Donations
15MvtM8e3bzepmZ5vTe8cHvrEZg6eDzw2w  