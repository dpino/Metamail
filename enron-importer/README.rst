Enron Email Importer
--------------------

This is a program for importing the Enron Email Dataset into a HBase database. You can download the Enron Email Dataset from http://www.cs.cmu.edu/~enron/enron_mail_20110402.tgz

This program works fine with the Enron Email Dataset. Once you have download and uncompressed the dataset, create a folder called 'data' and place the emails there. The structure should be as follows:

::

   + enron-importer 
       -+ data 
            -  DELETIONS.txt  
            -+ maildir

You can use this program to import your own dataset, although it may not fully work.

In order to run this program succesfully you also need to install HBase. The program has been tested successfully against 'HBase 0.90.6'.

Run it
------

Before running the program, check HBase is running:

::

    $ cd /usr/local/share/hbase/bin
    $ sudo ./start-hbase.sh


Now build the program with Maven:

::

    $ mvn clean install

Then run it:

::

    $ mvn exec:java -Dexec.mainClass="com.igalia.enron_importer.Main"
