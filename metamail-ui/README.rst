Welcome to Metamail
-------------------

What's Metamail?
****************

Metamail is an email analysis tool based on Hadoop. Metamail is composed of several parts:

    * **Metamail**. This is a collection of MapReduce jobs that retrieve email from a HBase database and compute different statistics.
    * **Database importer**. Imports emails, they should be stored into mbox format, into a HBase database.
    * **Metamail-ui**. Web application for visualizing the results obtained from the MapReduce jobs.

What statistics does it include?
********************************

Metamail includes the following statistics:

    * **Mails by size**
    * **Mails by thread length**
    * **Mails per day of the week**
    * **Mails per hour of the day**
    * **Mails per month**
    * **Mails per year**
    * **Mails received**
    * **Mails sent**

Can I use it for analyzing my own email?
****************************************

Yes. Metamail is a general-purporse tool and should work of any email dataset. Since it's based on Hadoop metamail can analyze large amounts of data.  However, notice that Metamail was built as a learning tool, so it's very likely you would need to tune some parts to make it work with your own dataset. Patches are welcome.

Some things to consider if you would like to use Metamail in your own organization:

    * First, use the database importer (enron-importer) to import your email dataset.
    * Build the MapReduce jobs and execute them. Check all jobs finish successfully.
    * Copy the results of the MapReduce jobs to the Web Tool. This step will require you to put the output in the right format (check the current .csv files). You can either do it manually or coding your own scripts.

What's the data Metamail comes with?
************************************

Metamail uses the 'Enron Email Dataset' as example data. This dataset, which is about 500MB, was made public and published to the web during the Enron trial. Many researchers use nowadays this dataset as sample data for email. The Enron Email Dataset can be found at http://www.cs.cmu.edu/~enron/.

Visualization
*************

The data used for visualization differs from the data obtained from the execution of the MapReduce jobs. This simplification was made to make visualization easier. For instance, mails by thread lengths may return thousands of threads many with just 1 email. That information is useless. In this case, the chart only show the Top 50 emails by thread length.

    * **Mails by size**. Mails by size grouped on intervals by 20KB (0-20KB, 20KB-40KB, etc). As almost all mails are on the 0-20KB interval, that interval was discarded (1 unit = 10 emails)
    * **Mails by thread length**. Top 50 mails by thread length
    * **Mails per day of the week**. From Monday-Sunday, mails on each day
    * **Mails per hour of the day**. From 0 hours to 23 hours, mails by hour
    * **Mails per month**. Only the emails received on year 2001 are shown
    * **Mails per year**
    * **Mails received**. Top 50 people who received more email
    * **Mails sent**. Top 50 people who sent more email

In all charts 1 unit = 1000 emails, except 'Mails by size'.

Contact
*******

If you have any request with regard to Metamail you can contact me at dpino@igalia.com.
If you would like to contribute with code please check the repo at github: https://github.com/dpino/Metamail.
