# Haptics - Hadoop performance testing in concurrent job scenarios
## Abstract
***

In today’s world, a massive amount of data is generated and stored every second. Not only created by humans, but also by machines, such as log files or sensor data.
Companies and research are interested in analysing this so-called “Big Data” and gaining insights which can be used to improve products, for example.
Big Data today means terabytes or petabytes of data which require huge parallelism to be stored and processed. Since in these scales of data thousands of machines are used,
efficiency of the computation is very important to reduce analysis times, energy consumption and therefore cost. With the invention of the MapReduce paradigm
and the creation of Apache Hadoop as an implementation of this paradigm, the Big Data world has obtained great tools that revolutionized the way huge amounts of data
can be analysed on large clusters of machines. Apache Hadoop as an open-source framework has seen significant contribution by large companies and research facilities.

But although Hadoop has been around for some years and is now widely used in research as well as the industry, much of its behaviour and performance factors are still unclear.
Hadoop has already been run on clusters with thousands of machines. Many different benchmarks have been created to measure various performance factors of Hadoop clusters.
Although the framework has shown to scale very well in performance when running a single large job like one of these benchmarks, we still don’t know exactly how efficiency scales
when running multiple jobs concurrently on large clusters, typical for today’s production applications.

With Haptics, we set out to create a tool that allows the execution of custom-designed tests, that can be specifically tailored to measure one performance factor at a time, if desired.
By using Haptics and applicable tests, we can hopefully find and understand the factors for concurrent job performance of Hadoop clusters.
With that knowledge, we can then identify and correct performance bottlenecks in Hadoop’s implementation and therefore increase its efficiency. 

## About
***

Haptics was created as part of my Computer Science Bachelor thesis. Although widely functional, it has only a prototype status and has not been tested thoroughly.
All aspects of Haptics, its architecture and information on how to use it can be found in my Bachelor thesis: http://sdrv.ms/Iu9obd

## License
***

Please refer to the LICENSE.txt file.
