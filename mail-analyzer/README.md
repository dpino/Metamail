-- Mail Analyzer

This is a mail analyzer based in Hadoop. Mail to be analyzed should be stored in <in> directory. 
Results are written in <out> directory.

Mail files should be stored in mbox format. Every file may have one or mail emails.:

-- Compile

$ mvn clean install

This generates a .jar file in target/

-- Run

$ hadoop jar ./target/metamail-0.0.1-SNAPSHOT.jar com.igalia.metamail.jobs.MessagesByTimePeriod

This job counts the number of mails by year, month, day, date and hour. Result is stored in mail/out. 
Before running the directory mail/out should not exist.

-- Dependencies

This tool uses Java Mail. Hadoop tries to find dependency libraries at /usr/share/hadoop/lib. Please, 
place a copy of Java Mail jar at that directory, otherwise you will get a NoClassDefFoundError at runtime.
